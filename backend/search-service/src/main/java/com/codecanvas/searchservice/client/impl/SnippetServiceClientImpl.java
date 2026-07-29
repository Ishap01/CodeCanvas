package com.codecanvas.searchservice.client.impl;

import com.codecanvas.searchservice.client.SnippetServiceClient;
import com.codecanvas.searchservice.dto.request.SearchRequest;
import com.codecanvas.searchservice.dto.response.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SnippetServiceClientImpl implements SnippetServiceClient {

    @Override
    public List<SearchResponse> searchSnippets(SearchRequest request) {

        // TODO
        // Replace with REST call to Snippet Service

        return Collections.emptyList();
    }
}