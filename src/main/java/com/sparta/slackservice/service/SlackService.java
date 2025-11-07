package com.sparta.slackservice.service;

import com.sparta.slackservice.domain.SlackMessage;
import com.sparta.slackservice.domain.SlackMessageStatus;
import com.sparta.slackservice.dto.request.getSlackMessagesReqDto;
import com.sparta.slackservice.dto.response.getSlackMessageDetailResDto;
import com.sparta.slackservice.dto.response.getSlackMessagesResDto;
import com.sparta.slackservice.dto.response.sendSlackMessageResDto;
import com.sparta.slackservice.exception.CustomException;
import com.sparta.slackservice.exception.ErrorCode;
import com.sparta.slackservice.repository.SlackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @Transactional
    public sendSlackMessageResDto sendSlackMessage(String slackAccountId, String messageText) {
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
        return sendSlackMessageResDto.builder()
                .slackId(saved.getSlackId())
                .channelId(saved.getChannelId())
                .message(saved.getSlackMessage())
                .status(saved.getStatus())
                .slackMessageTs(saved.getSlackMessageTs())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<getSlackMessagesResDto> getSlackMessages(getSlackMessagesReqDto reqDto) {
        // 허용 가능한 페이지 크기 목록
        List<Integer> allowedSizes = List.of(10, 30, 50);

        // 기본값(10)
        int size = allowedSizes.contains(reqDto.getSize()) ? reqDto.getSize() : 10;

        int page = Math.max(reqDto.getPage(), 1) - 1;

        // 기본 정렬 기준
        String sortBy = (reqDto.getSortBy() == null || reqDto.getSortBy().isBlank())
                ? "createdAt"
                : reqDto.getSortBy();

        Pageable pageable = PageRequest.of(
                page,
                size,
                reqDto.isAsc()
                        ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending()
        );

        Page<SlackMessage> messagePage = slackRepository.findAll(pageable);

        return messagePage.map(msg -> getSlackMessagesResDto.builder()
                .slackId(msg.getSlackId())
                .slackAccountId(msg.getSlackAccountId())
                .message(msg.getSlackMessage())
                .status(msg.getStatus())
                .createdAt(msg.getCreatedAt())
                .updatedAt(msg.getUpdatedAt())
                .build()
        );
    }

    @Transactional(readOnly = true)
    public getSlackMessageDetailResDto getSlackMessageById(UUID slackId) {
        SlackMessage message = slackRepository.findById(slackId)
                .orElseThrow(() -> new CustomException(ErrorCode.SLACK_MESSAGE_NOT_FOUND));

        return getSlackMessageDetailResDto.builder()
                .slackId(message.getSlackId())
                .slackAccountId(message.getSlackAccountId())
                .slackMessage(message.getSlackMessage())
                .channelId(message.getChannelId())
                .slackMessageTs(message.getSlackMessageTs())
                .status(message.getStatus())
                .createdAt(message.getCreatedAt())
                .createdBy(message.getCreatedBy())
                .updatedAt(message.getUpdatedAt())
                .updatedBy(message.getUpdatedBy())
                .deletedAt(message.getDeletedAt())
                .deletedBy(message.getDeletedBy())
                .build();
    }
}