package com.codecanvas.snippetservice.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codecanvas.snippetservice.entity.SnippetTag;

@Repository
public interface SnippetTagRepository
        extends JpaRepository<SnippetTag, UUID> {

    List<SnippetTag> findBySnippetSnippetId(UUID snippetId);

    void deleteBySnippetSnippetId(UUID snippetId);

    boolean existsBySnippetSnippetIdAndTagTagId(
            UUID snippetId,
            UUID tagId
    );
}