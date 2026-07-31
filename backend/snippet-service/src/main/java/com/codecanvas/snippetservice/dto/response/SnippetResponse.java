package com.codecanvas.snippetservice.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.codecanvas.snippetservice.enums.Status;
import com.codecanvas.snippetservice.enums.Visibility;

public class SnippetResponse {

    private UUID snippetId;
    private String title;
    private String description;
    private String code;
    private String language;
    private String framework;


    /*
     * URL sent to frontend for displaying
     * the preview image.
     */
    private String previewImageUrl;

    /*
     * Cloudinary public ID.
     *
     * This is generally not required by
     * frontend, but we are keeping it in
     * response for now because your backend
     * may need it during testing.
     */
    private String previewImagePublicId;

    private Visibility visibility;
    private Status status;
    private UUID userId;
    private UUID categoryId;
    private String categoryName;
    private List<String> tags;
    private long viewCount;
    private long likeCount;
    private long bookmarkCount;
    private long forkCount;
    private long commentCount;
    private UUID parentSnippetId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SnippetResponse() {
    }

    public SnippetResponse(
            UUID snippetId,
            String title,
            String description,
            String code,
            String language,
            String framework,
            String previewImageUrl,
            String previewImagePublicId,
            Visibility visibility,
            Status status,
            UUID userId,
            UUID categoryId,
            String categoryName,
            List<String> tags,
            long viewCount,
            long likeCount,
            long bookmarkCount,
            long forkCount,
            long commentCount,
            UUID parentSnippetId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.snippetId = snippetId;
        this.title = title;
        this.description = description;
        this.code = code;
        this.language = language;
        this.framework = framework;
        this.previewImageUrl =
                previewImageUrl;
        this.previewImagePublicId =
                previewImagePublicId;
        this.visibility = visibility;
        this.status = status;
        this.userId = userId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.tags = tags;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.bookmarkCount =
                bookmarkCount;
        this.forkCount = forkCount;
        this.commentCount = commentCount;
        this.parentSnippetId =
                parentSnippetId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getSnippetId() {
        return snippetId;
    }

    public void setSnippetId(
            UUID snippetId) {

        this.snippetId = snippetId;
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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(
            UUID userId) {

        this.userId = userId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(
            UUID categoryId) {

        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(
            String categoryName) {

        this.categoryName =
                categoryName;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(
            List<String> tags) {

        this.tags = tags;
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


    public long getCommentCount() {
        return commentCount;
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

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }
}