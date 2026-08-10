package com.codecanvas.snippetservice.kafka.mapper;

import com.codecanvas.snippetservice.entity.Snippet;
import com.codecanvas.snippetservice.entity.SnippetTag;
import com.codecanvas.snippetservice.kafka.event.*;
import org.springframework.stereotype.Component;
import com.codecanvas.snippetservice.entity.Comment;

import com.codecanvas.snippetservice.kafka.event.CommentCreatedEvent;

import java.time.LocalDateTime;
import java.util.UUID;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class SnippetEventMapper {

    /*
     * Converts Snippet entity into
     * SnippetCreatedEvent.
     *
     * This mapper is used only for Kafka events.
     */
    public SnippetCreatedEvent toSnippetCreatedEvent(
            Snippet snippet) {

        if (snippet == null) {
            return null;
        }

        List<String> tags = new ArrayList<>();

        if (snippet.getSnippetTags() != null) {

            for (SnippetTag snippetTag
                    : snippet.getSnippetTags()) {

                if (snippetTag != null
                        && snippetTag.getTag() != null
                        && snippetTag.getTag().getTagName() != null) {

                    tags.add(
                            snippetTag.getTag().getTagName()
                    );
                }
            }
        }

        return new SnippetCreatedEvent(

                snippet.getSnippetId(),

                snippet.getUserId(),

                snippet.getTitle(),

                snippet.getDescription(),

                snippet.getCode(),

                snippet.getLanguage(),

                snippet.getFramework(),

                snippet.getCategory() != null
                        ? snippet.getCategory().getCategoryName()
                        : null,

                tags,

                snippet.getVisibility(),

                snippet.getPreviewImageUrl(),

                snippet.getLikeCount(),

                snippet.getViewCount(),

                snippet.getBookmarkCount(),

                snippet.getForkCount(),

                snippet.getCreatedAt()
        );
    }

    public SnippetUpdatedEvent toSnippetUpdatedEvent(
            Snippet snippet) {

        if (snippet == null) {
            return null;
        }

        List<String> tags = new ArrayList<>();

        if (snippet.getSnippetTags() != null) {

            for (SnippetTag snippetTag
                    : snippet.getSnippetTags()) {

                if (snippetTag != null
                        && snippetTag.getTag() != null
                        && snippetTag.getTag().getTagName() != null) {

                    tags.add(
                            snippetTag.getTag().getTagName()
                    );
                }
            }
        }

        return new SnippetUpdatedEvent(

                snippet.getSnippetId(),

                snippet.getUserId(),

                snippet.getTitle(),

                snippet.getDescription(),

                snippet.getCode(),

                snippet.getLanguage(),

                snippet.getFramework(),

                snippet.getCategory() != null
                        ? snippet.getCategory().getCategoryName()
                        : null,

                tags,

                snippet.getVisibility(),

                snippet.getPreviewImageUrl(),

                snippet.getLikeCount(),

                snippet.getViewCount(),

                snippet.getBookmarkCount(),

                snippet.getForkCount(),

                snippet.getCreatedAt()
        );
    }

    public SnippetLikedEvent toSnippetLikedEvent(
            Snippet snippet,
            UUID likedBy) {

        if (snippet == null) {
            return null;
        }

        return new SnippetLikedEvent(

                snippet.getSnippetId(),

                likedBy,

                snippet.getUserId(),

                snippet.getLikeCount(),

                LocalDateTime.now()
        );
    }





    /*
     * Converts Snippet entity into
     * SnippetBookmarkedEvent.
     */
    public SnippetBookmarkedEvent toSnippetBookmarkedEvent(
            Snippet snippet,
            UUID userId) {

        if (snippet == null) {
            return null;
        }

        return new SnippetBookmarkedEvent(

                // Bookmarked snippet
                snippet.getSnippetId(),

                // User who bookmarked
                userId,

                // Updated bookmark count
                snippet.getBookmarkCount(),

                // Event time
                LocalDateTime.now()
        );
    }

    /*
     * Converts Comment entity into
     * CommentCreatedEvent.
     */
    public CommentCreatedEvent toCommentCreatedEvent(
            Comment comment) {

        if (comment == null) {
            return null;
        }

        return new CommentCreatedEvent(

                comment.getCommentId(),

                comment.getSnippet().getSnippetId(),

                comment.getSnippet().getUserId(),

                comment.getUserId(),

                comment.getContent(),

                comment.getCreatedAt()
        );
    }


    /*
     * Converts Reply entity into
     * ReplyCreatedEvent.
     */
    public ReplyCreatedEvent toReplyCreatedEvent(
            Comment reply) {

        if (reply == null) {
            return null;
        }

        return new ReplyCreatedEvent(

                // Reply id
                reply.getCommentId(),

                // Parent comment id
                reply.getParentComment().getCommentId(),

                // Owner of parent comment
                reply.getParentComment().getUserId(),

                // User who replied
                reply.getUserId(),

                // Reply content
                reply.getContent(),

                // Reply creation time
                reply.getCreatedAt()
        );
    }


    /*
     * Converts forked snippets into
     * SnippetForkedEvent.
     */
    public SnippetForkedEvent toSnippetForkedEvent(
            Snippet originalSnippet,
            Snippet forkedSnippet) {

        if (originalSnippet == null || forkedSnippet == null) {
            return null;
        }

        return new SnippetForkedEvent(

                // Original snippet
                originalSnippet.getSnippetId(),

                // Newly created fork
                forkedSnippet.getSnippetId(),

                // Owner of original snippet
                originalSnippet.getUserId(),

                // User who created the fork
                forkedSnippet.getUserId(),

                // Updated fork count
                originalSnippet.getForkCount(),

                // Event timestamp
                LocalDateTime.now()
        );
    }
}