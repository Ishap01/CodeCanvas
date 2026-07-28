package com.codecanvas.snippetservice.client;

import com.codecanvas.snippetservice.dto.request.IndexSnippetRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class SearchServiceClient {

    private final RestTemplate restTemplate;

    private static final String SEARCH_SERVICE_URL =
            "http://localhost:8083/api/search/index";

    public void indexSnippet(IndexSnippetRequest request) {

        restTemplate.postForObject(
                SEARCH_SERVICE_URL,
                request,
                String.class
        );

    }

}