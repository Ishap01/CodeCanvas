package com.codecanvas.aiservice.repository;

import com.codecanvas.aiservice.entity.AIHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AIHistoryRepository
        extends JpaRepository<AIHistory, UUID> {

    List<AIHistory> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
