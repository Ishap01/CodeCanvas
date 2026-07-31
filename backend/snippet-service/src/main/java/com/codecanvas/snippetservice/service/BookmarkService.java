package com.codecanvas.snippetservice.service;

import com.codecanvas.snippetservice.dto.response.BookmarkResponse;
import com.codecanvas.snippetservice.dto.response.SnippetListResponse;

import java.util.List;
import java.util.UUID;

public interface BookmarkService {

    /**
     * Bookmark a snippet.
     */
    BookmarkResponse bookmarkSnippet(UUID snippetId);

    /**
     * Remove bookmark.
     */
    BookmarkResponse removeBookmark(UUID snippetId);

    /**
     * Get bookmark count.
     */
    BookmarkResponse getBookmarkCount(UUID snippetId);

    /**
     * Check whether current user bookmarked.
     */
    BookmarkResponse hasBookmarked(UUID snippetId);

    /**
     * Get all bookmarked snippet ids of current user.
     */
    List<SnippetListResponse> getMyBookmarks();
}