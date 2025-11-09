package com.sparta.slackservice.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class deleteSlackMessagesReqDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MessageDeleteInfo {
        private UUID slackId;
        private String channelId;
        private String slackMessageTs;
    }

    private List<MessageDeleteInfo> messages;
}
