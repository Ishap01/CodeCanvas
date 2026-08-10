package com.codecanvas.snippetservice.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codecanvas.snippetservice.entity.Snippet;
import com.codecanvas.snippetservice.enums.Status;
import com.codecanvas.snippetservice.enums.Visibility;

@Repository
public interface SnippetRepository extends JpaRepository<Snippet, UUID> {

    List<Snippet> findByUserId(UUID userId);

    List<Snippet> findByVisibilityAndStatus(
            Visibility visibility,
            Status status
    );


    List<Snippet> findByUserIdAndStatus(
            UUID userId,
            Status status
    );

    boolean existsBySnippetIdAndUserId(
            UUID snippetId,
            UUID userId
    );

    List<Snippet> findBySnippetIdIn(List<UUID> snippetIds);

}