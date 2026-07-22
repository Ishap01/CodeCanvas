package com.codecanvas.snippetservice.repository;

import com.codecanvas.snippetservice.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TagRepository extends JpaRepository<Tag, UUID> {
}