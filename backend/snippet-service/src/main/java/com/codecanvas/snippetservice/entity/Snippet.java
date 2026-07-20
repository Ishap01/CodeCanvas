package com.codecanvas.snippetservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "snippets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Snippet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "snippet_id")
    private UUID snippetId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 50)
    private String language;

    @Column(length = 50)
    private String framework;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String code;

    @Column(name = "preview_image_url", length = 500)
    private String previewImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility;

    private Integer views;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        views = 0;

        if (visibility == null) {
            visibility = Visibility.PUBLIC;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}