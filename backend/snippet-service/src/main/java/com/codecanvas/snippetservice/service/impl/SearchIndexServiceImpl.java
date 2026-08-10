package com.codecanvas.snippetservice.service.impl;

import com.codecanvas.snippetservice.client.SearchServiceClient;
import com.codecanvas.snippetservice.dto.request.IndexSnippetRequest;
import com.codecanvas.snippetservice.entity.Snippet;
import com.codecanvas.snippetservice.service.SearchIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SearchIndexServiceImpl implements SearchIndexService {

    private final SearchServiceClient searchServiceClient;

    @Override
    public void indexSnippet(Snippet snippet) {

        searchServiceClient.indexSnippet(
                buildIndexRequest(snippet)
        );
    }

    private IndexSnippetRequest buildIndexRequest(Snippet snippet) {
        return IndexSnippetRequest.builder()
                .snippetId(snippet.getSnippetId())
                .title(snippet.getTitle())
                .description(snippet.getDescription())
                .language(snippet.getLanguage())
                .framework(snippet.getFramework())
                .category(snippet.getCategory().getCategoryName())
                .tags(
                        snippet.getSnippetTags()
                                .stream()
                                .map(snippetTag ->
                                        snippetTag.getTag().getTagName())
                                .toList()
                )
                .likes(snippet.getLikeCount())
                .views(snippet.getViewCount())
                .bookmarks(snippet.getBookmarkCount())
                .forks(snippet.getForkCount())
                .createdAt(snippet.getCreatedAt())
                .previewImageUrl(snippet.getPreviewImageUrl())
                .build();
    }

    @Override
    public void deleteSnippet(UUID snippetId) {

        searchServiceClient.deleteSnippet(snippetId);

    }
}