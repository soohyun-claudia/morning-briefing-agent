package com.briefing;

import com.briefing.agent.CalendarAgent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner run(CalendarAgent calendarAgent) {
		return args ->{
			System.out.println("=== 오늘 일정 ===");
			calendarAgent.getTodayEvents()
					.forEach(System.out::println);
		};
	}

}
