package com.codecanvas.snippetservice.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codecanvas.snippetservice.dto.request.CreateSnippetRequest;
import com.codecanvas.snippetservice.dto.request.UpdateSnippetRequest;
import com.codecanvas.snippetservice.dto.response.ApiResponse;
import com.codecanvas.snippetservice.dto.response.SnippetResponse;
import com.codecanvas.snippetservice.entity.Category;
import com.codecanvas.snippetservice.entity.Snippet;
import com.codecanvas.snippetservice.entity.Tag;
import com.codecanvas.snippetservice.enums.Status;
import com.codecanvas.snippetservice.enums.Visibility;
import com.codecanvas.snippetservice.exception.ResourceNotFoundException;
import com.codecanvas.snippetservice.exception.UnauthorizedActionException;
import com.codecanvas.snippetservice.mapper.SnippetMapper;
import com.codecanvas.snippetservice.repository.CategoryRepository;
import com.codecanvas.snippetservice.repository.SnippetRepository;
import com.codecanvas.snippetservice.repository.TagRepository;
import com.codecanvas.snippetservice.service.SnippetService;

@Service
@Transactional
public class SnippetServiceImpl implements SnippetService {

    private final SnippetRepository snippetRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final SnippetMapper snippetMapper;

    public SnippetServiceImpl(
            SnippetRepository snippetRepository,
            CategoryRepository categoryRepository,
            TagRepository tagRepository,
            SnippetMapper snippetMapper) {

        this.snippetRepository = snippetRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.snippetMapper = snippetMapper;
    }

    @Override
    public SnippetResponse createSnippet(
            UUID userId,
            CreateSnippetRequest request,
            String previewImageUrl) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "Authenticated user id is required"
            );
        }

        if (request == null) {
            throw new IllegalArgumentException(
                    "Snippet request is required"
            );
        }

        Category category = findOrCreateCategory(
                request.getCategory()
        );

        Snippet snippet = snippetMapper.toEntity(
                request,
                category
        );

        if (snippet == null) {
            throw new IllegalStateException(
                    "Snippet could not be created from request"
            );
        }

        snippet.setUserId(userId);

        snippet.setPreviewImageUrl(
                normalizeOptionalText(previewImageUrl)
        );

        addTagsToSnippet(
                snippet,
                request.getTags()
        );

        /*
         * Sirf parent Snippet save hoga.
         *
         * Snippet entity mein:
         *
         * @OneToMany(
         *     mappedBy = "snippet",
         *     cascade = CascadeType.ALL,
         *     orphanRemoval = true
         * )
         *
         * hona chahiye.
         *
         * Cascade ki wajah se SnippetTag automatically save honge.
         */
        Snippet savedSnippet =
                snippetRepository.save(snippet);

        return snippetMapper.toResponse(savedSnippet);
    }

    @Override
    @Transactional(readOnly = true)
    public SnippetResponse getSnippetById(
            UUID snippetId,
            UUID currentUserId) {

        Snippet snippet = findActiveSnippetById(snippetId);

        boolean owner =
                currentUserId != null
                && currentUserId.equals(
                        snippet.getUserId()
                );

        boolean publiclyVisible =
                snippet.getVisibility()
                        == Visibility.PUBLIC;

        if (!owner && !publiclyVisible) {

            throw new UnauthorizedActionException(
                    "You are not allowed to view this private snippet"
            );
        }

        return snippetMapper.toResponse(snippet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SnippetResponse> getPublicSnippets() {

        List<Snippet> snippets =
                snippetRepository.findByVisibilityAndStatus(
                        Visibility.PUBLIC,
                        Status.ACTIVE
                );

        return convertToResponseList(snippets);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SnippetResponse> getSnippetsByUserId(
            UUID userId) {

        if (userId == null) {
            throw new IllegalArgumentException(
                    "User id is required"
            );
        }

        List<Snippet> snippets =
                snippetRepository.findByUserIdAndStatus(
                        userId,
                        Status.ACTIVE
                );

        return convertToResponseList(snippets);
    }

    @Override
    public SnippetResponse updateSnippet(
            UUID snippetId,
            UUID userId,
            UpdateSnippetRequest request,
            String previewImageUrl) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Snippet update request is required"
            );
        }

        Snippet snippet =
                findActiveSnippetById(snippetId);

        verifyOwnership(snippet, userId);

        Category category =
                findOrCreateCategory(
                        request.getCategory()
                );

        snippetMapper.updateEntity(
                snippet,
                request,
                category
        );

        String normalizedImageUrl =
                normalizeOptionalText(
                        previewImageUrl
                );

        /*
         * Null ka matlab:
         * frontend ne new preview image nahi bheji.
         *
         * Isliye old image preserve hogi.
         */
        if (normalizedImageUrl != null) {
            snippet.setPreviewImageUrl(
                    normalizedImageUrl
            );
        }

        /*
         * Purane SnippetTag relation remove honge.
         *
         * orphanRemoval = true ki wajah se old
         * snippet_tags rows database se remove hongi.
         */
        snippet.clearTags();

        addTagsToSnippet(
                snippet,
                request.getTags()
        );

        Snippet updatedSnippet =
                snippetRepository.save(snippet);

        return snippetMapper.toResponse(updatedSnippet);
    }

    @Override
    public ApiResponse deleteSnippet(
            UUID snippetId,
            UUID userId) {

        Snippet snippet =
                findActiveSnippetById(snippetId);

        verifyOwnership(snippet, userId);

        /*
         * Soft delete:
         *
         * Row physically delete nahi hogi.
         * Sirf status DELETED ho jayega.
         */
        snippet.setStatus(Status.DELETED);

        snippetRepository.save(snippet);

        return new ApiResponse(
                true,
                "Snippet deleted successfully"
        );
    }

    private Snippet findActiveSnippetById(
            UUID snippetId) {

        if (snippetId == null) {
            throw new IllegalArgumentException(
                    "Snippet id is required"
            );
        }

        Snippet snippet = snippetRepository
                .findById(snippetId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Snippet not found with id: "
                                        + snippetId
                        )
                );

        if (snippet.getStatus() == Status.DELETED) {

            throw new ResourceNotFoundException(
                    "Snippet not found with id: "
                            + snippetId
            );
        }

        return snippet;
    }

    private void verifyOwnership(
            Snippet snippet,
            UUID userId) {

        if (snippet == null) {
            throw new ResourceNotFoundException(
                    "Snippet not found"
            );
        }

        if (userId == null) {
            throw new UnauthorizedActionException(
                    "Authenticated user is required"
            );
        }

        if (snippet.getUserId() == null
                || !snippet.getUserId()
                        .equals(userId)) {

            throw new UnauthorizedActionException(
                    "You are not allowed to modify this snippet"
            );
        }
    }

    private Category findOrCreateCategory(
            String categoryName) {

        String normalizedCategoryName =
                normalizeRequiredText(
                        categoryName,
                        "Category is required"
                );

        return categoryRepository
                .findByNameIgnoreCase(
                        normalizedCategoryName
                )
                .orElseGet(() -> {

                    Category category =
                            new Category();

                    category.setName(
                            normalizedCategoryName
                    );

                    return categoryRepository
                            .save(category);
                });
    }

    private void addTagsToSnippet(
            Snippet snippet,
            List<String> tagNames) {

        if (snippet == null) {
            throw new IllegalArgumentException(
                    "Snippet is required"
            );
        }

        if (tagNames == null
                || tagNames.isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one tag is required"
            );
        }

        Set<String> processedTagNames =
                new HashSet<>();

        for (String tagName : tagNames) {

            if (tagName == null
                    || tagName.isBlank()) {

                continue;
            }

            String normalizedTagName =
                    tagName.trim();

            String lowercaseTagName =
                    normalizedTagName.toLowerCase(
                            Locale.ROOT
                    );

            /*
             * React, react, REACT ko duplicate
             * treat karenge.
             */
            if (!processedTagNames.add(
                    lowercaseTagName)) {

                continue;
            }

            Tag tag = tagRepository
                    .findByNameIgnoreCase(
                            normalizedTagName
                    )
                    .orElseGet(() -> {

                        Tag newTag = new Tag();

                        newTag.setName(
                                normalizedTagName
                        );

                        return tagRepository
                                .save(newTag);
                    });

            /*
             * Important:
             *
             * snippet.addTag(tag) ke andar:
             *
             * SnippetTag snippetTag = new SnippetTag();
             * snippetTag.setSnippet(this);
             * snippetTag.setTag(tag);
             * snippetTags.add(snippetTag);
             *
             * hona chahiye.
             *
             * SnippetTagRepository.save() manually
             * call nahi karna.
             */
            snippet.addTag(tag);
        }

        if (snippet.getSnippetTags() == null
                || snippet.getSnippetTags()
                        .isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one valid tag is required"
            );
        }
    }

    private List<SnippetResponse>
            convertToResponseList(
                    List<Snippet> snippets) {

        List<SnippetResponse> responses =
                new ArrayList<>();

        if (snippets == null
                || snippets.isEmpty()) {

            return responses;
        }

        for (Snippet snippet : snippets) {

            if (snippet == null) {
                continue;
            }

            responses.add(
                    snippetMapper.toResponse(
                            snippet
                    )
            );
        }

        return responses;
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

    private String normalizeOptionalText(
            String value) {

        if (value == null
                || value.isBlank()) {

            return null;
        }

        return value.trim();
    }
}