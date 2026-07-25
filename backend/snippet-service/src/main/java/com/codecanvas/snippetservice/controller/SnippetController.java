package com.codecanvas.snippetservice.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codecanvas.snippetservice.dto.request.CreateSnippetRequest;
import com.codecanvas.snippetservice.dto.request.UpdateSnippetRequest;
import com.codecanvas.snippetservice.dto.response.ApiResponse;
import com.codecanvas.snippetservice.dto.response.SnippetResponse;
import com.codecanvas.snippetservice.security.AuthenticatedUser;
import com.codecanvas.snippetservice.service.SnippetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/snippets")
public class SnippetController {

    private final SnippetService snippetService;

    public SnippetController(
            SnippetService snippetService) {

        this.snippetService = snippetService;
    }

    @PostMapping
    public ResponseEntity<SnippetResponse>
            createSnippet(
                    Authentication authentication,
                    @Valid
                    @RequestBody
                    CreateSnippetRequest request,
                    @RequestParam(
                            name = "previewImageUrl",
                            required = false
                    )
                    String previewImageUrl) {

        UUID userId =
                extractUserId(authentication);

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
    public ResponseEntity<SnippetResponse>
            getSnippetById(
                    @PathVariable
                    UUID snippetId,
                    Authentication authentication) {

        UUID currentUserId =
                extractOptionalUserId(
                        authentication
                );

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

        List<SnippetResponse> response =
                snippetService.getPublicSnippets();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/me")
    public ResponseEntity<List<SnippetResponse>>
            getMySnippets(
                    Authentication authentication) {

        UUID userId =
                extractUserId(authentication);

        List<SnippetResponse> response =
                snippetService.getSnippetsByUserId(
                        userId
                );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{snippetId}")
    public ResponseEntity<SnippetResponse>
            updateSnippet(
                    @PathVariable
                    UUID snippetId,
                    Authentication authentication,
                    @Valid
                    @RequestBody
                    UpdateSnippetRequest request,
                    @RequestParam(
                            name = "previewImageUrl",
                            required = false
                    )
                    String previewImageUrl) {

        UUID userId =
                extractUserId(authentication);

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
    public ResponseEntity<ApiResponse>
            deleteSnippet(
                    @PathVariable
                    UUID snippetId,
                    Authentication authentication) {

        UUID userId =
                extractUserId(authentication);

        ApiResponse response =
                snippetService.deleteSnippet(
                        snippetId,
                        userId
                );

        return ResponseEntity.ok(response);
    }

    private UUID extractUserId(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "Authenticated user is required"
            );
        }

        Object principal =
                authentication.getPrincipal();

        if (!(principal
                instanceof AuthenticatedUser)) {

            throw new IllegalStateException(
                    "Invalid authenticated user"
            );
        }

        AuthenticatedUser authenticatedUser =
                (AuthenticatedUser) principal;

        if (authenticatedUser.getUserId() == null) {
            throw new IllegalStateException(
                    "User id is missing from authentication"
            );
        }

        return authenticatedUser.getUserId();
    }

    private UUID extractOptionalUserId(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return null;
        }

        Object principal =
                authentication.getPrincipal();

        if (!(principal
                instanceof AuthenticatedUser)) {

            return null;
        }

        AuthenticatedUser authenticatedUser =
                (AuthenticatedUser) principal;

        return authenticatedUser.getUserId();
    }
}