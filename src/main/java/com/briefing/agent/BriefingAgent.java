package com.briefing.agent;

import com.briefing.agent.dto.CalendarEvent;
import com.briefing.service.BriefingGeneratorService;
import com.briefing.service.MailService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 아침 브리핑 전체 흐름을 조율하는 Single Agent (Orchestrator)
 *
 * <p>각 컴포넌트를 순서대로 호출하여 브리핑 프로세스를 실행한다.
 *
 * <pre>
 * CalendarAgent (일정 수집)
 *       ↓
 * BriefingGeneratorService (브리핑 생성)
 *       ↓
 * MailService (이메일 발송)
 * </pre>
 *
 * <p>현재는 Single Agent 구조이며, 추후 Multi-Agent로 확장 예정이다.
 * 2단계에서 NewsAgent, WeatherAgent 등이 추가되면
 * 이 클래스가 Orchestrator 역할을 담당한다.
 */
@Component
public class BriefingAgent {

    /** 오늘의 캘린더 일정을 수집하는 Agent */
    private final CalendarAgent calendarAgent;
    /** Claude API를 호출하여 브리핑 텍스트를 생성하는 서비스 */
    private final BriefingGeneratorService briefingGeneratorService;
    /** 브리핑 이메일을 발송하는 서비스 */
    private final MailService mailService;

    /**
     * 의존성을 생성자 주입으로 받는다.
     *
     * @param calendarAgent            오늘의 캘린더 일정을 수집하는 Agent
     * @param briefingGeneratorService Claude API로 브리핑 텍스트를 생성하는 서비스
     * @param mailService              브리핑 이메일을 발송하는 서비스
     */
    public BriefingAgent(CalendarAgent calendarAgent,
                         BriefingGeneratorService briefingGeneratorService,
                         MailService mailService) {
        this.calendarAgent = calendarAgent;
        this.briefingGeneratorService = briefingGeneratorService;
        this.mailService = mailService;
    }

    /**
     * 브리핑 프로세스를 실행한다.
     *
     * <p>처리 흐름:
     * <ol>
     *   <li>CalendarAgent로 오늘 일정 수집</li>
     *   <li>BriefingGeneratorService로 브리핑 텍스트 생성</li>
     *   <li>MailService로 이메일 발송</li>
     * </ol>
     *
     * @throws Exception 일정 수집, 브리핑 생성, 이메일 발송 중 오류 발생 시
     */
    public void run() throws Exception {
        List<CalendarEvent> events = calendarAgent.getTodayEvents();
        System.out.println("=== 오늘 일정 ===");
        events.forEach(event -> System.out.println(event.startTime() + " " + event.title()));

        System.out.println("\n=== 브리핑 ===");
        String briefing = briefingGeneratorService.generateBriefing(events);
        System.out.println(briefing);

        System.out.println("\n=== 이메일 발송 ===");
        mailService.sendBriefing(briefing);
        System.out.println("이메일 발송 완료!");
    }
}
