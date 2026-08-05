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
public class SnippetLikedEvent {

    private UUID snippetId;

    private UUID userId;

    private UUID snippetOwnerId;

    private Long likeCount;

    private LocalDateTime likedAt;
}
