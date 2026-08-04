package com.codecanvas.snippetservice.kafka.mapper;

import com.codecanvas.snippetservice.entity.Snippet;
import com.codecanvas.snippetservice.entity.SnippetTag;
import com.codecanvas.snippetservice.kafka.event.SnippetCreatedEvent;
import org.springframework.stereotype.Component;
import com.codecanvas.snippetservice.kafka.event.SnippetUpdatedEvent;

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
}