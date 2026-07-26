package com.codecanvas.searchservice.controller;

import com.codecanvas.searchservice.dto.request.SearchRequest;
import com.codecanvas.searchservice.dto.response.*;
import com.codecanvas.searchservice.service.SearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/snippets")
    public ResponseEntity<ApiResponse<SearchPageResponse>> search(

            @Valid
            @RequestBody SearchRequest request,

            @RequestParam UUID userId
    ) {

        SearchPageResponse response = searchService.search(request, userId);

        return ResponseEntity.ok(
                ApiResponse.<SearchPageResponse>builder()
                        .success(true)
                        .message("Search completed successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<ApiResponse<List<SearchHistoryResponse>>> getSearchHistory(
            @PathVariable UUID userId
    ) {

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

}

