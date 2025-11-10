package com.sparta.slackservice.service;

import com.sparta.slackservice.client.SlackFeignClient;
import com.sparta.slackservice.domain.Slack;
import com.sparta.slackservice.domain.SlackMessageStatus;
import com.sparta.slackservice.dto.request.DeleteSlackMessagesReqDto;
import com.sparta.slackservice.dto.request.GetSlackMessagesReqDto;
import com.sparta.slackservice.dto.request.SearchSlackMessagesReqDto;
import com.sparta.slackservice.dto.request.UpdateSlackMessageReqDto;
import com.sparta.slackservice.dto.response.*;
import com.sparta.slackservice.global.exception.CustomException;
import com.sparta.slackservice.global.exception.ErrorCode;
import com.sparta.slackservice.repository.SlackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SlackService {

    private final SlackRepository slackRepository;
    private final SlackFeignClient slackFeignClient;

    @Value("${slack.bot.token}")
    private String slackBotToken;

    @Transactional
    public SendSlackMessageResDto sendSlackMessage(String slackAccountId, String messageText) {
        // 유효성 검증
        if (slackAccountId == null || slackAccountId.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_SLACK_ACCOUNT_ID);
        }

        // DM 채널 열기
        Map<String, Object> openResp = slackFeignClient.openConversation(
                "Bearer " + slackBotToken,
                Map.of("users", slackAccountId)
        );

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
        Map<String, Object> msgResp = slackFeignClient.postMessage(
                "Bearer " + slackBotToken,
                Map.of("channel", channelId, "text", messageText)
        );

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
        Slack saved = slackRepository.save(
                Slack.builder()
                        .slackAccountId(slackAccountId)
                        .slackMessage(messageText)
                        .channelId(channelId)
                        .slackMessageTs(ts != null ? ts : "UNKNOWN_TS")
                        .status(SlackMessageStatus.CREATED)
                        .build()
        );

        // DTO 반환
        return SendSlackMessageResDto.builder()
                .slackId(saved.getSlackId())
                .channelId(saved.getChannelId())
                .message(saved.getSlackMessage())
                .status(saved.getStatus())
                .slackMessageTs(saved.getSlackMessageTs())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<GetSlackMessagesResDto> getSlackMessages(GetSlackMessagesReqDto reqDto) {
        Pageable pageable = buildPageable(
                reqDto.getPage(),
                reqDto.getSize(),
                reqDto.getSortBy(),
                reqDto.isAsc());

        Page<Slack> messagePage = slackRepository.findAll(pageable);

        return messagePage.map(msg -> GetSlackMessagesResDto.builder()
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
    public GetSlackMessageDetailResDto getSlackMessageById(UUID slackId) {
        Slack message = slackRepository.findById(slackId)
                .orElseThrow(() -> new CustomException(ErrorCode.SLACK_MESSAGE_NOT_FOUND));

        return GetSlackMessageDetailResDto.builder()
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

    @Transactional
    public UpdateSlackMessageResDto updateSlackMessage(UUID slackId, UpdateSlackMessageReqDto request) {

        Slack message = slackRepository.findById(slackId)
                .orElseThrow(() -> new CustomException(ErrorCode.SLACK_USER_NOT_FOUND));

        // chat.update 호출
        Map<String, Object> response = slackFeignClient.updateMessage(
                "Bearer " + slackBotToken,
                Map.of(
                        "channel", request.getChannelId(),
                        "ts", request.getSlackMessageTs(),
                        "text", request.getNewText()
                )
        );

        if (response == null || !Boolean.TRUE.equals(response.get("ok"))) {
            throw new CustomException(ErrorCode.SLACK_MESSAGE_SEND_FAILED);
        }

        // DB 반영
        message.updateMessage(request.getNewText());

        // DTO 반환
        return UpdateSlackMessageResDto.builder()
                .slackId(message.getSlackId())
                .channelId(message.getChannelId())
                .message(message.getSlackMessage())
                .status(message.getStatus())
                .slackMessageTs(message.getSlackMessageTs())
                .updatedAt(message.getUpdatedAt())
                .build();
    }

    // 단건, 다건 삭제
    @Transactional
    public DeleteSlackMessagesResDto deleteSlackMessages(DeleteSlackMessagesReqDto request) {

        List<DeleteSlackMessagesResDto.DeletedMessageInfo> deletedList = new ArrayList<>();

        for (DeleteSlackMessagesReqDto.MessageDeleteInfo msg : request.getMessages()) {

            Slack message = slackRepository.findById(msg.getSlackId())
                    .orElseThrow(() -> new CustomException(ErrorCode.SLACK_USER_NOT_FOUND));

            // Slack API 호출
            Map<String, Object> response = slackFeignClient.deleteMessage(
                    "Bearer " + slackBotToken,
                    Map.of(
                            "channel", msg.getChannelId(),
                            "ts", msg.getSlackMessageTs()
                    )
            );

            if (response == null || !Boolean.TRUE.equals(response.get("ok"))) {
                throw new CustomException(ErrorCode.SLACK_MESSAGE_SEND_FAILED);
            }

            // Soft delete
            message.markAsDeleted();

            // 결과 리스트에 추가
            deletedList.add(
                    DeleteSlackMessagesResDto.DeletedMessageInfo.builder()
                            .slackId(message.getSlackId())
                            .status(message.getStatus())
                            .deletedAt(message.getDeletedAt())
                            .build()
            );
        }

        return DeleteSlackMessagesResDto.builder()
                .deletedMessages(deletedList)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<SearchSlackMessagesResDto> searchSlackMessages(SearchSlackMessagesReqDto req) {
        Pageable pageable = buildPageable(req.getPage(), req.getSize(), req.getSortBy(), req.isAsc());
        Page<Slack> resultPage = slackRepository.searchSlackMessages(req.getKeyword(), pageable);

        return resultPage.map(m -> SearchSlackMessagesResDto.builder()
                .slackId(m.getSlackId())
                .slackAccountId(m.getSlackAccountId())
                .slackMessage(m.getSlackMessage())
                .status(m.getStatus())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build());
    }

    private Pageable buildPageable(int page, int size, String sortBy, boolean isAsc) {
        // 허용 가능한 페이지 크기 목록
        List<Integer> allowedSizes = List.of(10, 30, 50);
        // 기본값(10)
        int validSize = allowedSizes.contains(size) ? size : 10;

        int validPage = Math.max(page, 1) - 1;

        // 기본 정렬 기준
        String validSortBy = (sortBy == null || sortBy.isBlank()) ? "createdAt" : sortBy;

        Sort sort = isAsc
                ? Sort.by(validSortBy).ascending()
                : Sort.by(validSortBy).descending();

        return PageRequest.of(validPage, validSize, sort);
    }
}