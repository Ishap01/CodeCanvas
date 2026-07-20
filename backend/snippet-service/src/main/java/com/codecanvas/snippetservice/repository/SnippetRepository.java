package com.codecanvas.snippetservice.repository;

import com.codecanvas.snippetservice.entity.Snippet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SnippetRepository extends JpaRepository<Snippet, UUID> {
}