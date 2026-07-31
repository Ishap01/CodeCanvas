package com.codecanvas.snippetservice.service;

import com.codecanvas.snippetservice.dto.response.LikeResponse;

import java.util.UUID;

public interface LikeService {

    /**
     * Like a snippet.
     */
    LikeResponse likeSnippet(UUID snippetId);

    /**
     * Remove like from a snippet.
     */
    LikeResponse unlikeSnippet(UUID snippetId);

    /**
     * Get total likes of a snippet.
     */
    LikeResponse getLikeCount(UUID snippetId);

    /**
     * Check whether current user has liked a snippet.
     */
    LikeResponse hasLikedSnippet(UUID snippetId);
}