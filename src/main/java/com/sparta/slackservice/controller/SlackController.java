package com.sparta.slackservice.controller;

import com.sparta.slackservice.dto.request.*;
import com.sparta.slackservice.dto.response.*;
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
    public ResponseEntity<SendSlackMessageResDto> sendSlackMessage(@RequestBody SendSlackMessageReqDto request) {
        SendSlackMessageResDto dto = slackService.sendSlackMessage(request.getSlackAccountId(), request.getText());
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<Page<GetSlackMessagesResDto>> getSlackMessage(@ModelAttribute GetSlackMessagesReqDto request) {
        Page<GetSlackMessagesResDto> dto = slackService.getSlackMessages(request);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetSlackMessageDetailResDto> getSlackMessage(@PathVariable UUID id) {
        GetSlackMessageDetailResDto dto = slackService.getSlackMessageById(id);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UpdateSlackMessageResDto> updateSlackMessage(
            @PathVariable UUID id,
            @RequestBody UpdateSlackMessageReqDto request
    ) {
        UpdateSlackMessageResDto dto = slackService.updateSlackMessage(id, request);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping
    public ResponseEntity<DeleteSlackMessagesResDto> deleteSlackMessages(@RequestBody DeleteSlackMessagesReqDto request) {
        DeleteSlackMessagesResDto dto = slackService.deleteSlackMessages(request);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<SearchSlackMessagesResDto>> searchSlackMessages(@ModelAttribute SearchSlackMessagesReqDto request) {
        Page<SearchSlackMessagesResDto> dto = slackService.searchSlackMessages(request);
        return ResponseEntity.ok(dto);
    }
}

