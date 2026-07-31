package com.codecanvas.snippetservice.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {

    private UUID commentId;

    private UUID snippetId;

    private UUID userId;

    private UUID parentCommentId;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean success;

    private String message;
}