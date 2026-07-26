package com.codecanvas.searchservice.repository;

import com.codecanvas.searchservice.dto.response.SearchHistoryResponse;
import com.codecanvas.searchservice.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.UUID;
@Repository
public interface SearchHistoryRepository
        extends JpaRepository<SearchHistory, UUID> {

    List<SearchHistory> findByUserIdOrderBySearchedAtDesc(UUID userId);

    @Query("""
       SELECT sh.keyword, COUNT(sh.keyword)
       FROM SearchHistory sh
       GROUP BY sh.keyword
       ORDER BY COUNT(sh.keyword) DESC
       """)
    List<Object[]> findPopularSearches();
}