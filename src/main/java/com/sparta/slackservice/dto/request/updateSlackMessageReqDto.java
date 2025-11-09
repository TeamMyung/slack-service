package com.sparta.slackservice.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class updateSlackMessageReqDto {
    private String channelId;
    private String slackMessageTs;
    private String newText;
}
