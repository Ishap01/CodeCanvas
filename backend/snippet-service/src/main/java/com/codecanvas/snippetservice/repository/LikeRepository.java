package com.codecanvas.snippetservice.repository;

import com.codecanvas.snippetservice.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {
}