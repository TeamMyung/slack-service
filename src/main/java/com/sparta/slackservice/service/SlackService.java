package com.sparta.slackservice.service;

import com.sparta.slackservice.domain.SlackMessage;
import com.sparta.slackservice.domain.SlackMessageStatus;
import com.sparta.slackservice.dto.SlackSendResponseDto;
import com.sparta.slackservice.exception.CustomException;
import com.sparta.slackservice.exception.ErrorCode;
import com.sparta.slackservice.repository.SlackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SlackService {

    private final SlackRepository slackRepository;

    @Value("${slack.bot.token}")
    private String slackBotToken;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://slack.com/api")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    public SlackSendResponseDto sendSlackMessage(String slackAccountId, String messageText) {
        // 유효성 검증
        if (slackAccountId == null || slackAccountId.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_SLACK_ACCOUNT_ID);
        }

        // DM 채널 열기
        Map<String, Object> openResp = webClient.post()
                .uri("/conversations.open")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + slackBotToken)
                .bodyValue(Map.of("users", slackAccountId))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (openResp == null) {
            throw new CustomException(ErrorCode.SLACK_DM_OPEN_FAILED);
        }

        boolean openOk = Boolean.TRUE.equals(openResp.get("ok"));
        if (!openOk) {
            String error = openResp.get("error") != null ? openResp.get("error").toString() : "unknown_error";
            switch (error) {
                case "user_not_found" -> throw new CustomException(ErrorCode.SLACK_USER_NOT_FOUND);
                default -> throw new CustomException(ErrorCode.SLACK_MESSAGE_SEND_FAILED);
            }
        }

        String channelId = (String) ((Map<String, Object>) openResp.get("channel")).get("id");
        if (channelId == null || channelId.isBlank()) {
            throw new CustomException(ErrorCode.SLACK_DM_OPEN_FAILED);
        }

        // 메시지 전송
        Map<String, Object> msgResp = webClient.post()
                .uri("/chat.postMessage")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + slackBotToken)
                .bodyValue(Map.of("channel", channelId, "text", messageText))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (msgResp == null) {
            throw new CustomException(ErrorCode.SLACK_MESSAGE_SEND_FAILED);
        }

        boolean success = Boolean.TRUE.equals(msgResp.get("ok"));
        if (!success) {
            String error = msgResp.get("error") != null ? msgResp.get("error").toString() : "unknown_error";
            switch (error) {
                case "user_not_found" -> throw new CustomException(ErrorCode.SLACK_USER_NOT_FOUND);
                default -> throw new CustomException(ErrorCode.SLACK_MESSAGE_SEND_FAILED);
            }
        }

        String ts = (String) msgResp.get("ts");

        // DB 저장
        SlackMessage saved = slackRepository.save(
                SlackMessage.builder()
                        .slackAccountId(slackAccountId)
                        .slackMessage(messageText)
                        .channelId(channelId)
                        .slackMessageTs(ts != null ? ts : "UNKNOWN_TS")
                        .status(SlackMessageStatus.CREATED)
                        .build()
        );

        // DTO 반환
        return SlackSendResponseDto.builder()
                .slackId(saved.getSlackId())
                .channelId(saved.getChannelId())
                .message(saved.getSlackMessage())
                .status(saved.getStatus())
                .slackMessageTs(saved.getSlackMessageTs())
                .createdAt(saved.getCreatedAt())
                .build();
    }
}