package com.codecanvas.searchservice.controller;

import com.codecanvas.searchservice.dto.request.SearchRequest;
import com.codecanvas.searchservice.dto.response.*;
import com.codecanvas.searchservice.security.AuthenticatedUser;
import com.codecanvas.searchservice.service.search.SearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.codecanvas.searchservice.dto.request.IndexSnippetRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/snippets")
    public ResponseEntity<ApiResponse<SearchPageResponse>> search(

            Authentication authentication,

            @Valid
            @RequestBody SearchRequest request
    ) {

        UUID userId = extractRequiredUserId(authentication);

        SearchPageResponse response =
                searchService.search(request, userId);

        return ResponseEntity.ok(
                ApiResponse.<SearchPageResponse>builder()
                        .success(true)
                        .message("Search completed successfully")
                        .data(response)
                        .build()
        );
    }

@GetMapping("/history")
public ResponseEntity<ApiResponse<List<SearchHistoryResponse>>>
getSearchHistory(
        Authentication authentication) {

    UUID userId =
            extractRequiredUserId(authentication);

        List<SearchHistoryResponse> history =
                searchService.getUserSearchHistory(userId);

        return ResponseEntity.ok(
                ApiResponse.<List<SearchHistoryResponse>>builder()
                        .success(true)
                        .message("Search history fetched successfully")
                        .data(history)
                        .build()
        );
    }

    @GetMapping("/suggestions")
    public ResponseEntity<ApiResponse<List<AutocompleteResponse>>> getSuggestions(

            @RequestParam String keyword
    ) {

        List<AutocompleteResponse> response =
                searchService.getSuggestions(keyword);

        return ResponseEntity.ok(
                ApiResponse.<List<AutocompleteResponse>>builder()
                        .success(true)
                        .message("Suggestions fetched successfully")
                        .data(response)
                        .build()
        );


    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<PopularSearchResponse>>> getPopularSearches() {

        List<PopularSearchResponse> response =
                searchService.getPopularSearches();

        return ResponseEntity.ok(
                ApiResponse.<List<PopularSearchResponse>>builder()
                        .success(true)
                        .message("Popular searches fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/internal/index")
    public ResponseEntity<ApiResponse<String>> indexSnippet(
            @RequestBody IndexSnippetRequest request) {

        searchService.indexSnippet(request);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Snippet indexed successfully")
                        .data("SUCCESS")
                        .build()
        );
    }

    private UUID extractRequiredUserId(Authentication authentication) {

        if (authentication == null) {
            throw new IllegalStateException(
                    "Authentication is required"
            );
        }

        if (!authentication.isAuthenticated()) {
            throw new IllegalStateException(
                    "User is not authenticated"
            );
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof AuthenticatedUser authenticatedUser)) {
            throw new IllegalStateException(
                    "Invalid authenticated user principal"
            );
        }

        UUID userId = authenticatedUser.getUserId();

        if (userId == null) {
            throw new IllegalStateException(
                    "User id is missing from authentication"
            );
        }

        return userId;
    }

    @DeleteMapping("/internal/index/{snippetId}")
    public ResponseEntity<ApiResponse<String>> deleteSnippet(
            @PathVariable UUID snippetId) {

        searchService.deleteSnippet(snippetId);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Snippet removed from search index")
                        .data("SUCCESS")
                        .build()
        );
    }

}

