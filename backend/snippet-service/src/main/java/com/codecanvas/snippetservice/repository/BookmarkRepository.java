package com.codecanvas.snippetservice.repository;

import com.codecanvas.snippetservice.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {
}