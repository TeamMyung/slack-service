package com.sparta.slackservice.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class searchSlackMessagesReqDto {
    private String keyword;

    private int page;
    private int size;
    private String sortBy;
    private boolean isAsc;
}
