package com.codecanvas.snippetservice.service.impl;

import com.codecanvas.snippetservice.dto.request.CreateCommentRequest;
import com.codecanvas.snippetservice.dto.request.UpdateCommentRequest;
import com.codecanvas.snippetservice.dto.response.CommentResponse;
import com.codecanvas.snippetservice.entity.Comment;
import com.codecanvas.snippetservice.entity.Snippet;
import com.codecanvas.snippetservice.exception.ResourceNotFoundException;
import com.codecanvas.snippetservice.repository.CommentRepository;
import com.codecanvas.snippetservice.repository.SnippetRepository;
import com.codecanvas.snippetservice.security.AuthenticatedUser;
import com.codecanvas.snippetservice.service.CommentService;
import com.codecanvas.snippetservice.service.SearchIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final SnippetRepository snippetRepository;
    private final SearchIndexService searchIndexService;

    private UUID getCurrentUserId() {

        AuthenticatedUser authenticatedUser =
                (AuthenticatedUser) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        return authenticatedUser.getUserId();
    }

    private Snippet getSnippet(UUID snippetId) {

        return snippetRepository.findById(snippetId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Snippet not found."
                        ));
    }

    private Comment getComment(UUID commentId) {

        return commentRepository.findById(commentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Comment not found."
                        ));
    }

    private CommentResponse mapToResponse(Comment comment) {

        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .snippetId(comment.getSnippet().getSnippetId())
                .userId(comment.getUserId())
                .parentCommentId(
                        comment.getParentComment() == null
                                ? null
                                : comment.getParentComment().getCommentId()
                )
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .success(true)
                .build();
    }

    @Override
    public CommentResponse addComment(
            UUID snippetId,
            CreateCommentRequest request) {

        UUID userId = getCurrentUserId();

        Snippet snippet = getSnippet(snippetId);

        Comment comment = Comment.builder()
                .content(request.getContent())
                .userId(userId)
                .snippet(snippet)
                .parentComment(null)
                .build();

        Comment savedComment =
                commentRepository.save(comment);

        snippet.setCommentCount(
                snippet.getCommentCount() + 1
        );

        snippetRepository.save(snippet);

        searchIndexService.indexSnippet(snippet);

        CommentResponse response =
                mapToResponse(savedComment);

        response.setMessage(
                "Comment added successfully."
        );

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(UUID snippetId) {

        Snippet snippet = getSnippet(snippetId);

        return commentRepository
                .findBySnippetAndParentCommentIsNullOrderByCreatedAtAsc(snippet)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CommentResponse updateComment(
            UUID commentId,
            UpdateCommentRequest request) {

        Comment comment = getComment(commentId);

        UUID currentUserId = getCurrentUserId();

        if (!comment.getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException(
                    "You can only edit your own comment."
            );
        }

        comment.setContent(request.getContent());

        Comment updatedComment =
                commentRepository.save(comment);

        CommentResponse response =
                mapToResponse(updatedComment);

        response.setMessage(
                "Comment updated successfully."
        );

        return response;
    }

    @Override
    public CommentResponse deleteComment(UUID commentId) {

        Comment comment = getComment(commentId);

        UUID currentUserId = getCurrentUserId();

        if (!comment.getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException(
                    "You can only delete your own comment."
            );
        }

        Snippet snippet = comment.getSnippet();

        commentRepository.delete(comment);

        snippet.setCommentCount(
                Math.max(
                        0,
                        snippet.getCommentCount() - 1
                )
        );

        snippetRepository.save(snippet);

        searchIndexService.indexSnippet(snippet);

        return CommentResponse.builder()
                .success(true)
                .message("Comment deleted successfully.")
                .build();
    }

    @Override
    public CommentResponse replyToComment(
            UUID commentId,
            CreateCommentRequest request) {

        UUID userId = getCurrentUserId();

        Comment parentComment = getComment(commentId);

        Snippet snippet = parentComment.getSnippet();

        Comment reply = Comment.builder()
                .content(request.getContent())
                .userId(userId)
                .snippet(snippet)
                .parentComment(parentComment)
                .build();

        Comment savedReply =
                commentRepository.save(reply);

        snippet.setCommentCount(
                snippet.getCommentCount() + 1
        );

        snippetRepository.save(snippet);

        searchIndexService.indexSnippet(snippet);

        CommentResponse response =
                mapToResponse(savedReply);

        response.setMessage(
                "Reply added successfully."
        );

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getReplies(
            UUID commentId) {

        Comment parentComment =
                getComment(commentId);

        return commentRepository
                .findByParentCommentOrderByCreatedAtAsc(parentComment)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
}