package com.sparta.slackservice.dto.response;

import com.sparta.slackservice.domain.SlackMessageStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class SendSlackMessageResDto {
    private UUID slackId;
    private String channelId;
    private String message;
    private SlackMessageStatus status;
    private String slackMessageTs;
    private LocalDateTime createdAt;
    private Long createdBy;
}
