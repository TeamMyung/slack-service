package com.sparta.slackservice.dto.request;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SendOrderAINotifyReqDto {
    private UUID orderId;
    private String customerName; // 수령 업체 담당자
    private String customerEmail;
    private String productName;
    private Integer quantity;
    private String request;
    private LocalDateTime orderTime; // 주문 승인 일자
    private String startHubName;
    private String endHubName;
    private Integer duration;
    private String slackAccountId; // 발송 허브 담당자
    private String deliveryManagerName; //배송 담당자 정보
    private String deliveryManagerEmail;
}
