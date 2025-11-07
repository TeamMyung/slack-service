package com.sparta.slackservice.controller;

import com.sparta.slackservice.dto.SlackSendRequestDto;
import com.sparta.slackservice.dto.SlackSendResponseDto;
import com.sparta.slackservice.service.SlackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/slack")
public class SlackController {

    private final SlackService slackService;

    @PostMapping
    public ResponseEntity<SlackSendResponseDto> sendSlackMessage(@RequestBody SlackSendRequestDto request) {
        SlackSendResponseDto dto = slackService.sendSlackMessage(request.getSlackAccountId(), request.getText());
        return ResponseEntity.ok(dto);
    }
}

