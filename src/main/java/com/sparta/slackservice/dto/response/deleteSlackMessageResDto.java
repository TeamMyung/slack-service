package com.sparta.slackservice.dto.response;

import com.sparta.slackservice.domain.SlackMessageStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class deleteSlackMessageResDto {
    private UUID slackId;
    private SlackMessageStatus status;
    private LocalDateTime deletedAt;
}
