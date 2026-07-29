package com.codecanvas.snippetservice.service;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.codecanvas.snippetservice.dto.request.CreateSnippetRequest;
import com.codecanvas.snippetservice.dto.request.UpdateSnippetRequest;
import com.codecanvas.snippetservice.dto.response.ApiResponse;
import com.codecanvas.snippetservice.dto.response.SnippetResponse;

public interface SnippetService {

    SnippetResponse createSnippet(
            UUID userId,
            CreateSnippetRequest request
    );

    SnippetResponse getSnippetById(
            UUID snippetId,
            UUID currentUserId
    );

    List<SnippetResponse> getPublicSnippets();

    List<SnippetResponse> getSnippetsByUserId(
            UUID userId
    );

    SnippetResponse updateSnippet(
            UUID snippetId,
            UUID userId,
            UpdateSnippetRequest request
    );

    SnippetResponse uploadOrReplacePreviewImage(
            UUID snippetId,
            UUID userId,
            MultipartFile image
    );

    ApiResponse deletePreviewImage(
            UUID snippetId,
            UUID userId
    );

    ApiResponse deleteSnippet(
            UUID snippetId,
            UUID userId
    );

    List<SnippetResponse> getAllSnippets();
}