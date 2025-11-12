package com.sparta.slackservice.global.kafka;

import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class KafkaLogger {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@PostConstruct
	public void init() {
		System.out.println("### 현재 Kafka 설정: " + bootstrapServers);
	}
}
