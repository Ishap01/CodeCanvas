package com.codecanvas.notificationservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyCreatedEvent {

    private UUID replyId;

    private UUID parentCommentId;

    private UUID commentOwnerId;

    private UUID userId;

    private String content;

    private LocalDateTime createdAt;
}
