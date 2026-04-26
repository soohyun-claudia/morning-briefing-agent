package com.briefing.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Claude API를 호출하여 브리핑 텍스트를 생성하는 서비스
 *
 * <p>{@code @Component}를 붙이는 이유:
 * Spring이 이 클래스를 Bean으로 등록하여 관리하게 하기 위함.
 * 직접 객체를 생성하지 않고 Spring이 자동으로 주입해준다.
 *
 * <pre>
 * // 직접 생성 (X)
 * BriefingGeneratorService briefingGeneratorService = new BriefingGeneratorService();
 *
 * // Spring 주입 (O)
 * private final BriefingGeneratorService briefingGeneratorService;
 * </pre>
 *
 * <p>CalendarAgent에서 받은 오늘 일정 목록을 Claude API에 전달하여
 * 브리핑 텍스트를 생성하고 반환한다.
 */
@Component
public class BriefingGeneratorService {

    @Value("${anthropic.api-key")
    private String apiKey;

    public String generateBriefing(List<String> events) {
        // 1. 프롬프트 만들기

        // 2. Claude api 호출하기

        // 3. 응답에서 텍스트 추출하기

        return null;

    }
}
