package com.sparta.slackservice.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class deleteSlackMessageReqDto {
    private String channelId;
    private String slackMessageTs;
}

