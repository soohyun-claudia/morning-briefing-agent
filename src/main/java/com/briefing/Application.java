package com.briefing;

import com.briefing.agent.BriefingAgent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Morning Briefing Agent 애플리케이션 진입점
 *
 * <p>{@code @EnableScheduling} 을 통해 스케줄링 기능을 활성화한다.
 * {@link com.briefing.scheduler.BriefingScheduler} 가 매일 05:30에 자동으로 실행된다.
 *
 * <p>{@code CommandLineRunner} 는 테스트용으로,
 * 앱 실행 시 즉시 브리핑을 실행하여 동작을 확인할 수 있다.
 * 배포 후에는 {@code CommandLineRunner} 를 제거하고
 * {@link com.briefing.scheduler.BriefingScheduler} 만으로 운영한다.
 */
@EnableScheduling
@SpringBootApplication
public class Application {

	/**
	 * 애플리케이션 메인 메서드
	 *
	 * @param args 커맨드라인 인수
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * 앱 실행 시 즉시 브리핑을 실행하는 테스트용 Runner
	 *
	 * <p>배포 후 {@link com.briefing.scheduler.BriefingScheduler} 로 대체 예정
	 *
	 * @param briefingAgent 브리핑 전체 흐름을 조율하는 Agent
	 * @return 앱 시작 시 실행될 {@link CommandLineRunner}
	 */
	@Bean
	public CommandLineRunner run(BriefingAgent briefingAgent) {
		return args -> briefingAgent.run();
	}

}
