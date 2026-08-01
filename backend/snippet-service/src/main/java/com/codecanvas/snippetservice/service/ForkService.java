package com.codecanvas.snippetservice.service;

import com.codecanvas.snippetservice.dto.response.SnippetResponse;

import java.util.UUID;

public interface ForkService {

    SnippetResponse forkSnippet(
            UUID snippetId,
            UUID currentUserId
    );
}