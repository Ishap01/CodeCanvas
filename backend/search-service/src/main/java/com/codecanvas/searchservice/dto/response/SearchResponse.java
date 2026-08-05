package com.codecanvas.searchservice.dto.response;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResponse {

    private UUID snippetId;

    private String title;

    private String description;

    private String language;

    private String framework;

    private String previewImageUrl;

    private Long views;

    private Long likes;

    private Long forks;

    private Boolean bookmarked;

    private String category;

    private List<String> tags;

    private Long bookmarks;

}