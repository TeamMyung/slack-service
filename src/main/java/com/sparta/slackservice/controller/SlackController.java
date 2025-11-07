package com.sparta.slackservice.controller;

import com.sparta.slackservice.dto.request.sendSlackMessageReqDto;
import com.sparta.slackservice.dto.response.sendSlackMessageResDto;
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
    public ResponseEntity<sendSlackMessageResDto> sendSlackMessage(@RequestBody sendSlackMessageReqDto request) {
        sendSlackMessageResDto dto = slackService.sendSlackMessage(request.getSlackAccountId(), request.getText());
        return ResponseEntity.ok(dto);
    }
}

