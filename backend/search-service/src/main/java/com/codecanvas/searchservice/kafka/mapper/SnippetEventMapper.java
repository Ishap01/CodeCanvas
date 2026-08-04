package com.codecanvas.searchservice.kafka.mapper;

import com.codecanvas.searchservice.dto.request.IndexSnippetRequest;
import com.codecanvas.searchservice.kafka.event.SnippetCreatedEvent;
import org.springframework.stereotype.Component;
import com.codecanvas.searchservice.kafka.event.SnippetUpdatedEvent;

@Component
public class SnippetEventMapper {

    public IndexSnippetRequest toIndexSnippetRequest(
            SnippetCreatedEvent event) {

        if (event == null) {
            return null;
        }

        return IndexSnippetRequest.builder()

                .snippetId(event.getSnippetId())

                .title(event.getTitle())

                .description(event.getDescription())

                .language(event.getLanguage())

                .framework(event.getFramework())

                .category(event.getCategory())

                .tags(event.getTags())

                .likes(event.getLikeCount())

                .views(event.getViewCount())

                .bookmarks(event.getBookmarkCount())

                .forks(event.getForkCount())

                .createdAt(event.getCreatedAt())

                .previewImageUrl(event.getPreviewImageUrl())

                .build();
    }

    public IndexSnippetRequest toIndexSnippetRequest(
            SnippetUpdatedEvent event) {

        if (event == null) {
            return null;
        }

        return IndexSnippetRequest.builder()

                .snippetId(event.getSnippetId())

                .title(event.getTitle())

                .description(event.getDescription())

                .language(event.getLanguage())

                .framework(event.getFramework())

                .category(event.getCategory())

                .tags(event.getTags())

                .likes(event.getLikeCount())

                .views(event.getViewCount())

                .bookmarks(event.getBookmarkCount())

                .forks(event.getForkCount())

                .createdAt(event.getCreatedAt())

                .previewImageUrl(event.getPreviewImageUrl())

                .build();
    }


}