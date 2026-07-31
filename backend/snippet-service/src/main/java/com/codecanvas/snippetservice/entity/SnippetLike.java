package com.codecanvas.snippetservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "snippet_likes",
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
public class SnippetLike {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "like_id")
    private UUID likeId;

    @Column(name = "snippet_id", nullable = false)
    private UUID snippetId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "liked_at", nullable = false, updatable = false)
    private LocalDateTime likedAt;

    @PrePersist
    public void prePersist() {
        likedAt = LocalDateTime.now();
    }
}