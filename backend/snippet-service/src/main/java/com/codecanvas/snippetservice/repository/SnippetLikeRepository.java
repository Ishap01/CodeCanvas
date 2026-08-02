package com.codecanvas.snippetservice.repository;

import com.codecanvas.snippetservice.entity.SnippetLike;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnippetLikeRepository
        extends JpaRepository<SnippetLike, UUID> {


    boolean existsBySnippetIdAndUserId(
            UUID snippetId,
            UUID userId
    );


    Optional<SnippetLike> findBySnippetIdAndUserId(
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
}