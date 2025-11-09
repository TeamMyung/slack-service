package com.sparta.slackservice.controller;

import com.sparta.slackservice.dto.request.getSlackMessagesReqDto;
import com.sparta.slackservice.dto.request.sendSlackMessageReqDto;
import com.sparta.slackservice.dto.request.updateSlackMessageReqDto;
import com.sparta.slackservice.dto.response.getSlackMessageDetailResDto;
import com.sparta.slackservice.dto.response.getSlackMessagesResDto;
import com.sparta.slackservice.dto.response.sendSlackMessageResDto;
import com.sparta.slackservice.dto.response.updateSlackMessageResDto;
import com.sparta.slackservice.service.SlackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/slacks")
public class SlackController {

    private final SlackService slackService;

    @PostMapping
    public ResponseEntity<sendSlackMessageResDto> sendSlackMessage(@RequestBody sendSlackMessageReqDto request) {
        sendSlackMessageResDto dto = slackService.sendSlackMessage(request.getSlackAccountId(), request.getText());
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<Page<getSlackMessagesResDto>> getSlackMessage(@ModelAttribute getSlackMessagesReqDto request) {
        Page<getSlackMessagesResDto> dto = slackService.getSlackMessages(request);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<getSlackMessageDetailResDto> getSlackMessage(@PathVariable UUID id) {
        getSlackMessageDetailResDto dto = slackService.getSlackMessageById(id);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<updateSlackMessageResDto> updateSlackMessage(
            @PathVariable UUID id,
            @RequestBody updateSlackMessageReqDto request
    ) {
        updateSlackMessageResDto dto = slackService.updateSlackMessage(id, request);
        return ResponseEntity.ok(dto);
    }
}

