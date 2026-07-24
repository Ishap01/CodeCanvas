package com.codecanvas.snippetservice.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codecanvas.snippetservice.dto.request.CreateSnippetRequest;
import com.codecanvas.snippetservice.dto.request.UpdateSnippetRequest;
import com.codecanvas.snippetservice.dto.response.ApiResponse;
import com.codecanvas.snippetservice.dto.response.SnippetResponse;
import com.codecanvas.snippetservice.service.SnippetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/snippets")
public class SnippetController {

    private final SnippetService snippetService;

    public SnippetController(SnippetService snippetService) {
        this.snippetService = snippetService;
    }

    @PostMapping
    public ResponseEntity<SnippetResponse> createSnippet(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CreateSnippetRequest request) {

        /*
         * Cloudinary abhi implement nahi kiya hai.
         * Isliye previewImageUrl filhaal null bhej rahe hain.
         */
        String previewImageUrl = null;

        SnippetResponse response =
                snippetService.createSnippet(
                        userId,
                        request,
                        previewImageUrl
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{snippetId}")
    public ResponseEntity<SnippetResponse> getSnippetById(
            @PathVariable UUID snippetId,
            @RequestHeader(
                    value = "X-User-Id",
                    required = false
            ) UUID currentUserId) {

        SnippetResponse response =
                snippetService.getSnippetById(
                        snippetId,
                        currentUserId
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/public")
    public ResponseEntity<List<SnippetResponse>>
            getPublicSnippets() {

        List<SnippetResponse> responses =
                snippetService.getPublicSnippets();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SnippetResponse>>
            getSnippetsByUserId(
                    @PathVariable UUID userId) {

        List<SnippetResponse> responses =
                snippetService.getSnippetsByUserId(userId);

        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{snippetId}")
    public ResponseEntity<SnippetResponse> updateSnippet(
            @PathVariable UUID snippetId,
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody UpdateSnippetRequest request) {

        /*
         * New preview image abhi available nahi hai.
         * null ka matlab old image preserve hogi.
         */
        String previewImageUrl = null;

        SnippetResponse response =
                snippetService.updateSnippet(
                        snippetId,
                        userId,
                        request,
                        previewImageUrl
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{snippetId}")
    public ResponseEntity<ApiResponse> deleteSnippet(
            @PathVariable UUID snippetId,
            @RequestHeader("X-User-Id") UUID userId) {

        ApiResponse response =
                snippetService.deleteSnippet(
                        snippetId,
                        userId
                );

        return ResponseEntity.ok(response);
    }
}