package com.sparta.slackservice.repository;

import com.sparta.slackservice.domain.SlackMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlackRepository extends JpaRepository<SlackMessage, Long> {
}
