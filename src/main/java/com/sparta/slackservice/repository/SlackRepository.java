package com.sparta.slackservice.repository;

import com.sparta.slackservice.domain.Slack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SlackRepository extends JpaRepository<Slack, UUID>, SlackRepositoryCustom {
}
