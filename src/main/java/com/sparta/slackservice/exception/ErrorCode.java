package com.sparta.slackservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INVALID_SLACK_ACCOUNT_ID(10001, HttpStatus.BAD_REQUEST, "Slack Account ID는 필수 입력 값입니다."),
    SLACK_DM_OPEN_FAILED(10002, HttpStatus.BAD_REQUEST, "DM 채널을 열 수 없습니다."),
    SLACK_MESSAGE_SEND_FAILED(10003, HttpStatus.BAD_REQUEST, "Slack 메시지 전송에 실패했습니다."),
    SLACK_USER_NOT_ACCEPTED(10004, HttpStatus.BAD_REQUEST, "사용자가 DM을 받을 수 없습니다."),
    SLACK_USER_NOT_FOUND(10005, HttpStatus.NOT_FOUND, "Slack 사용자 ID를 찾을 수 없습니다."),
    ;

    private final int code;
    private final HttpStatus status;
    private final String details;
}

