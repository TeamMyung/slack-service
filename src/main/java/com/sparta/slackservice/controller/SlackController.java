package com.sparta.slackservice.controller;

import com.sparta.slackservice.dto.request.*;
import com.sparta.slackservice.dto.response.*;
import com.sparta.slackservice.global.config.ApiResponse;
import com.sparta.slackservice.service.SlackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/slacks")
public class SlackController {

    private final SlackService slackService;

    @PostMapping
    public ResponseEntity<ApiResponse<SendSlackMessageResDto>> sendSlackMessage(@RequestBody SendSlackMessageReqDto request) {
        SendSlackMessageResDto dto = slackService.sendSlackMessage(request.getSlackAccountId(), request.getText());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(dto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<GetSlackMessagesResDto>>> getSlackMessages(@ModelAttribute GetSlackMessagesReqDto request) {
        Page<GetSlackMessagesResDto> dto = slackService.getSlackMessages(request);
        return ResponseEntity.ok(new ApiResponse<>(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GetSlackMessageDetailResDto>> getSlackMessage(@PathVariable UUID id) {
        GetSlackMessageDetailResDto dto = slackService.getSlackMessageById(id);
        return ResponseEntity.ok(new ApiResponse<>(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateSlackMessageResDto>> updateSlackMessage(
            @PathVariable UUID id,
            @RequestBody UpdateSlackMessageReqDto request
    ) {
        UpdateSlackMessageResDto dto = slackService.updateSlackMessage(id, request);
        return ResponseEntity.ok(new ApiResponse<>(dto));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<DeleteSlackMessagesResDto>> deleteSlackMessages(@RequestBody DeleteSlackMessagesReqDto request) {
        DeleteSlackMessagesResDto dto = slackService.deleteSlackMessages(request);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(new ApiResponse<>(dto));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<SearchSlackMessagesResDto>>> searchSlackMessages(@ModelAttribute SearchSlackMessagesReqDto request) {
        Page<SearchSlackMessagesResDto> dto = slackService.searchSlackMessages(request);
        return ResponseEntity.ok(new ApiResponse<>(dto));
    }
}