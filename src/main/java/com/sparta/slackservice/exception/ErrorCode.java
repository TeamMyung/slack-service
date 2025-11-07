package com.sparta.slackservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 각자 도메인에 맞게 에러 코드 작성
    USER_NOT_FOUND(1001, HttpStatus.NOT_FOUND, "일치하는 회원 정보를 찾을 수 없습니다."),
    ;

    private final int code;
    private final HttpStatus status;
    private final String details;
}

