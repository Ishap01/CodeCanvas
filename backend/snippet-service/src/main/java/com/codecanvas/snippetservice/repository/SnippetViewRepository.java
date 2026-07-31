package com.codecanvas.snippetservice.repository;

import com.codecanvas.snippetservice.entity.SnippetView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SnippetViewRepository extends JpaRepository<SnippetView, UUID> {

    boolean existsBySnippetIdAndUserId(
            UUID snippetId,
            UUID userId
    );

    long countBySnippetId(
            UUID snippetId
    );
}