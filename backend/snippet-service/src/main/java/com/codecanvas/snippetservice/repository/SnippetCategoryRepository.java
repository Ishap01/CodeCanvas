package com.codecanvas.snippetservice.repository;

import com.codecanvas.snippetservice.entity.SnippetCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SnippetCategoryRepository extends JpaRepository<SnippetCategory, UUID> {
}