package com.sparta.slackservice.controller;

import com.sparta.slackservice.dto.request.*;
import com.sparta.slackservice.dto.response.*;
import com.sparta.slackservice.global.authz.Action;
import com.sparta.slackservice.global.authz.Authorize;
import com.sparta.slackservice.global.authz.Resource;
import com.sparta.slackservice.global.config.ApiResponse;
import com.sparta.slackservice.service.SlackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/slacks")
@Tag(name = "Slack 메시지 API", description = "Slack 메시지 전송 및 관리 관련 API")
public class SlackController {

    private final SlackService slackService;

    /**
     * Slack 메시지를 전송한다.
     *
     * @param request Slack 사용자 ID 및 메시지 내용을 포함한 요청 DTO
     * @return 전송 결과 DTO
     */
    @Operation(summary = "Slack 메시지 전송", description = "Slack 사용자에게 DM 메시지를 전송합니다.")
    @Authorize(resource = Resource.SLACK, action = Action.CREATE)
    @PostMapping
    public ResponseEntity<ApiResponse<SendSlackMessageResDto>> sendSlackMessage(@RequestBody SendSlackMessageReqDto request) {
        SendSlackMessageResDto dto = slackService.sendSlackMessage(request.getSlackAccountId(), request.getText());
        return ResponseEntity.ok(new ApiResponse<>(dto));
    }

    /**
     * Slack 메시지 목록을 페이징 조회한다.
     *
     * @param request 페이징 요청 DTO
     * @return 메시지 목록 DTO
     */
    @Operation(summary = "Slack 메시지 목록 조회", description = "Slack 메시지 목록을 페이징하여 조회합니다."
    )
    @Authorize(resource = Resource.SLACK, action = Action.READ)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<GetSlackMessagesResDto>>> getSlackMessages(@ModelAttribute GetSlackMessagesReqDto request) {
        Page<GetSlackMessagesResDto> dto = slackService.getSlackMessages(request);
        return ResponseEntity.ok(new ApiResponse<>(dto));
    }

    /**
     * Slack 메시지 상세 정보를 조회한다.
     *
     * @param id 메시지 UUID
     * @return 메시지 상세 정보 DTO
     */
    @Operation(summary = "Slack 메시지 상세 조회", description = "Slack 메시지의 상세 정보를 조회합니다.")
    @Authorize(resource = Resource.SLACK, action = Action.READ)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GetSlackMessageDetailResDto>> getSlackMessage(@PathVariable UUID id) {
        GetSlackMessageDetailResDto dto = slackService.getSlackMessageById(id);
        return ResponseEntity.ok(new ApiResponse<>(dto));
    }

    /**
     * Slack 메시지를 수정한다.
     *
     * @param id 메시지 UUID
     * @param request 수정 요청 DTO
     * @return 수정 결과 DTO
     */
    @Operation(summary = "Slack 메시지 수정", description = "Slack에 전송된 메시지 내용을 수정합니다.")
    @Authorize(resource = Resource.SLACK, action = Action.UPDATE)
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateSlackMessageResDto>> updateSlackMessage(
            @PathVariable UUID id,
            @RequestBody UpdateSlackMessageReqDto request
    ) {
        UpdateSlackMessageResDto dto = slackService.updateSlackMessage(id, request);
        return ResponseEntity.ok(new ApiResponse<>(dto));
    }

    /**
     * Slack 메시지를 삭제한다.
     *
     * @param request 삭제 요청 DTO
     * @return 삭제 결과 DTO
     */
    @Operation(summary = "Slack 메시지 삭제", description = "Slack 메시지를 삭제합니다."
    )
    @Authorize(resource = Resource.SLACK, action = Action.DELETE)
    @DeleteMapping
    public ResponseEntity<ApiResponse<DeleteSlackMessagesResDto>> deleteSlackMessages(@RequestBody DeleteSlackMessagesReqDto request) {
        DeleteSlackMessagesResDto dto = slackService.deleteSlackMessages(request);
        return ResponseEntity.ok(new ApiResponse<>(dto));
    }

    /**
     * Slack 메시지를 키워드로 검색한다.
     *
     * @param request 검색 요청 DTO
     * @return 검색 결과 DTO
     */
    @Operation(summary = "Slack 메시지 검색", description = "키워드로 Slack 메시지를 검색합니다.")
    @Authorize(resource = Resource.SLACK, action = Action.READ)
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<SearchSlackMessagesResDto>>> searchSlackMessages(@ModelAttribute SearchSlackMessagesReqDto request) {
        Page<SearchSlackMessagesResDto> dto = slackService.searchSlackMessages(request);
        return ResponseEntity.ok(new ApiResponse<>(dto));
    }
}