package com.codecanvas.snippetservice.service;

import com.codecanvas.snippetservice.dto.request.CreateCommentRequest;
import com.codecanvas.snippetservice.dto.request.UpdateCommentRequest;
import com.codecanvas.snippetservice.dto.response.CommentResponse;
import java.util.List;
import java.util.UUID;

public interface CommentService {

  CommentResponse addComment(UUID snippetId, CreateCommentRequest request);

  List<CommentResponse> getComments(UUID snippetId);

  CommentResponse updateComment(UUID commentId, UpdateCommentRequest request);

  CommentResponse deleteComment(UUID commentId);

  CommentResponse replyToComment(UUID commentId, CreateCommentRequest request);

  List<CommentResponse> getReplies(UUID commentId);
}
