package com.sparta.slackservice.repository;

import com.sparta.slackservice.domain.Slack;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SlackRepositoryCustom {
    Page<Slack> searchSlackMessages(String keyword, Pageable pageable);
}
