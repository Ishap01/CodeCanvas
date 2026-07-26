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

        if (request == null) {
            throw new IllegalArgumentException(
                    "Create snippet request is required"
            );
        }

        if (category == null) {
            throw new IllegalArgumentException(
                    "Category is required"
            );
        }

        Snippet snippet = new Snippet();

        snippet.setTitle(
                normalizeRequiredText(
                        request.getTitle(),
                        "Title is required"
                )
        );

        snippet.setDescription(
                normalizeRequiredText(
                        request.getDescription(),
                        "Description is required"
                )
        );

        /*
         * Code ke leading spaces aur indentation important
         * ho sakte hain, isliye trim nahi karenge.
         */
        snippet.setCode(
                validateRequiredCode(
                        request.getCode()
                )
        );

        snippet.setLanguage(
                normalizeRequiredText(
                        request.getLanguage(),
                        "Language is required"
                )
        );

        snippet.setFramework(
                normalizeOptionalText(
                        request.getFramework()
                )
        );

        snippet.setVisibility(
                request.getVisibility()
        );

        snippet.setStatus(Status.ACTIVE);
        snippet.setCategory(category);

        /*
         * Image initial create ke time null rahegi.
         *
         * Separate Cloudinary image endpoint se
         * upload ki jayegi.
         */
        snippet.setPreviewImageUrl(null);
        snippet.setPreviewImagePublicId(null);

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

        if (snippet == null) {
            throw new IllegalArgumentException(
                    "Snippet entity is required"
            );
        }

        if (request == null) {
            throw new IllegalArgumentException(
                    "Update snippet request is required"
            );
        }

        if (category == null) {
            throw new IllegalArgumentException(
                    "Category is required"
            );
        }

        snippet.setTitle(
                normalizeRequiredText(
                        request.getTitle(),
                        "Title is required"
                )
        );

        snippet.setDescription(
                normalizeRequiredText(
                        request.getDescription(),
                        "Description is required"
                )
        );

        snippet.setCode(
                validateRequiredCode(
                        request.getCode()
                )
        );

        snippet.setLanguage(
                normalizeRequiredText(
                        request.getLanguage(),
                        "Language is required"
                )
        );

        snippet.setFramework(
                normalizeOptionalText(
                        request.getFramework()
                )
        );

        snippet.setVisibility(
                request.getVisibility()
        );

        snippet.setCategory(category);

        /*
         * Important:
         *
         * Image fields yahan update nahi hongi.
         *
         * Cloudinary image upload, replace aur delete
         * separate service methods se honge.
         */
    }

    public SnippetResponse toResponse(
            Snippet snippet) {

        if (snippet == null) {
            throw new IllegalArgumentException(
                    "Snippet entity is required"
            );
        }

        List<String> tagNames;

        if (snippet.getSnippetTags() == null) {

            tagNames = Collections.emptyList();

        } else {

            tagNames = snippet.getSnippetTags()
                    .stream()
                    .filter(snippetTag ->
                            snippetTag != null
                                    && snippetTag.getTag() != null
                                    && snippetTag.getTag()
                                    .getName() != null
                    )
                    .map(snippetTag ->
                            snippetTag.getTag()
                                    .getName()
                    )
                    .sorted(
                            String.CASE_INSENSITIVE_ORDER
                    )
                    .toList();
        }

        SnippetResponse response =
                new SnippetResponse();

        response.setSnippetId(
                snippet.getSnippetId()
        );

        response.setTitle(
                snippet.getTitle()
        );

        response.setDescription(
                snippet.getDescription()
        );

        response.setCode(
                snippet.getCode()
        );

        response.setLanguage(
                snippet.getLanguage()
        );

        response.setFramework(
                snippet.getFramework()
        );

        /*
         * Cloudinary fields.
         */
        response.setPreviewImageUrl(
                snippet.getPreviewImageUrl()
        );

        response.setPreviewImagePublicId(
                snippet.getPreviewImagePublicId()
        );

        response.setVisibility(
                snippet.getVisibility()
        );

        response.setStatus(
                snippet.getStatus()
        );

        response.setUserId(
                snippet.getUserId()
        );

        if (snippet.getCategory() != null) {

            response.setCategoryId(
                    snippet.getCategory()
                            .getCategoryId()
            );

            response.setCategoryName(
                    snippet.getCategory()
                            .getName()
            );
        }

        response.setTags(tagNames);

        response.setViewCount(
                snippet.getViewCount()
        );

        response.setLikeCount(
                snippet.getLikeCount()
        );

        response.setBookmarkCount(
                snippet.getBookmarkCount()
        );

        response.setForkCount(
                snippet.getForkCount()
        );

        response.setParentSnippetId(
                snippet.getParentSnippetId()
        );

        response.setCreatedAt(
                snippet.getCreatedAt()
        );

        response.setUpdatedAt(
                snippet.getUpdatedAt()
        );

        return response;
    }

    private String normalizeRequiredText(
            String value,
            String errorMessage) {

        if (value == null
                || value.isBlank()) {

            throw new IllegalArgumentException(
                    errorMessage
            );
        }

        return value.trim();
    }

    private String validateRequiredCode(
            String code) {

        if (code == null
                || code.isBlank()) {

            throw new IllegalArgumentException(
                    "Code is required"
            );
        }

        /*
         * Code ko trim nahi karenge because indentation
         * aur formatting preserve rehni chahiye.
         */
        return code;
    }

    private String normalizeOptionalText(
            String value) {

        if (value == null
                || value.isBlank()) {

            return null;
        }

        return value.trim();
    }
}