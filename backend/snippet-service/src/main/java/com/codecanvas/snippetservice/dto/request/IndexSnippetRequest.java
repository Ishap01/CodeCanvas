package com.codecanvas.snippetservice.dto.request;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndexSnippetRequest {

    private UUID snippetId;

    private String title;

    private String description;

    private String language;

    private String framework;

    private String category;

    private List<String> tags;

    private Long likes;

    private Long views;

    private Long bookmarks;

    private Long forks;

    private LocalDateTime createdAt;

    private String previewImageUrl;

}