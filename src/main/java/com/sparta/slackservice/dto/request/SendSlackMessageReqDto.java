package com.sparta.slackservice.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SendSlackMessageReqDto {
        private String slackAccountId;
        private String text;
}
