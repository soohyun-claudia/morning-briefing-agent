package com.briefing.scheduler;

import com.briefing.agent.BriefingAgent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 매일 아침 브리핑을 자동으로 실행하는 스케줄러
 *
 * <p>{@code @Scheduled} 를 사용하여 매일 05:30에 {@link BriefingAgent}를 실행한다.
 *
 * <p>cron 표현식 형식:
 * <pre>
 * 0  30  5  *  *  *
 * │   │  │  │  │  │
 * │   │  │  │  │  └── 요일 (* = 매일)
 * │   │  │  │  └───── 월 (* = 매월)
 * │   │  │  └──────── 일 (* = 매일)
 * │   │  └─────────── 시 (5 = 5시)
 * │   └────────────── 분 (30 = 30분)
 * └────────────────── 초 (0 = 0초)
 * </pre>
 *
 * <p>스케줄러가 동작하려면 {@code @EnableScheduling} 이 활성화되어 있어야 한다.
 * {@link com.briefing.Application} 에 {@code @EnableScheduling} 이 선언되어 있다.
 */
@Component
public class BriefingScheduler {

    /** 브리핑 전체 흐름을 조율하는 Agent */
    private final BriefingAgent briefingAgent;

    /**
     * BriefingAgent를 생성자 주입으로 받는다.
     *
     * @param briefingAgent 브리핑 전체 흐름을 조율하는 Agent
     */
    public BriefingScheduler(BriefingAgent briefingAgent) {
        this.briefingAgent = briefingAgent;
    }

    /**
     * 매일 05:30에 브리핑 프로세스를 실행한다.
     *
     * <p>{@link BriefingAgent#run()} 을 호출하여
     * 일정 수집 → 브리핑 생성 → 이메일 발송 순으로 처리한다.
     *
     * @throws Exception 브리핑 실행 중 오류 발생 시
     */
    @Scheduled(cron = "0 30 5 * * *") // 왼쪽부터 순서대로 초 분 시 일 월 요일
    public void scheduleBriefing() throws Exception {
        briefingAgent.run();
    }
}
