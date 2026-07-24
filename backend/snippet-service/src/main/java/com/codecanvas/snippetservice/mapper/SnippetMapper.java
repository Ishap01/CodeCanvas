package com.codecanvas.snippetservice.mapper;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.codecanvas.snippetservice.dto.request.CreateSnippetRequest;
import com.codecanvas.snippetservice.dto.request.UpdateSnippetRequest;
import com.codecanvas.snippetservice.dto.response.SnippetResponse;
import com.codecanvas.snippetservice.entity.Category;
import com.codecanvas.snippetservice.entity.Snippet;
import com.codecanvas.snippetservice.enums.Status;

@Component
public class SnippetMapper {

    public Snippet toEntity(
            CreateSnippetRequest request,
            Category category) {

        Snippet snippet = new Snippet();

        snippet.setTitle(request.getTitle().trim());
        snippet.setDescription(request.getDescription().trim());
        snippet.setCode(request.getCode());
        snippet.setLanguage(request.getLanguage().trim());
        snippet.setFramework(
                normalizeOptionalText(request.getFramework())
        );
        snippet.setVisibility(request.getVisibility());
        snippet.setStatus(Status.ACTIVE);
        snippet.setCategory(category);
        snippet.setViewCount(0L);
        snippet.setLikeCount(0L);
        snippet.setBookmarkCount(0L);
        snippet.setForkCount(0L);

        return snippet;
    }

    public void updateEntity(
            Snippet snippet,
            UpdateSnippetRequest request,
            Category category) {

        snippet.setTitle(request.getTitle().trim());
        snippet.setDescription(request.getDescription().trim());
        snippet.setCode(request.getCode());
        snippet.setLanguage(request.getLanguage().trim());
        snippet.setFramework(
                normalizeOptionalText(request.getFramework())
        );
        snippet.setVisibility(request.getVisibility());
        snippet.setCategory(category);
    }

    public SnippetResponse toResponse(Snippet snippet) {

        List<String> tagNames;

        if (snippet.getSnippetTags() == null) {

            tagNames = Collections.emptyList();

        } else {

            tagNames = snippet.getSnippetTags()
                    .stream()
                    .filter(snippetTag ->
                            snippetTag != null
                            && snippetTag.getTag() != null
                    )
                    .map(snippetTag ->
                            snippetTag.getTag().getName()
                    )
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .toList();
        }

        SnippetResponse response = new SnippetResponse();

        response.setSnippetId(snippet.getSnippetId());
        response.setTitle(snippet.getTitle());
        response.setDescription(snippet.getDescription());
        response.setCode(snippet.getCode());
        response.setLanguage(snippet.getLanguage());
        response.setFramework(snippet.getFramework());
        response.setPreviewImageUrl(snippet.getPreviewImageUrl());
        response.setVisibility(snippet.getVisibility());
        response.setStatus(snippet.getStatus());
        response.setUserId(snippet.getUserId());

        if (snippet.getCategory() != null) {
            response.setCategoryId(
                    snippet.getCategory().getCategoryId()
            );

            response.setCategoryName(
                    snippet.getCategory().getName()
            );
        }

        response.setTags(tagNames);
        response.setViewCount(snippet.getViewCount());
        response.setLikeCount(snippet.getLikeCount());
        response.setBookmarkCount(snippet.getBookmarkCount());
        response.setForkCount(snippet.getForkCount());
        response.setParentSnippetId(snippet.getParentSnippetId());
        response.setCreatedAt(snippet.getCreatedAt());
        response.setUpdatedAt(snippet.getUpdatedAt());

        return response;
    }

    private String normalizeOptionalText(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}