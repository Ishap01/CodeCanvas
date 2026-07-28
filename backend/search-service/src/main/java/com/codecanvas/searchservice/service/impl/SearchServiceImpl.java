package com.codecanvas.searchservice.service.impl;

import com.codecanvas.searchservice.document.SearchDocument;
import com.codecanvas.searchservice.dto.request.IndexSnippetRequest;
import com.codecanvas.searchservice.dto.request.SearchRequest;
import com.codecanvas.searchservice.dto.response.*;
import com.codecanvas.searchservice.entity.SearchHistory;
import com.codecanvas.searchservice.repository.SearchDocumentRepository;
import com.codecanvas.searchservice.repository.SearchHistoryRepository;
import com.codecanvas.searchservice.service.search.SearchService;
import com.codecanvas.searchservice.service.search.ElasticSearchQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.codecanvas.searchservice.service.search.SearchPage;


import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final ElasticSearchQueryService elasticSearchQueryService;
    private final SearchDocumentRepository searchDocumentRepository;


    @Override
    public SearchPageResponse search(SearchRequest request, UUID userId) {

        // Save search history
        saveSearchHistory(userId, request);

        // Get paginated result from Elasticsearch
        SearchPage<SearchDocument> result =
                elasticSearchQueryService.search(request);

        // Convert SearchDocument -> SearchResponse
        List<SearchResponse> snippets =
                result.getContent()
                        .stream()
                        .map(document ->
                                SearchResponse.builder()
                                        .snippetId(document.getSnippetId())
                                        .title(document.getTitle())
                                        .description(document.getDescription())
                                        .language(document.getLanguage())
                                        .framework(document.getFramework())
                                        .previewImageUrl(document.getPreviewImageUrl())
                                        .views(document.getViews())
                                        .likes(document.getLikes())
                                        .forks(0L)
                                        .bookmarked(false)
                                        .build()
                        )
                        .toList();

        // Return paginated response
        return SearchPageResponse.builder()
                .snippets(snippets)
                .currentPage(result.getCurrentPage())
                .pageSize(result.getPageSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .first(result.isFirst())
                .last(result.isLast())
                .build();
    }
    @Override
    public void indexSnippet(IndexSnippetRequest request) {

        SearchDocument document =
                SearchDocument.builder()
                        .snippetId(request.getSnippetId())
                        .title(request.getTitle())
                        .description(request.getDescription())
                        .language(request.getLanguage())
                        .framework(request.getFramework())
                        .category(request.getCategory())
                        .tags(request.getTags())
                        .likes(request.getLikes())
                        .views(request.getViews())
                        .previewImageUrl(request.getPreviewImageUrl())
                        .build();

        searchDocumentRepository.save(document);
    }

    @Override
    public List<AutocompleteResponse> getSuggestions(String keyword) {

        return elasticSearchQueryService
                .autocomplete(keyword)
                .stream()
                .map(document -> AutocompleteResponse.builder()
                        .snippetId(document.getSnippetId())
                        .suggestion(document.getTitle())
                        .build())
                .toList();
    }
    @Override
    public List<PopularSearchResponse> getPopularSearches() {

        return searchHistoryRepository.findPopularSearches()
                .stream()
                .map(result -> PopularSearchResponse.builder()
                        .keyword((String) result[0])
                        .count((Long) result[1])
                        .build())
                .toList();
    }
    @Override
    public void saveSearchHistory(UUID userId, SearchRequest request) {

        SearchHistory history = SearchHistory.builder()
                .userId(userId)
                .keyword(request.getKeyword())
                .language(request.getLanguage())
                .framework(request.getFramework())
                .build();

        searchHistoryRepository.save(history);
    }

    @Override
    public List<SearchHistoryResponse> getUserSearchHistory(UUID userId) {

        List<SearchHistory> historyList =
                searchHistoryRepository.findByUserIdOrderBySearchedAtDesc(userId);

        return historyList.stream()
                .map(history -> SearchHistoryResponse.builder()
                        .keyword(history.getKeyword())
                        .language(history.getLanguage())
                        .framework(history.getFramework())
                        .searchedAt(history.getSearchedAt())
                        .build())
                .toList();
    }
}

