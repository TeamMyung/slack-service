package com.sparta.slackservice.global.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.sparta.globalevent.event.SlackMessageEvent;
import com.sparta.slackservice.dto.request.SendOrderAINotifyReqDto;
import com.sparta.slackservice.service.SlackAIService;
import com.sparta.slackservice.service.SlackService;
import org.springframework.kafka.annotation.KafkaListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlackMessageConsumer {

	private final SlackAIService slackAIService;

	@KafkaListener(topics = "slack-notify", groupId = "slack-service-group")
	public void listen(SlackMessageEvent event) {
		log.info("[Kafka] Slack 알림 이벤트 수신: {}", event);

		try {
			SendOrderAINotifyReqDto dto = new SendOrderAINotifyReqDto();
			dto.setOrderId(event.getOrderId());
			dto.setCustomerName(event.getCustomerName());
			dto.setCustomerEmail(event.getCustomerEmail());
			dto.setProductName(event.getProductName());
			dto.setQuantity(event.getQuantity());
			dto.setRequest(event.getRequest());
			dto.setOrderTime(event.getOrderTime());
			dto.setStartHubName(event.getStartHubName());
			dto.setEndHubName(event.getEndHubName());
			dto.setEstimatedTime(event.getEstimatedTime());
			dto.setSlackAccountId(event.getSlackAccountId());
			dto.setDeliveryManagerName(event.getDeliveryManagerName());
			dto.setDeliveryManagerEmail(event.getDeliveryManagerEmail());

			log.info("Slack 알림 처리 완료: {}", event.getOrderId());
		} catch (Exception e) {
			log.error("Slack 알림 처리 중 오류 발생: {}", e.getMessage(), e);
		}
	}
}
