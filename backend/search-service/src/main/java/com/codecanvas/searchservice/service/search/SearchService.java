package com.codecanvas.searchservice.service.search;

import com.codecanvas.searchservice.dto.request.SearchRequest;
import com.codecanvas.searchservice.dto.response.*;
import com.codecanvas.searchservice.dto.request.IndexSnippetRequest;
import com.codecanvas.searchservice.kafka.event.UserRegisteredEvent;
import com.codecanvas.searchservice.kafka.event.UserUpdatedEvent;
import com.codecanvas.searchservice.kafka.event.UserRegisteredEvent;
import com.codecanvas.searchservice.kafka.event.UserUpdatedEvent;

import java.util.List;
import java.util.UUID;

public interface SearchService {

    SearchPageResponse search(SearchRequest request, UUID userId);

    void saveSearchHistory(UUID userId, SearchRequest request);


    List<SearchHistoryResponse> getUserSearchHistory(UUID userId);

    List<AutocompleteResponse> getSuggestions(String keyword);

    List<PopularSearchResponse> getPopularSearches();

    void indexSnippet(IndexSnippetRequest request);

    void deleteSnippet(UUID snippetId);


    /*
     * =========================================================
     * USER OPERATIONS
     * =========================================================
     */

    void indexUser(UserRegisteredEvent event);

    void updateUser(UserUpdatedEvent event);

    void deleteUser(UUID userId);


    /*
     * =========================================================
     * USER SEARCH
     * Search users by full name, username or bio
     * =========================================================
     */
    List<UserSearchResponse> searchUsers(String keyword);

    /*
     * =========================================================
     * USER AUTOCOMPLETE
     * Suggest users by full name or username
     * =========================================================
     */
    List<UserAutocompleteResponse> autocompleteUsers(String keyword);
}