package com.codecanvas.snippetservice.kafka.event;

import com.codecanvas.snippetservice.enums.Visibility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SnippetUpdatedEvent {

    private UUID snippetId;

    private UUID userId;

    private String title;

    private String description;

    private String code;

    private String language;

    private String framework;

    private String category;

    private List<String> tags;

    private Visibility visibility;

    private String previewImageUrl;

    private Long likeCount;

    private Long viewCount;

    private Long bookmarkCount;

    private Long forkCount;

    private LocalDateTime createdAt;
}