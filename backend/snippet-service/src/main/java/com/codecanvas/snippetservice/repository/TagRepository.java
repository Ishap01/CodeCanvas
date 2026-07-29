package com.codecanvas.snippetservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codecanvas.snippetservice.entity.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    Optional<Tag> findByTagNameIgnoreCase(String name);

    boolean existsByTagNameIgnoreCase(String name);

    List<Tag> findByTagNameInIgnoreCase(List<String> names);
}