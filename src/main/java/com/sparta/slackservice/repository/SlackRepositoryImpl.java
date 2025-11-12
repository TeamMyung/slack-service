package com.sparta.slackservice.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.slackservice.domain.QSlack;
import com.sparta.slackservice.domain.Slack;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SlackRepositoryImpl implements SlackRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Slack> searchSlackMessages(String keyword, Pageable pageable) {
        QSlack s = QSlack.slack;

        List<Slack> content = queryFactory
                .selectFrom(s)
                .where(containsKeyword(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(s.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(s.count())
                .from(s)
                .where(containsKeyword(keyword))
                .fetchOne();

        long totalCount = total != null ? total : 0L;

        return new PageImpl<>(content, pageable, totalCount);
    }

    private BooleanExpression containsKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        return QSlack.slack.slackMessage.containsIgnoreCase(keyword);
    }
}
