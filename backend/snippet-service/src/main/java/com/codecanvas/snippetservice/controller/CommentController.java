package com.codecanvas.snippetservice.controller;

import com.codecanvas.snippetservice.dto.request.CreateCommentRequest;
import com.codecanvas.snippetservice.dto.request.UpdateCommentRequest;
import com.codecanvas.snippetservice.dto.response.CommentResponse;
import com.codecanvas.snippetservice.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/snippets/{snippetId}/comments")
    public CommentResponse addComment(
            @PathVariable UUID snippetId,
            @Valid @RequestBody CreateCommentRequest request) {

        return commentService.addComment(
                snippetId,
                request
        );
    }

    @GetMapping("/snippets/{snippetId}/comments")
    public List<CommentResponse> getComments(
            @PathVariable UUID snippetId) {

        return commentService.getComments(
                snippetId
        );
    }

    @PutMapping("/comments/{commentId}")
    public CommentResponse updateComment(
            @PathVariable UUID commentId,
            @Valid @RequestBody UpdateCommentRequest request) {
        System.out.println("Inside updateComment");

        return commentService.updateComment(
                commentId,
                request
        );
    }

    @DeleteMapping("/comments/{commentId}")
    public CommentResponse deleteComment(
            @PathVariable UUID commentId) {

        return commentService.deleteComment(
                commentId
        );
    }

    @PostMapping("/comments/{commentId}/replies")
    public CommentResponse replyToComment(
            @PathVariable UUID commentId,
            @Valid @RequestBody CreateCommentRequest request) {

        return commentService.replyToComment(
                commentId,
                request
        );
    }

    @GetMapping("/comments/{commentId}/replies")
    public List<CommentResponse> getReplies(
            @PathVariable UUID commentId) {

        return commentService.getReplies(
                commentId
        );
    }
}