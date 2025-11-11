package com.sparta.slackservice.service;

import com.sparta.slackservice.dto.request.SendOrderAINotifyReqDto;
import com.sparta.slackservice.dto.response.SendSlackMessageResDto;
import com.sparta.slackservice.global.exception.CustomException;
import com.sparta.slackservice.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SlackAIService {

    private final SlackService slackService;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient = WebClient.create();

    /**
     * 주문 정보를 기반으로 Gemini로 발송 시한을 계산하고 Slack으로 발송한다.
     */
    public void handleOrderNotification(SendOrderAINotifyReqDto dto) {
        // 프롬프트 생성
        String prompt = buildPrompt(dto);

        // Gemini API 호출
        String aiResult = callGemini(prompt);

        // Slack 메시지 생성
        String message = buildSlackMessage(dto, aiResult);

        // SlackService를 통해 전송 및 DB 저장
        slackService.sendSlackMessage(dto.getSlackAccountId(), message);
    }

    // 프롬프트 생성
    private String buildPrompt(SendOrderAINotifyReqDto dto) {
        return """
        아래는 물류 주문 정보입니다.
        주문시간의 날짜와 요청사항(납기일자), 발송지와 도착지 간의 거리 및 배송 소요 시간을 모두 고려하여, 언제까지 발송해야 납기일자에 맞춰 도착할 수 있는지 계산해주세요.
        
        [규칙]
        1. 반드시 한 문장으로만 답변하세요.
        2. 계산 근거, 이유, 설명은 포함하지 마세요.
        3. 근무시간(09~18시)을 초과하는 시간은 불가능합니다.
        4. 답변은 반드시 이 형식만 사용해야 합니다:
            "최종 발송 시한은 00월 OO일 오전/오후 OO시입니다."

        [거리 정보]
        발송지와 도착지 간 예상 소요시간: 약 %d시간
        
        [주문 정보]
        주문 번호: %s
        상품: %s %d개
        요청사항: %s
        주문시간: %s
        발송지: %s
        도착지: %s
        배송 담당자 근무시간: 09 - 18
        """.formatted(
                dto.getEstimatedTime(),
                dto.getOrderId(),
                dto.getProductName(), dto.getQuantity(),
                dto.getRequest(),dto.getOrderTime(),
                dto.getStartHubName(),
                dto.getEndHubName()
        );
    }

    // Gemini API 호출
    private String callGemini(String prompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiApiKey;

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
        );

        try {
            Map<String, Object> response = webClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (response == null || response.isEmpty()) {
                throw new CustomException(ErrorCode.AI_RESULT_NULL);
            }

            var candidates = (List<Map<String, Object>>) response.get("candidates");
            var content = (Map<String, Object>) candidates.get(0).get("content");
            var parts = (List<Map<String, Object>>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.AI_REQUEST_FAILED);
        }
    }

    // Slack 메시지 생성
    private String buildSlackMessage(SendOrderAINotifyReqDto dto, String aiResult) {
        return """
        📦 [발송 허브 알림]

        주문 번호: %s
        주문자: %s / %s
        상품: %s %d개
        요청사항: %s
        발송지: %s
        도착지: %s
        배송담당자: %s / %s

        %s
        """.formatted(
                dto.getOrderId(),
                dto.getCustomerName(), dto.getCustomerEmail(),
                dto.getProductName(), dto.getQuantity(),
                dto.getRequest(),
                dto.getStartHubName(), dto.getEndHubName(),
                dto.getDeliveryManagerName(), dto.getDeliveryManagerEmail(),
                aiResult
        );
    }
}
