package com.codecanvas.searchservice.client;

import com.codecanvas.searchservice.dto.request.SearchRequest;
import com.codecanvas.searchservice.dto.response.SearchResponse;

import java.util.List;

public interface SnippetServiceClient {

    List<SearchResponse> searchSnippets(SearchRequest request);

}