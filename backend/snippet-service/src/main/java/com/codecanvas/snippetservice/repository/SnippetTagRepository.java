package com.codecanvas.snippetservice.repository;

import com.codecanvas.snippetservice.entity.SnippetTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SnippetTagRepository extends JpaRepository<SnippetTag, UUID> {
}