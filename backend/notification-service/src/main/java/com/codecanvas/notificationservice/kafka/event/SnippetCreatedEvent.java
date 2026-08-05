package com.codecanvas.notificationservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnippetCreatedEvent {

    private UUID snippetId;

    private UUID userId;

    private String title;

    private String description;

    private String code;

    private String language;

    private String framework;

    private String category;

    private List<String> tags;

    private String visibility;

    private String previewImageUrl;

    private Long likeCount;

    private Long viewCount;

    private Long bookmarkCount;

    private Long forkCount;

    private LocalDateTime createdAt;
}
