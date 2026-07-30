package com.codecanvas.snippetservice.repository;

import com.codecanvas.snippetservice.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookmarkRepository
        extends JpaRepository<Bookmark, UUID> {

    boolean existsBySnippetIdAndUserId(
            UUID snippetId,
            UUID userId
    );

    Optional<Bookmark> findBySnippetIdAndUserId(
            UUID snippetId,
            UUID userId
    );

    long countBySnippetId(
            UUID snippetId
    );

    void deleteBySnippetIdAndUserId(
            UUID snippetId,
            UUID userId
    );


    List<Bookmark> findByUserIdOrderByBookmarkedAtDesc(UUID userId);
}