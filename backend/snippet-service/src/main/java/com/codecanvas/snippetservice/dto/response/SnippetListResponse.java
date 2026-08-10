package com.codecanvas.snippetservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnippetListResponse {

    private UUID snippetId;

    private String title;

    private String description;

    private String language;

    private String framework;

    private String previewImageUrl;

    private UUID userId;

    private long viewCount;

    private long likeCount;

    private long bookmarkCount;

    private long forkCount;

    private LocalDateTime createdAt;
}