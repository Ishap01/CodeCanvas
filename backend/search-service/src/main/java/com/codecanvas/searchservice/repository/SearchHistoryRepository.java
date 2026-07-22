package com.codecanvas.searchservice.repository;

import com.codecanvas.searchservice.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SearchHistoryRepository
        extends JpaRepository<SearchHistory, UUID> {
}
