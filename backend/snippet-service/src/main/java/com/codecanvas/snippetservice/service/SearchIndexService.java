package com.codecanvas.snippetservice.service;

import com.codecanvas.snippetservice.entity.Snippet;

import java.util.UUID;

public interface SearchIndexService {

    void indexSnippet(Snippet snippet);

    void deleteSnippet(UUID snippetId);
}
