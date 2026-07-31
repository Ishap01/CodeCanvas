package com.codecanvas.snippetservice.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.time.LocalDateTime;

import com.codecanvas.snippetservice.repository.SnippetViewRepository;
import com.codecanvas.snippetservice.service.SearchIndexService;
import com.codecanvas.snippetservice.service.ViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.codecanvas.snippetservice.dto.request.CreateSnippetRequest;
import com.codecanvas.snippetservice.dto.request.UpdateSnippetRequest;
import com.codecanvas.snippetservice.dto.response.ApiResponse;
import com.codecanvas.snippetservice.dto.response.CloudinaryUploadResponse;
import com.codecanvas.snippetservice.dto.response.SnippetResponse;
import com.codecanvas.snippetservice.entity.Category;
import com.codecanvas.snippetservice.entity.Snippet;
import com.codecanvas.snippetservice.entity.SnippetTag;
import com.codecanvas.snippetservice.entity.Tag;
import com.codecanvas.snippetservice.enums.Status;
import com.codecanvas.snippetservice.enums.Visibility;
import com.codecanvas.snippetservice.exception.ResourceNotFoundException;
import com.codecanvas.snippetservice.exception.UnauthorizedActionException;
import com.codecanvas.snippetservice.mapper.SnippetMapper;
import com.codecanvas.snippetservice.repository.CategoryRepository;
import com.codecanvas.snippetservice.repository.SnippetRepository;
import com.codecanvas.snippetservice.repository.TagRepository;
import com.codecanvas.snippetservice.service.CloudinaryService;
import com.codecanvas.snippetservice.service.SnippetService;
import com.codecanvas.snippetservice.dto.request.IndexSnippetRequest;
import com.codecanvas.snippetservice.client.SearchServiceClient;

@Service
@Transactional
@RequiredArgsConstructor
public class SnippetServiceImpl implements SnippetService {

    private final SnippetRepository snippetRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final SnippetMapper snippetMapper;
    private final CloudinaryService cloudinaryService;
    private final SearchIndexService searchIndexService;
    private final SnippetViewRepository snippetViewRepository;
    private final ViewService viewService;


    @Override
    public SnippetResponse createSnippet(
            UUID userId,
            CreateSnippetRequest request) {

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

        /*
         * Image initially null rahegi.
         *
         * Image upload API baad mein Cloudinary URL
         * aur public ID save karegi.
         */
        snippet.setPreviewImageUrl(null);
        snippet.setPreviewImagePublicId(null);

        addTagsToSnippet(
                snippet,
                request.getTags()
        );

        /*
         * Snippet parent entity hai.
         *
         * CascadeType.ALL ke karan associated
         * SnippetTag rows automatically save hongi.
         */
        Snippet savedSnippet =
                snippetRepository.save(snippet);

        searchIndexService.indexSnippet(savedSnippet);

        return snippetMapper.toResponse(savedSnippet);
    }

    @Override
    @Transactional
    public SnippetResponse getSnippetById(
            UUID snippetId,
            UUID currentUserId) {

        Snippet snippet =
                findActiveSnippetById(snippetId);

        boolean owner =
                currentUserId != null
                        && currentUserId.equals(
                        snippet.getUserId()
                );

        boolean publiclyVisible =
                snippet.getVisibility()
                        == Visibility.PUBLIC;

        if (!owner && !publiclyVisible) {
            System.out.println("Visibility = " + snippet.getVisibility());
            throw new UnauthorizedActionException(
                    "You are not allowed to view this private snippet"
            );
        }

        viewService.recordView(
                snippet,
                currentUserId
        );

        return snippetMapper.toResponse(snippet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SnippetResponse> getPublicSnippets() {

        List<Snippet> snippets =
                snippetRepository
                        .findByVisibilityAndStatus(
                                Visibility.PUBLIC,
                                Status.ACTIVE
                        );

        return convertToResponseList(snippets);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SnippetResponse> getAllSnippets() {

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
                snippetRepository
                        .findByUserIdAndStatus(
                                userId,
                                Status.ACTIVE
                        );

        return convertToResponseList(snippets);
    }

    @Override
    public SnippetResponse updateSnippet(
            UUID snippetId,
            UUID userId,
            UpdateSnippetRequest request) {

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

        /*
         * This updates:
         *
         * title
         * description
         * code
         * language
         * framework
         * visibility
         * category
         *
         * Cloudinary fields are not modified here.
         */
        snippetMapper.updateEntity(
                snippet,
                request,
                category
        );

        synchronizeTags(
                snippet,
                request.getTags()
        );

        Snippet updatedSnippet =
                snippetRepository.save(snippet);

        searchIndexService.indexSnippet(updatedSnippet);
        return snippetMapper.toResponse(updatedSnippet);
    }

    @Override
    public SnippetResponse uploadOrReplacePreviewImage(
            UUID snippetId,
            UUID userId,
            MultipartFile image) {

        Snippet snippet =
                findActiveSnippetById(snippetId);

        verifyOwnership(snippet, userId);

        /*
         * Existing Cloudinary public ID ko temporarily
         * preserve karenge.
         *
         * New image successfully upload hone ke baad hi
         * old image delete hogi.
         */
        String oldPublicId =
                normalizeOptionalText(
                        snippet.getPreviewImagePublicId()
                );

        /*
         * CloudinaryService internally:
         *
         * 1. File validate karegi
         * 2. Image upload karegi
         * 3. secure_url return karegi
         * 4. public_id return karegi
         */
        CloudinaryUploadResponse uploadResponse =
                cloudinaryService.uploadImage(image);

        if (uploadResponse == null
                || normalizeOptionalText(
                uploadResponse.getImageUrl()
        ) == null
                || normalizeOptionalText(
                uploadResponse.getImagePublicId()
        ) == null) {

            throw new IllegalStateException(
                    "Cloudinary did not return valid image details"
            );
        }

        String newImageUrl =
                uploadResponse.getImageUrl().trim();

        String newPublicId =
                uploadResponse
                        .getImagePublicId()
                        .trim();

        snippet.setPreviewImageUrl(
                newImageUrl
        );

        snippet.setPreviewImagePublicId(
                newPublicId
        );

        try {

            Snippet updatedSnippet =
                    snippetRepository.save(snippet);

            searchIndexService.indexSnippet(updatedSnippet);

            /*
             * New image and database update successful hone ke baad
             * previous Cloudinary image delete karenge.
             */
            if (oldPublicId != null
                    && !oldPublicId.equals(newPublicId)) {

                cloudinaryService.deleteImage(
                        oldPublicId
                );
            }

            return snippetMapper.toResponse(
                    updatedSnippet
            );

        } catch (RuntimeException exception) {

            /*
             * Database save fail ho gaya, lekin new image
             * Cloudinary par upload ho chuki hai.
             *
             * Orphan image avoid karne ke liye new image
             * delete karne ki koshish karenge.
             */
            try {
                cloudinaryService.deleteImage(
                        newPublicId
                );
            } catch (RuntimeException cleanupException) {

                exception.addSuppressed(
                        cleanupException
                );
            }

            throw exception;
        }
    }

    @Override
    public ApiResponse deletePreviewImage(
            UUID snippetId,
            UUID userId) {

        Snippet snippet =
                findActiveSnippetById(snippetId);

        verifyOwnership(snippet, userId);

        String publicId =
                normalizeOptionalText(
                        snippet.getPreviewImagePublicId()
                );

        if (publicId == null) {

            /*
             * Public ID absent hone par bhi stale URL
             * database mein ho sakti hai.
             */
            snippet.setPreviewImageUrl(null);
            snippet.setPreviewImagePublicId(null);

            Snippet updatedSnippet = snippetRepository.save(snippet);

            searchIndexService.indexSnippet(updatedSnippet);

            return new ApiResponse(
                    true,
                    "Snippet preview image deleted successfully"
            );
        }

        /*
         * First Cloudinary asset delete karenge.
         *
         * Delete successful hone ke baad database fields
         * clear hongi.
         */
        cloudinaryService.deleteImage(publicId);

        snippet.setPreviewImageUrl(null);
        snippet.setPreviewImagePublicId(null);

        Snippet updatedSnippet =
                snippetRepository.save(snippet);

        searchIndexService.indexSnippet(updatedSnippet);

        return new ApiResponse(
                true,
                "Snippet preview image deleted successfully"
        );
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
         * Database row remove nahi hogi.
         * Status ACTIVE se DELETED ho jayega.
         *
         * Preview image bhi preserve rahegi because future
         * mein restore feature add kiya ja sakta hai.
         */
        snippet.setStatus(Status.DELETED);

        snippetRepository.save(snippet);

        searchIndexService.deleteSnippet(snippetId);

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

        Snippet snippet =
                snippetRepository
                        .findById(snippetId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Snippet not found with id: "
                                                + snippetId
                                )
                        );

        if (snippet.getStatus()
                == Status.DELETED) {

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
                .findByCategoryNameIgnoreCase(
                        normalizedCategoryName
                )
                .orElseGet(() -> {

                    Category category =
                            new Category();

                    category.setCategoryName(
                            normalizedCategoryName
                    );

                    return categoryRepository.save(
                            category
                    );
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
             * React, react aur REACT ko same
             * request tag maana jayega.
             */
            if (!processedTagNames.add(
                    lowercaseTagName)) {

                continue;
            }

            Tag tag = findOrCreateTag(
                    normalizedTagName
            );

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

    private void synchronizeTags(
            Snippet snippet,
            List<String> requestedTagNames) {

        if (snippet == null) {
            throw new IllegalArgumentException(
                    "Snippet is required"
            );
        }

        if (requestedTagNames == null
                || requestedTagNames.isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one tag is required"
            );
        }

        Set<String> requestedNormalizedNames =
                new HashSet<>();

        for (String tagName
                : requestedTagNames) {

            if (tagName == null
                    || tagName.isBlank()) {
                continue;
            }

            requestedNormalizedNames.add(
                    tagName.trim()
                            .toLowerCase(
                                    Locale.ROOT
                            )
            );
        }

        if (requestedNormalizedNames.isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one valid tag is required"
            );
        }

        if (snippet.getSnippetTags() == null) {

            throw new IllegalStateException(
                    "Snippet tag collection is not initialized"
            );
        }

        Iterator<SnippetTag> iterator =
                snippet.getSnippetTags()
                        .iterator();

        while (iterator.hasNext()) {

            SnippetTag snippetTag =
                    iterator.next();

            if (snippetTag == null
                    || snippetTag.getTag() == null
                    || snippetTag.getTag()
                    .getTagName() == null) {

                iterator.remove();
                continue;
            }

            String existingTagName =
                    snippetTag.getTag()
                            .getTagName()
                            .trim()
                            .toLowerCase(
                                    Locale.ROOT
                            );

            if (!requestedNormalizedNames
                    .contains(existingTagName)) {

                iterator.remove();
                snippetTag.setSnippet(null);
            }
        }

        Set<String> existingNormalizedNames =
                new HashSet<>();

        for (SnippetTag snippetTag
                : snippet.getSnippetTags()) {

            if (snippetTag == null
                    || snippetTag.getTag() == null
                    || snippetTag.getTag()
                    .getTagName() == null) {

                continue;
            }

            existingNormalizedNames.add(
                    snippetTag.getTag()
                            .getTagName()
                            .trim()
                            .toLowerCase(
                                    Locale.ROOT
                            )
            );
        }

        Set<String> processedRequestTags =
                new HashSet<>();

        for (String requestedTagName
                : requestedTagNames) {

            if (requestedTagName == null
                    || requestedTagName.isBlank()) {

                continue;
            }

            String normalizedTagName =
                    requestedTagName.trim();

            String lowercaseTagName =
                    normalizedTagName.toLowerCase(
                            Locale.ROOT
                    );

            if (!processedRequestTags.add(
                    lowercaseTagName)) {

                continue;
            }

            if (existingNormalizedNames.contains(
                    lowercaseTagName)) {

                continue;
            }

            Tag tag = findOrCreateTag(
                    normalizedTagName
            );

            snippet.addTag(tag);

            existingNormalizedNames.add(
                    lowercaseTagName
            );
        }

        if (snippet.getSnippetTags()
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one valid tag is required"
            );
        }
    }

    private Tag findOrCreateTag(
            String tagName) {

        String normalizedTagName =
                normalizeRequiredText(
                        tagName,
                        "Tag name is required"
                );

        return tagRepository
                .findByTagNameIgnoreCase(
                        normalizedTagName
                )
                .orElseGet(() -> {

                    Tag tag = new Tag();

                    tag.setTagName(
                            normalizedTagName
                    );

                    return tagRepository.save(tag);
                });
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