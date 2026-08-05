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
public class CommentCreatedEvent {

    private UUID commentId;

    private UUID snippetId;

    private UUID snippetOwnerId;

    private UUID userId;

    private String content;

    private LocalDateTime createdAt;
}
