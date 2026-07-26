package com.codecanvas.snippetservice.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.codecanvas.snippetservice.enums.Status;
import com.codecanvas.snippetservice.enums.Visibility;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "snippets")
public class Snippet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "snippet_id")
    private UUID snippetId;

    @Column(
            name = "user_id",
            nullable = false
    )
    private UUID userId;

    @Column(
            name = "title",
            nullable = false,
            length = 200
    )
    private String title;

    @Column(
            name = "description",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String description;

    @Column(
            name = "code",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String code;

    @Column(
            name = "language",
            nullable = false,
            length = 50
    )
    private String language;

    @Column(
            name = "framework",
            length = 100
    )
    private String framework;

    /*
     * Cloudinary secure URL.
     *
     * Frontend uses this URL to display
     * the snippet preview image.
     */
    @Column(
            name = "preview_image_url",
            length = 1000
    )
    private String previewImageUrl;

    /*
     * Cloudinary public ID.
     *
     * Backend uses this value to replace
     * or delete the image from Cloudinary.
     */
    @Column(
            name = "preview_image_public_id",
            length = 500
    )
    private String previewImagePublicId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "visibility",
            nullable = false,
            length = 20
    )
    private Visibility visibility;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private Status status;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "category_id",
            nullable = false
    )
    private Category category;

    @OneToMany(
            mappedBy = "snippet",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<SnippetTag> snippetTags =
            new ArrayList<>();

    @Column(
            name = "view_count",
            nullable = false
    )
    private long viewCount;

    @Column(
            name = "like_count",
            nullable = false
    )
    private long likeCount;

    @Column(
            name = "bookmark_count",
            nullable = false
    )
    private long bookmarkCount;

    @Column(
            name = "fork_count",
            nullable = false
    )
    private long forkCount;

    @Column(name = "parent_snippet_id")
    private UUID parentSnippetId;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    public Snippet() {
    }

    @PrePersist
    public void prePersist() {

        LocalDateTime currentTime =
                LocalDateTime.now();

        if (createdAt == null) {
            createdAt = currentTime;
        }

        updatedAt = currentTime;

        if (visibility == null) {
            visibility = Visibility.PUBLIC;
        }

        if (status == null) {
            status = Status.ACTIVE;
        }

        viewCount = Math.max(viewCount, 0);
        likeCount = Math.max(likeCount, 0);
        bookmarkCount = Math.max(bookmarkCount, 0);
        forkCount = Math.max(forkCount, 0);
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addTag(Tag tag) {

        if (tag == null) {
            throw new IllegalArgumentException(
                    "Tag cannot be null"
            );
        }

        if (snippetTags == null) {
            snippetTags = new ArrayList<>();
        }

        for (SnippetTag existingSnippetTag
                : snippetTags) {

            if (existingSnippetTag.getTag() != null
                    && existingSnippetTag
                    .getTag()
                    .getTagId() != null
                    && tag.getTagId() != null
                    && existingSnippetTag
                    .getTag()
                    .getTagId()
                    .equals(tag.getTagId())) {

                return;
            }
        }

        SnippetTag snippetTag =
                new SnippetTag();

        snippetTag.setSnippet(this);
        snippetTag.setTag(tag);

        snippetTags.add(snippetTag);
    }

    public void removeTag(Tag tag) {

        if (tag == null
                || snippetTags == null) {

            return;
        }

        snippetTags.removeIf(snippetTag -> {

            Tag existingTag =
                    snippetTag.getTag();

            boolean sameTag =
                    existingTag != null
                            && existingTag
                            .getTagId() != null
                            && tag.getTagId() != null
                            && existingTag
                            .getTagId()
                            .equals(tag.getTagId());

            if (sameTag) {
                snippetTag.setSnippet(null);
                snippetTag.setTag(null);
            }

            return sameTag;
        });
    }

    public void clearTags() {

        if (snippetTags == null) {
            snippetTags = new ArrayList<>();
            return;
        }

        for (SnippetTag snippetTag
                : snippetTags) {

            snippetTag.setSnippet(null);
            snippetTag.setTag(null);
        }

        snippetTags.clear();
    }

    public UUID getSnippetId() {
        return snippetId;
    }

    public void setSnippetId(
            UUID snippetId) {

        this.snippetId = snippetId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(
            UUID userId) {

        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(
            String title) {

        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description) {

        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(
            String code) {

        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(
            String language) {

        this.language = language;
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(
            String framework) {

        this.framework = framework;
    }

    public String getPreviewImageUrl() {
        return previewImageUrl;
    }

    public void setPreviewImageUrl(
            String previewImageUrl) {

        this.previewImageUrl =
                previewImageUrl;
    }

    public String getPreviewImagePublicId() {
        return previewImagePublicId;
    }

    public void setPreviewImagePublicId(
            String previewImagePublicId) {

        this.previewImagePublicId =
                previewImagePublicId;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(
            Visibility visibility) {

        this.visibility = visibility;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(
            Status status) {

        this.status = status;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(
            Category category) {

        this.category = category;
    }

    public List<SnippetTag> getSnippetTags() {
        return snippetTags;
    }

    public void setSnippetTags(
            List<SnippetTag> snippetTags) {

        this.snippetTags =
                new ArrayList<>();

        if (snippetTags == null) {
            return;
        }

        for (SnippetTag snippetTag
                : snippetTags) {

            if (snippetTag == null) {
                continue;
            }

            snippetTag.setSnippet(this);
            this.snippetTags.add(
                    snippetTag
            );
        }
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(
            long viewCount) {

        this.viewCount = viewCount;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(
            long likeCount) {

        this.likeCount = likeCount;
    }

    public long getBookmarkCount() {
        return bookmarkCount;
    }

    public void setBookmarkCount(
            long bookmarkCount) {

        this.bookmarkCount =
                bookmarkCount;
    }

    public long getForkCount() {
        return forkCount;
    }

    public void setForkCount(
            long forkCount) {

        this.forkCount = forkCount;
    }

    public UUID getParentSnippetId() {
        return parentSnippetId;
    }

    public void setParentSnippetId(
            UUID parentSnippetId) {

        this.parentSnippetId =
                parentSnippetId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt) {

        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt) {

        this.updatedAt = updatedAt;
    }
}