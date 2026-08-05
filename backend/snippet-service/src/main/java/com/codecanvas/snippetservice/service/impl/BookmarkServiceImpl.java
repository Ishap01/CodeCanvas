package com.codecanvas.snippetservice.service.impl;

import com.codecanvas.snippetservice.dto.response.BookmarkResponse;
import com.codecanvas.snippetservice.dto.response.SnippetListResponse;
import com.codecanvas.snippetservice.entity.Bookmark;
import com.codecanvas.snippetservice.entity.Snippet;
import com.codecanvas.snippetservice.exception.ResourceNotFoundException;
import com.codecanvas.snippetservice.repository.BookmarkRepository;
import com.codecanvas.snippetservice.repository.SnippetRepository;
import com.codecanvas.snippetservice.security.AuthenticatedUser;
import com.codecanvas.snippetservice.service.BookmarkService;
import com.codecanvas.snippetservice.service.SearchIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codecanvas.snippetservice.kafka.event.SnippetBookmarkedEvent;
import com.codecanvas.snippetservice.kafka.mapper.SnippetEventMapper;
import com.codecanvas.snippetservice.kafka.producer.SnippetEventProducer;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkServiceImpl
        implements BookmarkService {

    private final SnippetRepository snippetRepository;
    private final BookmarkRepository bookmarkRepository;
    private final SearchIndexService searchIndexService;

    // Kafka Producer
    private final SnippetEventProducer snippetEventProducer;

    // Converts entity into Kafka event.
    private final SnippetEventMapper snippetEventMapper;

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
    public BookmarkResponse bookmarkSnippet(UUID snippetId) {

        UUID userId = getCurrentUserId();

        Snippet snippet = getSnippet(snippetId);

        if (bookmarkRepository.existsBySnippetIdAndUserId(
                snippetId,
                userId
        )) {

            return BookmarkResponse.builder()
                    .success(true)
                    .bookmarked(true)
                    .bookmarkCount(snippet.getBookmarkCount())
                    .message("Snippet already bookmarked.")
                    .build();
        }

        Bookmark bookmark = new Bookmark();

        bookmark.setSnippetId(snippetId);
        bookmark.setUserId(userId);

        bookmarkRepository.save(bookmark);

        snippet.setBookmarkCount(
                snippet.getBookmarkCount() + 1
        );

        Snippet updatedSnippet =
                snippetRepository.save(snippet);

        /*
         * Create Kafka event
         * after successful database update.
         */
        SnippetBookmarkedEvent event =
                snippetEventMapper.toSnippetBookmarkedEvent(
                        updatedSnippet,
                        userId
                );

        /*
         * Publish bookmark event to Kafka.
         */
        snippetEventProducer.publishSnippetBookmarkedEvent(
                event
        );

        /*
         * Update Elasticsearch index.
         */

        searchIndexService.indexSnippet(updatedSnippet);

        return BookmarkResponse.builder()
                .success(true)
                .bookmarked(true)
                .bookmarkCount(updatedSnippet.getBookmarkCount())
                .message("Snippet bookmarked successfully.")
                .build();
    }

    @Override
    public BookmarkResponse removeBookmark(UUID snippetId) {

        UUID userId = getCurrentUserId();

        Snippet snippet = getSnippet(snippetId);

        if (!bookmarkRepository.existsBySnippetIdAndUserId(
                snippetId,
                userId
        )) {

            return BookmarkResponse.builder()
                    .success(true)
                    .bookmarked(false)
                    .bookmarkCount(snippet.getBookmarkCount())
                    .message("Snippet already removed from bookmarks.")
                    .build();
        }

        bookmarkRepository.deleteBySnippetIdAndUserId(
                snippetId,
                userId
        );

        snippet.setBookmarkCount(
                Math.max(
                        0,
                        snippet.getBookmarkCount() - 1
                )
        );

        Snippet updatedSnippet =
                snippetRepository.save(snippet);

        searchIndexService.indexSnippet(updatedSnippet);

        return BookmarkResponse.builder()
                .success(true)
                .bookmarked(false)
                .bookmarkCount(updatedSnippet.getBookmarkCount())
                .message("Bookmark removed successfully.")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BookmarkResponse getBookmarkCount(
            UUID snippetId) {

        Snippet snippet = getSnippet(snippetId);

        return BookmarkResponse.builder()
                .success(true)
                .bookmarked(false)
                .bookmarkCount(snippet.getBookmarkCount())
                .message("Bookmark count fetched successfully.")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BookmarkResponse hasBookmarked(
            UUID snippetId) {

        UUID userId = getCurrentUserId();

        Snippet snippet = getSnippet(snippetId);

        boolean bookmarked =
                bookmarkRepository.existsBySnippetIdAndUserId(
                        snippetId,
                        userId
                );

        return BookmarkResponse.builder()
                .success(true)
                .bookmarked(bookmarked)
                .bookmarkCount(snippet.getBookmarkCount())
                .message("Bookmark status fetched successfully.")
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public List<SnippetListResponse> getMyBookmarks() {

        UUID userId = getCurrentUserId();

        List<UUID> snippetIds =
                bookmarkRepository.findByUserIdOrderByBookmarkedAtDesc(userId)
                        .stream()
                        .map(Bookmark::getSnippetId)
                        .toList();

        if (snippetIds.isEmpty()) {
            return List.of();
        }

        return snippetRepository.findBySnippetIdIn(snippetIds)
                .stream()
                .map(this::toSnippetListResponse)
                .toList();
    }

    private SnippetListResponse toSnippetListResponse(
            Snippet snippet) {

        return SnippetListResponse.builder()
                .snippetId(snippet.getSnippetId())
                .title(snippet.getTitle())
                .description(snippet.getDescription())
                .language(snippet.getLanguage())
                .framework(snippet.getFramework())
                .previewImageUrl(snippet.getPreviewImageUrl())
                .userId(snippet.getUserId())
                .viewCount(snippet.getViewCount())
                .likeCount(snippet.getLikeCount())
                .bookmarkCount(snippet.getBookmarkCount())
                .forkCount(snippet.getForkCount())
                .createdAt(snippet.getCreatedAt())
                .build();
    }
}