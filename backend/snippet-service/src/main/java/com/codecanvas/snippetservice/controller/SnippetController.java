package com.codecanvas.snippetservice.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codecanvas.snippetservice.dto.request.CreateSnippetRequest;
import com.codecanvas.snippetservice.dto.request.UpdateSnippetRequest;
import com.codecanvas.snippetservice.dto.response.ApiResponse;
import com.codecanvas.snippetservice.dto.response.SnippetResponse;
import com.codecanvas.snippetservice.security.AuthenticatedUser;
import com.codecanvas.snippetservice.service.ForkService;
import com.codecanvas.snippetservice.service.SnippetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/snippets")
public class SnippetController {

    private final SnippetService snippetService;
    private final ForkService forkService;

    public SnippetController(
            SnippetService snippetService,
            ForkService forkService) {

        this.snippetService =
                snippetService;

        this.forkService =
                forkService;
    }

    /*
     * CREATE SNIPPET
     *
     * POST /api/snippets
     */
    @PostMapping
    public ResponseEntity<SnippetResponse>
    createSnippet(
            Authentication authentication,
            @Valid
            @RequestBody
            CreateSnippetRequest request) {

        UUID userId =
                extractRequiredUserId(
                        authentication
                );

        SnippetResponse response =
                snippetService.createSnippet(
                        userId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /*
     * GET ALL SNIPPETS
     */
    @GetMapping
    public ResponseEntity<List<SnippetResponse>>
    getAllSnippets() {

        return ResponseEntity.ok(
                snippetService.getAllSnippets()
        );
    }

    /*
     * GET SINGLE SNIPPET
     *
     * GET /api/snippets/{snippetId}
     */
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

    /*
     * GET SNIPPET FEED
     *
     * GET /api/snippets/public
     *
     * Anonymous/normal user:
     * PUBLIC snippets
     *
     * Premium user:
     * PUBLIC + PREMIUM snippets
     */
    @GetMapping("/public")
    public ResponseEntity<List<SnippetResponse>>
    getPublicSnippets(
            Authentication authentication) {

        UUID currentUserId =
                extractOptionalUserId(
                        authentication
                );

        List<SnippetResponse> response =
                snippetService
                        .getPublicSnippets(
                                currentUserId
                        );

        return ResponseEntity.ok(response);
    }

    /*
     * GET CURRENT USER SNIPPETS
     *
     * GET /api/snippets/user/me
     */
    @GetMapping("/user/me")
    public ResponseEntity<List<SnippetResponse>>
    getMySnippets(
            Authentication authentication) {

        UUID userId =
                extractRequiredUserId(
                        authentication
                );

        List<SnippetResponse> response =
                snippetService
                        .getSnippetsByUserId(
                                userId
                        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SnippetResponse>> getUserSnippets(
            @PathVariable UUID userId,
            Authentication authentication) {

        UUID currentUserId =
                extractOptionalUserId(authentication);

        return ResponseEntity.ok(
                snippetService.getProfileSnippets(
                        userId,
                        currentUserId
                )
        );
    }

    /*
     * UPDATE SNIPPET
     *
     * PUT /api/snippets/{snippetId}
     */
    @PutMapping("/{snippetId}")
    public ResponseEntity<SnippetResponse>
    updateSnippet(
            @PathVariable
            UUID snippetId,
            Authentication authentication,
            @Valid
            @RequestBody
            UpdateSnippetRequest request) {

        UUID userId =
                extractRequiredUserId(
                        authentication
                );

        SnippetResponse response =
                snippetService.updateSnippet(
                        snippetId,
                        userId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    /*
     * UPLOAD PREVIEW IMAGE
     */
    @PostMapping(
            value = "/{snippetId}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<SnippetResponse>
    uploadPreviewImage(
            @PathVariable
            UUID snippetId,
            Authentication authentication,
            @RequestParam("image")
            MultipartFile image) {

        UUID userId =
                extractRequiredUserId(
                        authentication
                );

        SnippetResponse response =
                snippetService
                        .uploadOrReplacePreviewImage(
                                snippetId,
                                userId,
                                image
                        );

        return ResponseEntity.ok(response);
    }

    /*
     * REPLACE PREVIEW IMAGE
     */
    @PutMapping(
            value = "/{snippetId}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<SnippetResponse>
    replacePreviewImage(
            @PathVariable
            UUID snippetId,
            Authentication authentication,
            @RequestParam("image")
            MultipartFile image) {

        UUID userId =
                extractRequiredUserId(
                        authentication
                );

        SnippetResponse response =
                snippetService
                        .uploadOrReplacePreviewImage(
                                snippetId,
                                userId,
                                image
                        );

        return ResponseEntity.ok(response);
    }

    /*
     * DELETE PREVIEW IMAGE
     */
    @DeleteMapping("/{snippetId}/image")
    public ResponseEntity<ApiResponse>
    deletePreviewImage(
            @PathVariable
            UUID snippetId,
            Authentication authentication) {

        UUID userId =
                extractRequiredUserId(
                        authentication
                );

        ApiResponse response =
                snippetService
                        .deletePreviewImage(
                                snippetId,
                                userId
                        );

        return ResponseEntity.ok(response);
    }

    /*
     * DELETE SNIPPET
     */
    @DeleteMapping("/{snippetId}")
    public ResponseEntity<ApiResponse>
    deleteSnippet(
            @PathVariable
            UUID snippetId,
            Authentication authentication) {

        UUID userId =
                extractRequiredUserId(
                        authentication
                );

        ApiResponse response =
                snippetService.deleteSnippet(
                        snippetId,
                        userId
                );

        return ResponseEntity.ok(response);
    }

    /*
     * FORK SNIPPET
     */
    @PostMapping("/{snippetId}/fork")
    public ResponseEntity<SnippetResponse>
    forkSnippet(
            @PathVariable
            UUID snippetId,
            @AuthenticationPrincipal
            AuthenticatedUser authenticatedUser) {

        SnippetResponse response =
                forkService.forkSnippet(
                        snippetId,
                        authenticatedUser.getUserId()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    private UUID extractRequiredUserId(
            Authentication authentication) {

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

        Object principal =
                authentication.getPrincipal();

        if (!(principal
                instanceof AuthenticatedUser)) {

            throw new IllegalStateException(
                    "Invalid authenticated user principal"
            );
        }

        AuthenticatedUser authenticatedUser =
                (AuthenticatedUser) principal;

        UUID userId =
                authenticatedUser.getUserId();

        if (userId == null) {

            throw new IllegalStateException(
                    "User id is missing from authentication"
            );
        }

        return userId;
    }

    private UUID extractOptionalUserId(
            Authentication authentication) {

        if (authentication == null) {

            return null;
        }

        if (!authentication.isAuthenticated()) {

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