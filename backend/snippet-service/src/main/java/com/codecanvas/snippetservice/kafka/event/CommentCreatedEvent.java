package com.codecanvas.snippetservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreatedEvent {

    private UUID commentId;

    private UUID snippetId;

    private UUID snippetOwnerId;

    private UUID userId;

    private String content;

    private LocalDateTime createdAt;
}