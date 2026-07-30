package com.codecanvas.snippetservice.controller;

import com.codecanvas.snippetservice.dto.response.LikeResponse;
import com.codecanvas.snippetservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/snippets")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{snippetId}/like")
    public LikeResponse likeSnippet(
            @PathVariable UUID snippetId) {

        return likeService.likeSnippet(snippetId);
    }

    @DeleteMapping("/{snippetId}/like")
    public LikeResponse unlikeSnippet(
            @PathVariable UUID snippetId) {

        return likeService.unlikeSnippet(snippetId);
    }

    @GetMapping("/{snippetId}/likes")
    public LikeResponse getLikeCount(
            @PathVariable UUID snippetId) {

        return likeService.getLikeCount(snippetId);
    }

    @GetMapping("/{snippetId}/liked")
    public LikeResponse hasLikedSnippet(
            @PathVariable UUID snippetId) {

        return likeService.hasLikedSnippet(snippetId);
    }
}