package com.briefing.agent;

import com.briefing.agent.dto.CalendarEvent;
import com.briefing.service.BriefingGeneratorService;
import com.briefing.service.MailService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BriefingAgent {

    // CalendarAgent, BriefingGeneratorService, MailService 주입
    private final CalendarAgent calendarAgent;
    private final BriefingGeneratorService briefingGeneratorService;
    private final MailService mailService;

    public BriefingAgent(CalendarAgent calendarAgent,
                         BriefingGeneratorService briefingGeneratorService,
                         MailService mailService) {
        this.calendarAgent = calendarAgent;
        this.briefingGeneratorService = briefingGeneratorService;
        this.mailService = mailService;
    }

    // 브리핑 실행 메서드
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
