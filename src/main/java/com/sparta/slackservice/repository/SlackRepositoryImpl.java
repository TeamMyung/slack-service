package com.sparta.slackservice.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.slackservice.domain.QSlack;
import com.sparta.slackservice.domain.Slack;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SlackRepositoryImpl implements SlackRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Slack> searchSlackMessages(String keyword, Pageable pageable) {
        QSlack s = QSlack.slack;

        return queryFactory
                .selectFrom(s)
                .where(containsKeyword(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(s.createdAt.desc())
                .fetch();
    }

    @Override
    public long countSlackMessages(String keyword) {
        QSlack s = QSlack.slack;

        Long count = queryFactory
                .select(s.count())
                .from(s)
                .where(containsKeyword(keyword))
                .fetchOne();

        return count != null ? count : 0L;
    }

    private BooleanExpression containsKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        return QSlack.slack.slackMessage.containsIgnoreCase(keyword);
    }
}
