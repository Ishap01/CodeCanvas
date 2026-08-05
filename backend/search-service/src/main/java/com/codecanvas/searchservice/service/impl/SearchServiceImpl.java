package com.codecanvas.searchservice.service.impl;


import com.codecanvas.searchservice.document.UserDocument;
import com.codecanvas.searchservice.kafka.event.UserRegisteredEvent;
import com.codecanvas.searchservice.kafka.event.UserUpdatedEvent;
import com.codecanvas.searchservice.repository.UserDocumentRepository;
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



    /*
     * =========================================================
     * USER ELASTICSEARCH REPOSITORY
     * =========================================================
     */
    private final UserDocumentRepository userDocumentRepository;


    @Override
    public SearchPageResponse search(SearchRequest request, UUID userId) {

        // Save search history
        if (request.getKeyword() != null &&
                !request.getKeyword().isBlank()) {

            saveSearchHistory(userId, request);

        }

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

                                        .category(document.getCategory())
                                        .tags(document.getTags())

                                        .previewImageUrl(document.getPreviewImageUrl())

                                        .likes(safeCount(document.getLikes()))
                                        .views(safeCount(document.getViews()))
                                        .forks(safeCount(document.getForks()))
                                        .bookmarks(safeCount(document.getBookmarks()))

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

    private Long safeCount(Long value) {
        return value == null ? 0L : value;
    }
    @Override
    public void indexSnippet(IndexSnippetRequest request) {

        SearchDocument document =
                SearchDocument.builder()
                        .id(request.getSnippetId().toString())
                        .snippetId(request.getSnippetId())
                        .title(request.getTitle())
                        .description(request.getDescription())
                        .language(request.getLanguage())
                        .framework(request.getFramework())
                        .category(request.getCategory())
                        .tags(request.getTags())
                        .likes(request.getLikes())
                        .views(request.getViews())
                        .bookmarks(request.getBookmarks())
                        .forks(request.getForks())
                        .createdAt(request.getCreatedAt())
                        .previewImageUrl(request.getPreviewImageUrl())
                        .build();

        System.out.println("Request createdAt  : " + request.getCreatedAt());
        System.out.println("Document createdAt : " + document.getCreatedAt());


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

        if (request.getKeyword() == null ||
                request.getKeyword().isBlank()) {
            return;
        }

        SearchHistory history = SearchHistory.builder()
                .userId(userId)
                .keyword(request.getKeyword().trim())
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

    @Override
    public void deleteSnippet(UUID snippetId) {

        searchDocumentRepository.deleteById(
                snippetId.toString()
        );
    }

    /*
     * =========================================================
     * USER INDEXING
     * Index newly registered user into Elasticsearch
     * =========================================================
     */
    @Override
    public void indexUser(UserRegisteredEvent event) {

        UserDocument document =
                UserDocument.builder()
                        .id(event.getUserId().toString())
                        .userId(event.getUserId())
                        .fullName(event.getFullName())
                        .username(event.getUsername())
                        .email(event.getEmail())
                        .bio(event.getBio())
                        .profileImage(event.getProfileImage())
                        .build();

        userDocumentRepository.save(document);
    }

    /*
     * =========================================================
     * USER UPDATE
     * Update existing user in Elasticsearch
     * =========================================================
     */
    @Override
    public void updateUser(UserUpdatedEvent event) {

        UserDocument document =
                UserDocument.builder()
                        .id(event.getUserId().toString())
                        .userId(event.getUserId())
                        .fullName(event.getFullName())
                        .username(event.getUsername())
                        .email(event.getEmail())
                        .bio(event.getBio())
                        .profileImage(event.getProfileImage())
                        .build();

        userDocumentRepository.save(document);
    }

    /*
     * =========================================================
     * USER DELETE
     * Remove user from Elasticsearch
     * =========================================================
     */
    @Override
    public void deleteUser(UUID userId) {

        userDocumentRepository.deleteByUserId(userId);
    }


    /*
     * =========================================================
     * USER SEARCH
     * Search users by full name, username or bio
     * =========================================================
     */
    @Override
    public List<UserSearchResponse> searchUsers(String keyword) {

        List<UserDocument> users =
                elasticSearchQueryService.searchUsers(keyword);

        return users.stream()
                .map(user ->
                        UserSearchResponse.builder()
                                .userId(user.getUserId())
                                .fullName(user.getFullName())
                                .username(user.getUsername())
                                .bio(user.getBio())
                                .profileImage(user.getProfileImage())
                                .build()
                )
                .toList();
    }


    /*
     * =========================================================
     * USER AUTOCOMPLETE
     * Suggest users by full name or username
     * =========================================================
     */
    @Override
    public List<UserAutocompleteResponse> autocompleteUsers(
            String keyword) {

        return elasticSearchQueryService
                .autocompleteUsers(keyword)
                .stream()
                .map(user ->
                        UserAutocompleteResponse.builder()
                                .userId(user.getUserId())
                                .fullName(user.getFullName())
                                .username(user.getUsername())
                                .profileImage(user.getProfileImage())
                                .build()
                )
                .toList();
    }
}

