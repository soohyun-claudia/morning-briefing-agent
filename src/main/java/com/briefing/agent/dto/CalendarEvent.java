package com.briefing.agent.dto;

import java.time.ZonedDateTime;

/**
 * Google Calendar에서 수집한 일정 정보를 담는 DTO
 *
 * <p>Java 16+의 {@code record} 타입으로 선언되어 불변(immutable) 객체이다.
 * getter, equals, hashCode, toString이 자동으로 생성된다.
 *
 * <p>사용 예시:
 * <pre>
 * CalendarEvent event = new CalendarEvent("BESA 강의", ZonedDateTime.now());
 * event.title();      // "BESA 강의"
 * event.startTime();  // ZonedDateTime 객체
 * </pre>
 *
 * <p>시작 시간을 {@link ZonedDateTime}으로 저장하는 이유:
 * {@code LocalDateTime}은 시간대 정보가 없어 AWS EC2 배포 시
 * 서버 시간대(UTC)와 한국 시간(KST) 간 9시간 차이가 발생할 수 있다.
 * {@code ZonedDateTime}은 시간대 정보를 포함하여 어느 환경에서도 정확한 시간을 보장한다.
 *
 * @param title     일정 제목
 * @param startTime 일정 시작 시간 (시스템 시간대 적용)
 */
public record CalendarEvent(String title, ZonedDateTime startTime) {
}
