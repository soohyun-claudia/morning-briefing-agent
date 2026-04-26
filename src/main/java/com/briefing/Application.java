package com.briefing;

import com.briefing.agent.CalendarAgent;
import com.briefing.agent.dto.CalendarEvent;
import com.briefing.service.BriefingGeneratorService;
import com.briefing.service.MailService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.List;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner run(CalendarAgent calendarAgent, BriefingGeneratorService briefingGeneratorService, JavaMailSenderImpl mailSender, MailService mailService) {
		return args -> {
			List<CalendarEvent> events = calendarAgent.getTodayEvents();
			System.out.println("=== 오늘 일정 ===");
			events.forEach(event -> System.out.println(event.startTime() + " " + event.title()));

			System.out.println("\n=== 브리핑 ===");
			String briefing = briefingGeneratorService.generateBriefing(events);
			System.out.println(briefing);

			System.out.println("\n=== 이메일 발송 ===");
			mailService.sendBriefing(briefing);
			System.out.println("이메일 발송 완료!");
		};
	}

}
