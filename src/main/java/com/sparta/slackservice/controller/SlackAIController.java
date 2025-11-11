package com.sparta.slackservice.controller;

import com.sparta.slackservice.dto.request.SendOrderAINotifyReqDto;
import com.sparta.slackservice.dto.response.SendSlackMessageResDto;
import com.sparta.slackservice.global.config.ApiResponse;
import com.sparta.slackservice.service.SlackAIService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/slacks/api")
@RequiredArgsConstructor
public class SlackAIController {

    private final SlackAIService slackAIService;

    /**
     * 주문 발생 시 발송담당자에게 AI로 발송시한을 계산해 Slack 메시지를 전송한다.
     *
     * @param request 주문정보 요청 DTO
     * @return 전송 결과 DTO
     */
    @Operation(summary = "주문 발생 시 Slack 메시지 전송", description = "주문 발생 시 AI를 통해 최종 발송 시한을 생성해 Slack 메시지로 전송한다.")
    @PostMapping("/ai-notify")
    public ResponseEntity<ApiResponse<SendSlackMessageResDto>> sendOrderAINotify(@RequestBody SendOrderAINotifyReqDto request) {
        SendSlackMessageResDto dto = slackAIService.handleOrderNotification(request);
        return ResponseEntity.ok(new ApiResponse<>(dto));
    }
}
