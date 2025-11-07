package com.sparta.slackservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SlackSendRequestDto {
        private String slackAccountId;
        private String text;
        private String channelId;
}
