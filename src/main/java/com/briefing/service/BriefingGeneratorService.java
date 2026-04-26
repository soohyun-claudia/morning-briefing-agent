package com.briefing.service;

import com.briefing.agent.dto.CalendarEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";

    @Value("${anthropic.api-key}")
    private String apiKey;

    public String generateBriefing(List<CalendarEvent> events) throws Exception{
        // 1. 프롬프트 만들기
        StringBuilder prompt = new StringBuilder();
        prompt.append("오늘 일정은 다음과 같습니다:\n");

        for (CalendarEvent event : events) {
            prompt.append("- ")
                    .append(event.startTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                    .append(" ")
                    .append(event.title())
                    .append("\n");
        }

        prompt.append("위 일정을 바탕으로 하루를 브리핑해주세요.");
        // 2. Claude api 호출하기
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", "2023-06-01");

        Map<String, Object> body = new HashMap<>();
        body.put("model", "claude-sonnet-4-6");
        body.put("max_tokens", 1024);

        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt.toString());

        body.put("messages", List.of(message));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // post 요청
        ResponseEntity<String> response = restTemplate.postForEntity(
                CLAUDE_API_URL, requestEntity, String.class
        );


        // 3. 응답에서 텍스트 추출하기
        String responseBody = response.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(responseBody);
        String briefingText = root.path("content")
                .get(0)
                .path("text")
                .asText();

        return briefingText;

    }
}
