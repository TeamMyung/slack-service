package com.sparta.slackservice.dto.response;

import com.sparta.slackservice.domain.SlackMessageStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class getSlackMessagesResDto {
    private UUID slackId;
    private String slackAccountId;
    private String message;
    private SlackMessageStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}