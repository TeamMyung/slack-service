package com.sparta.slackservice.dto.response;

import com.sparta.slackservice.domain.SlackMessageStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class DeleteSlackMessagesResDto {

    private List<DeletedMessageInfo> deletedMessages;

    @Getter
    @Builder
    public static class DeletedMessageInfo {
        private UUID slackId;
        private SlackMessageStatus status;
        private LocalDateTime deletedAt;
    }
}
