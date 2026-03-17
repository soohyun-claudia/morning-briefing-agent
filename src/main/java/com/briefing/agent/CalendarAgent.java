package com.briefing.agent;

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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

/**
 * 각 단계 핵심 훑기
 * GoogleClientSecrets.load : credentials.json 파일 읽기
 * GoogleAuthorizationCodeFlow : OAuth 인증 흐름 설정
 * CALENDAR_READONLY : 캘린더 읽기 권한만 요청
 * FileDAtaStoreFactory : 토큰을 tokens 폴더에 저장
 * LocalSErverReceiver : 브라우저 로그인 후 토큰 받는 로컬 서버
 * Calendar.Builder : 실제 API 호출할 수 있는 객체 생성
 */

@Component
public class CalendarAgent {


    @Value("${google.calendar.credentials-path}")
    private String credentialsPath;

    @Value("${google.calendar.tokens-path}")
    private String tokensPath;

    @Value("${google.calendar.calendar-id}")
    private String calendarId;

    // credentials.json 경로 (application.yml에서 읽어올 거예요)
    private static final String APPLICATION_NAME = "morning-briefing-agent";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    // Google Calendar 서비스 객체

    // 오늘 일정 가져오는 메서드
    public List<String> getTodayEvents() throws Exception{
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

        // 일정 제목만 추출해서 반환
        return events.stream()
                .map(Event::getSummary)
                .toList();
    }

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
