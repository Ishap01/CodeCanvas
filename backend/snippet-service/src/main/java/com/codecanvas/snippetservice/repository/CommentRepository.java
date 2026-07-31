package com.codecanvas.snippetservice.repository;

import com.codecanvas.snippetservice.entity.Comment;
import com.codecanvas.snippetservice.entity.Snippet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository
        extends JpaRepository<Comment, UUID> {

    List<Comment> findBySnippetAndParentCommentIsNullOrderByCreatedAtAsc(
            Snippet snippet
    );

    List<Comment> findByParentCommentOrderByCreatedAtAsc(
            Comment parentComment
    );

    long countBySnippet(Snippet snippet);
}