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
public class SnippetLikedEvent {

    private UUID snippetId;

    private UUID userId;

    private UUID snippetOwnerId;

    private Long likeCount;

    private LocalDateTime likedAt;
}