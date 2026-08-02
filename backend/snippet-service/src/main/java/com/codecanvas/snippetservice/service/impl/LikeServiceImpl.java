package com.codecanvas.snippetservice.service.impl;

import com.codecanvas.snippetservice.dto.response.LikeResponse;
import com.codecanvas.snippetservice.entity.Snippet;
import com.codecanvas.snippetservice.entity.SnippetLike;
import com.codecanvas.snippetservice.exception.ResourceNotFoundException;
import com.codecanvas.snippetservice.repository.SnippetLikeRepository;
import com.codecanvas.snippetservice.repository.SnippetRepository;
import com.codecanvas.snippetservice.security.AuthenticatedUser;
import com.codecanvas.snippetservice.service.LikeService;
import com.codecanvas.snippetservice.service.SearchIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final SnippetRepository snippetRepository;
    private final SnippetLikeRepository likeRepository;
    private final SearchIndexService searchIndexService;

    private UUID getCurrentUserId() {

        AuthenticatedUser user =
                (AuthenticatedUser) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        return user.getUserId();
    }

    private Snippet getSnippet(UUID snippetId) {

        return snippetRepository.findById(snippetId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Snippet not found"
                        ));
    }

    @Override
    @CacheEvict(value = {"likes", "like_count", "snippets"}, allEntries = true)
    public LikeResponse likeSnippet(UUID snippetId) {

        UUID userId = getCurrentUserId();

        Snippet snippet = getSnippet(snippetId);

        if (likeRepository.existsBySnippetIdAndUserId(
                snippetId,
                userId
        )) {

            return LikeResponse.builder()
                    .success(true)
                    .liked(true)
                    .likeCount(snippet.getLikeCount())
                    .message("Snippet already liked.")
                    .build();
        }

        SnippetLike like = new SnippetLike();

        like.setSnippetId(snippetId);
        like.setUserId(userId);

        likeRepository.save(like);

        snippet.setLikeCount(
                snippet.getLikeCount() + 1
        );

        Snippet updatedSnippet =
                snippetRepository.save(snippet);

        searchIndexService.indexSnippet(updatedSnippet);

        return LikeResponse.builder()
                .success(true)
                .liked(true)
                .likeCount(updatedSnippet.getLikeCount())
                .message("Snippet liked successfully.")
                .build();
    }

    @Override
    @CacheEvict(value = {"likes", "like_count", "snippets"}, allEntries = true)
    public LikeResponse unlikeSnippet(UUID snippetId) {

        UUID userId = getCurrentUserId();

        Snippet snippet = getSnippet(snippetId);

        if (!likeRepository.existsBySnippetIdAndUserId(
                snippetId,
                userId
        )) {

            return LikeResponse.builder()
                    .success(true)
                    .liked(false)
                    .likeCount(snippet.getLikeCount())
                    .message("Snippet already unliked.")
                    .build();
        }

        likeRepository.deleteBySnippetIdAndUserId(
                snippetId,
                userId
        );

        snippet.setLikeCount(
                Math.max(0, snippet.getLikeCount() - 1)
        );

        Snippet updatedSnippet =
                snippetRepository.save(snippet);

        searchIndexService.indexSnippet(updatedSnippet);

        return LikeResponse.builder()
                .success(true)
                .liked(false)
                .likeCount(updatedSnippet.getLikeCount())
                .message("Snippet unliked successfully.")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "like_count", key = "#snippetId")
    public LikeResponse getLikeCount(UUID snippetId) {

        Snippet snippet = getSnippet(snippetId);

        return LikeResponse.builder()
                .success(true)
                .liked(false)
                .likeCount(snippet.getLikeCount())
                .message("Like count fetched successfully.")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "likes", key = "#snippetId")
    public LikeResponse hasLikedSnippet(UUID snippetId) {

        UUID userId = getCurrentUserId();

        Snippet snippet = getSnippet(snippetId);

        boolean liked =
                likeRepository.existsBySnippetIdAndUserId(
                        snippetId,
                        userId
                );

        return LikeResponse.builder()
                .success(true)
                .liked(liked)
                .likeCount(snippet.getLikeCount())
                .message("Like status fetched successfully.")
                .build();
    }
}
