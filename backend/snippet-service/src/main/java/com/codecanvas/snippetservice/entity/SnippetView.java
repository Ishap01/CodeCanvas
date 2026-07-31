package com.codecanvas.snippetservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "snippet_views",
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
public class SnippetView {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "view_id")
    private UUID viewId;

    @Column(name = "snippet_id", nullable = false)
    private UUID snippetId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "viewed_at", nullable = false, updatable = false)
    private LocalDateTime viewedAt;

    @PrePersist
    public void prePersist() {
        viewedAt = LocalDateTime.now();
    }
}