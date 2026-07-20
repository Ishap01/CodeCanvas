package com.codecanvas.searchservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "search_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "search_id")
    private UUID searchId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false)
    private String keyword;

    @Column(length = 50)
    private String language;

    @Column(length = 50)
    private String framework;

    @Column(name = "searched_at")
    private LocalDateTime searchedAt;

    @PrePersist
    public void prePersist() {
        searchedAt = LocalDateTime.now();
    }
}