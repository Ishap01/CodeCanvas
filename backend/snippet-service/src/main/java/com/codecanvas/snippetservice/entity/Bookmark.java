package com.codecanvas.snippetservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "bookmarks",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "snippet_id",
                                "user_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "bookmark_id")
    private UUID bookmarkId;

    @Column(name = "snippet_id", nullable = false)
    private UUID snippetId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(
            name = "bookmarked_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime bookmarkedAt;

    @PrePersist
    public void prePersist() {
        bookmarkedAt = LocalDateTime.now();
    }
}