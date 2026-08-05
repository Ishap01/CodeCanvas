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
public class SnippetForkedEvent {

    private UUID originalSnippetId;

    private UUID forkedSnippetId;

    private UUID originalOwnerId;

    private UUID forkedBy;

    private Long forkCount;

    private LocalDateTime forkedAt;
}
