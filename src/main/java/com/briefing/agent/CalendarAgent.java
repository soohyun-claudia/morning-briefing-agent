package com.briefing.agent;

import com.briefing.agent.dto.CalendarEvent;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Google Calendar API를 호출하여 오늘의 일정을 수집하는 Agent
 *
 * <p>Google OAuth 2.0 인증을 통해 사용자의 캘린더에 접근하고,
 * 오늘 하루의 일정을 {@link CalendarEvent} 목록으로 반환한다.
 *
 * <p>주요 컴포넌트:
 * <ul>
 *   <li>{@code GoogleClientSecrets} : credentials.json 파일을 읽어 앱 인증 정보 로드</li>
 *   <li>{@code GoogleAuthorizationCodeFlow} : OAuth 2.0 인증 흐름 설정</li>
 *   <li>{@code CalendarScopes.CALENDAR_READONLY} : 캘린더 읽기 권한만 요청</li>
 *   <li>{@code FileDataStoreFactory} : 발급받은 토큰을 tokens 폴더에 저장 (재로그인 방지)</li>
 *   <li>{@code LocalServerReceiver} : 브라우저 로그인 후 토큰을 받는 로컬 서버</li>
 *   <li>{@code Calendar.Builder} : 실제 API 호출할 수 있는 Calendar 객체 생성</li>
 * </ul>
 */
@Component
public class CalendarAgent {

    /** application.yml에서 주입받는 Google OAuth 인증 파일 경로 */
    @Value("${google.calendar.credentials-path}")
    private String credentialsPath;

    /** application.yml에서 주입받는 OAuth 토큰 저장 폴더 경로 */
    @Value("${google.calendar.tokens-path}")
    private String tokensPath;

    /** application.yml에서 주입받는 조회할 캘린더 ID */
    @Value("${google.calendar.calendar-id}")
    private String calendarId;

    /** Google API 요청 시 사용할 애플리케이션 이름 */
    private static final String APPLICATION_NAME = "morning-briefing-agent";

    /** Google API JSON 직렬화/역직렬화에 사용할 JsonFactory */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /**
     * 오늘 하루의 캘린더 일정을 조회하여 반환한다.
     *
     * <p>오늘 00:00:00 부터 오늘 23:59:59.999 까지의 일정을 시작 시간 순으로 조회한다.
     * 종일 일정의 경우 시작 시간을 00:00:00 으로 설정한다.
     *
     * @return 오늘의 캘린더 일정 목록 ({@link CalendarEvent} 리스트)
     * @throws Exception Google Calendar API 호출 실패 또는 인증 실패 시
     */
    public List<CalendarEvent> getTodayEvents() throws Exception{
        // 1. Calendar 객체 가져오기
        Calendar service = getCalendarService();

        // 2. 시간 범위 지정 (오늘 시작 시간 ~ 오늘 끝 시간 범위 설정
        ZoneId zone = ZoneId.systemDefault();
        // 오늘 시작 (00:00:00)
        DateTime start = new DateTime(LocalDate.now().atStartOfDay(zone).toInstant().toEpochMilli());

        // 오늘 끝 (다음날 00:00:00 - 1ms)
        DateTime end = new DateTime(LocalDate.now().plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1);

        // Google Calendar API로 일정 조회
        List<Event> events = service.events().list(calendarId)
                .setTimeMin(start)
                .setTimeMax(end)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute()
                .getItems();

        // Event → CalendarEvent DTO로 변환하여 반환
        return events.stream()
                .map(event -> new CalendarEvent(
                    event.getSummary(),
                    extractStartTime(event)
                ))
                .toList();
    }

    /**
     * Google Calendar Event에서 시작 시간을 추출하여 {@link ZonedDateTime}으로 변환한다.
     *
     * <p>Google Calendar 일정은 두 가지 형태로 시작 시간을 가진다:
     * <ul>
     *   <li>시간 지정 일정: {@code getDateTime()} 으로 밀리초 단위 시간 반환</li>
     *   <li>종일 일정: {@code getDate()} 로 날짜만 반환 → 해당 날짜 00:00:00 으로 설정</li>
     * </ul>
     *
     * @param event Google Calendar API의 Event 객체
     * @return 시스템 시간대(한국)가 적용된 {@link ZonedDateTime}
     */
    private ZonedDateTime extractStartTime(Event event) {
        if (event.getStart().getDateTime() != null) {
            return ZonedDateTime.ofInstant(
                    Instant.ofEpochMilli(event.getStart().getDateTime().getValue()),
                    ZoneId.systemDefault()
                    // 일정 시작 정보 꺼내와서 밀리초(long)으로 변환하고
                    // 이거를 다시 java instant로 변환(시간의 절대적인 순간)한 후에
                    // ZonedDateTime으로 변환 후 내 시스템 시간대(한국)으로 설정
            );
        }
        return LocalDate.parse(event.getStart().getDate().toStringRfc3339())
                .atStartOfDay(ZoneId.systemDefault());
    }

    /**
     * Google Calendar API 서비스 객체를 생성하여 반환한다.
     *
     * <p>처음 실행 시 브라우저를 통해 Google OAuth 로그인을 진행하고,
     * 발급된 토큰을 tokens 폴더에 저장한다.
     * 이후 실행 시에는 저장된 토큰을 재사용하여 자동으로 인증한다.
     *
     * @return 인증이 완료된 {@link Calendar} 서비스 객체
     * @throws Exception 인증 파일 로드 실패 또는 OAuth 인증 실패 시
     */
    private Calendar getCalendarService() throws Exception {

        // 1. credentials.json을 읽어야 함
        //InputStream in = new FileInputStream(credentialsPath);
        InputStream in = getClass().getResourceAsStream("/credentials.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // 2. OAuth 인증 처리
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), JSON_FACTORY, clientSecrets,
                Collections.singletonList(CalendarScopes.CALENDAR_READONLY))
                .setDataStoreFactory(new FileDataStoreFactory(new File(tokensPath)))
                .setAccessType("offline")
                .build();

        // 3. 로컬 서버로 인증 처리 + 토큰 저장
        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(8888)
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver)
                .authorize("user");

        // 4. Calendar 객체 반환
        return new Calendar.Builder(new NetHttpTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
