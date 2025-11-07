package com.sparta.slackservice.domain;

import brave.internal.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_slacks")
public class SlackMessage extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID slackId;

    @Column(nullable = false)
    private String slackAccountId;

    @Column(columnDefinition = "TEXT")
    private String slackMessage;

    @Column(nullable = false)
    private String channelId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SlackMessageStatus status;

    @Column(nullable = false)
    private String slackMessageTs;
}
