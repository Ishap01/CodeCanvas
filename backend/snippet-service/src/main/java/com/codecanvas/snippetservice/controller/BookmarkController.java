package com.codecanvas.snippetservice.controller;

import com.codecanvas.snippetservice.dto.response.BookmarkResponse;
import com.codecanvas.snippetservice.dto.response.SnippetListResponse;
import com.codecanvas.snippetservice.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/snippets")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/{snippetId}/bookmark")
    public BookmarkResponse bookmarkSnippet(
            @PathVariable UUID snippetId) {

        return bookmarkService.bookmarkSnippet(snippetId);
    }

    @DeleteMapping("/{snippetId}/bookmark")
    public BookmarkResponse removeBookmark(
            @PathVariable UUID snippetId) {

        return bookmarkService.removeBookmark(snippetId);
    }

    @GetMapping("/{snippetId}/bookmarks")
    public BookmarkResponse getBookmarkCount(
            @PathVariable UUID snippetId) {

        return bookmarkService.getBookmarkCount(snippetId);
    }

    @GetMapping("/{snippetId}/bookmarked")
    public BookmarkResponse hasBookmarked(
            @PathVariable UUID snippetId) {

        return bookmarkService.hasBookmarked(snippetId);
    }

    @GetMapping("/bookmarks/me")
    public List<SnippetListResponse> getMyBookmarks() {

        return bookmarkService.getMyBookmarks();
    }
}