package com.sparta.slackservice.repository;

import com.sparta.slackservice.domain.Slack;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SlackRepositoryCustom {

    List<Slack> searchSlackMessages(String keyword, Pageable pageable);

    long countSlackMessages(String keyword);
}
