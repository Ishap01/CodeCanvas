package com.codecanvas.snippetservice.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.codecanvas.snippetservice.enums.Status;
import com.codecanvas.snippetservice.enums.Visibility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnippetResponse {

    private UUID snippetId;

    private String title;

    private String description;

    /*
     * Backward compatibility.
     * Existing frontend still reads this field.
     */
    private String code;

    /*
     * Multiple source files.
     */
    private List<SnippetFileResponse> files;

    private String language;

    private String framework;

    /*
     * URL sent to frontend for displaying
     * the preview image.
     */
    private String previewImageUrl;

    /*
     * Cloudinary public ID.
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
}