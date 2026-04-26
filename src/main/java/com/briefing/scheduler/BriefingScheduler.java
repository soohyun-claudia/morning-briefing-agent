package com.briefing.scheduler;

import com.briefing.agent.BriefingAgent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BriefingScheduler {

    private final BriefingAgent briefingAgent;

    public BriefingScheduler(BriefingAgent briefingAgent) {
        this.briefingAgent = briefingAgent;
    }

    @Scheduled(cron = "0 30 5 * * *") // 왼쪽부터 순서대로 초 분 시 일 월 요일
    public void scheduleBriefing() throws Exception {
        briefingAgent.run();
    }
}
