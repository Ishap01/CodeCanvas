//package com.codecanvas.snippetservice.controller;
//
//import java.util.List;
//import java.util.UUID;
//
//import com.codecanvas.snippetservice.service.ForkService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.codecanvas.snippetservice.dto.request.CreateSnippetRequest;
//import com.codecanvas.snippetservice.dto.request.UpdateSnippetRequest;
//import com.codecanvas.snippetservice.dto.response.ApiResponse;
//import com.codecanvas.snippetservice.dto.response.SnippetResponse;
//import com.codecanvas.snippetservice.security.AuthenticatedUser;
//import com.codecanvas.snippetservice.service.SnippetService;
//
//import jakarta.validation.Valid;
//
//@RestController
//@RequestMapping("/api/snippets")
//public class SnippetController {
//
//    private final SnippetService snippetService;
//    private final ForkService forkService;
//
//    public SnippetController(
//            SnippetService snippetService, ForkService forkService) {
//
//        this.snippetService = snippetService;
//        this.forkService = forkService;
//    }
//
//    /*
//     * CREATE SNIPPET
//     *
//     * POST /api/snippets
//     *
//     * Content-Type:
//     * application/json
//     *
//     * Valid JWT required.
//     *
//     * Image create request ke saath nahi bhejni.
//     * Snippet create hone ke baad separate image
//     * endpoint use karna hai.
//     */
//    @PostMapping
//    public ResponseEntity<SnippetResponse>
//
//
//    createSnippet(
//            Authentication authentication,
//            @Valid
//            @RequestBody
//            CreateSnippetRequest request) {
//
//        UUID userId =
//                extractRequiredUserId(
//                        authentication
//                );
//
//        SnippetResponse response =
//                snippetService.createSnippet(
//                        userId,
//                        request
//                );
//
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(response);
//
//
//
//    }
//
//    /*Get all snippets*/
//    @GetMapping
//    public ResponseEntity<List<SnippetResponse>> getAllSnippets() {
//        return ResponseEntity.ok(snippetService.getAllSnippets());
//    }
//
//    /*
//     * GET SINGLE SNIPPET
//     *
//     * GET /api/snippets/{snippetId}
//     *
//     * PUBLIC snippet:
//     * Token optional.
//     *
//     * PRIVATE snippet:
//     * Owner ka valid token required.
//     */
//    @GetMapping("/{snippetId}")
//    public ResponseEntity<SnippetResponse> getSnippetById(
//            @PathVariable UUID snippetId,
//            Authentication authentication) {
//
//        System.out.println("INSIDE CONTROLLER");
//
//        UUID currentUserId =
//                extractOptionalUserId(authentication);
//
//        SnippetResponse response =
//                snippetService.getSnippetById(
//                        snippetId,
//                        currentUserId
//                );
//
//        return ResponseEntity.ok(response);
//    }
//
//    /*
//     * GET ALL PUBLIC SNIPPETS
//     *
//     * GET /api/snippets/public
//     *
//     * Token required nahi.
//     */
//    @GetMapping("/public")
//    public ResponseEntity<List<SnippetResponse>>
//    getPublicSnippets() {
//
//        List<SnippetResponse> response =
//                snippetService
//                        .getPublicSnippets();
//
//        return ResponseEntity.ok(response);
//    }
//
//
//
//
//
//    /*
//     * GET CURRENT LOGGED-IN USER SNIPPETS
//     *
//     * GET /api/snippets/user/me
//     *
//     * Valid JWT required.
//     */
//    @GetMapping("/user/me")
//    public ResponseEntity<List<SnippetResponse>>
//    getMySnippets(
//            Authentication authentication) {
//
//        UUID userId =
//                extractRequiredUserId(
//                        authentication
//                );
//
//        List<SnippetResponse> response =
//                snippetService
//                        .getSnippetsByUserId(
//                                userId
//                        );
//
//        return ResponseEntity.ok(response);
//    }
//
//    /*
//     * UPDATE SNIPPET DETAILS
//     *
//     * PUT /api/snippets/{snippetId}
//     *
//     * Content-Type:
//     * application/json
//     *
//     * Valid JWT required.
//     *
//     * Sirf snippet owner update kar sakta hai.
//     *
//     * Image is endpoint se update nahi hogi.
//     */
//    @PutMapping("/{snippetId}")
//    public ResponseEntity<SnippetResponse>
//    updateSnippet(
//            @PathVariable
//            UUID snippetId,
//            Authentication authentication,
//            @Valid
//            @RequestBody
//            UpdateSnippetRequest request) {
//
//        UUID userId =
//                extractRequiredUserId(
//                        authentication
//                );
//
//        SnippetResponse response =
//                snippetService.updateSnippet(
//                        snippetId,
//                        userId,
//                        request
//                );
//
//        return ResponseEntity.ok(response);
//    }
//
//    /*
//     * UPLOAD FIRST PREVIEW IMAGE
//     *
//     * POST /api/snippets/{snippetId}/image
//     *
//     * Content-Type:
//     * multipart/form-data
//     *
//     * Form-data:
//     * key   = image
//     * type  = File
//     *
//     * Yeh endpoint first upload aur replacement
//     * dono handle kar sakta hai.
//     */
//    @PostMapping(
//            value = "/{snippetId}/image",
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
//    )
//    public ResponseEntity<SnippetResponse>
//    uploadPreviewImage(
//            @PathVariable
//            UUID snippetId,
//            Authentication authentication,
//            @RequestParam("image")
//            MultipartFile image) {
//
//        UUID userId =
//                extractRequiredUserId(
//                        authentication
//                );
//
//        SnippetResponse response =
//                snippetService
//                        .uploadOrReplacePreviewImage(
//                                snippetId,
//                                userId,
//                                image
//                        );
//
//        return ResponseEntity.ok(response);
//    }
//
//    /*
//     * REPLACE EXISTING PREVIEW IMAGE
//     *
//     * PUT /api/snippets/{snippetId}/image
//     *
//     * Content-Type:
//     * multipart/form-data
//     *
//     * Form-data:
//     * key   = image
//     * type  = File
//     *
//     * New image upload hone aur DB save hone ke
//     * baad old Cloudinary image delete hogi.
//     */
//    @PutMapping(
//            value = "/{snippetId}/image",
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
//    )
//    public ResponseEntity<SnippetResponse>
//    replacePreviewImage(
//            @PathVariable
//            UUID snippetId,
//            Authentication authentication,
//            @RequestParam("image")
//            MultipartFile image) {
//
//        UUID userId =
//                extractRequiredUserId(
//                        authentication
//                );
//
//        SnippetResponse response =
//                snippetService
//                        .uploadOrReplacePreviewImage(
//                                snippetId,
//                                userId,
//                                image
//                        );
//
//        return ResponseEntity.ok(response);
//    }
//
//    /*
//     * DELETE PREVIEW IMAGE
//     *
//     * DELETE /api/snippets/{snippetId}/image
//     *
//     * Cloudinary asset delete karega.
//     *
//     * Database mein:
//     *
//     * previewImageUrl = null
//     * previewImagePublicId = null
//     */
//    @DeleteMapping("/{snippetId}/image")
//    public ResponseEntity<ApiResponse>
//    deletePreviewImage(
//            @PathVariable
//            UUID snippetId,
//            Authentication authentication) {
//
//        UUID userId =
//                extractRequiredUserId(
//                        authentication
//                );
//
//        ApiResponse response =
//                snippetService
//                        .deletePreviewImage(
//                                snippetId,
//                                userId
//                        );
//
//        return ResponseEntity.ok(response);
//    }
//
//    /*
//     * DELETE SNIPPET
//     *
//     * DELETE /api/snippets/{snippetId}
//     *
//     * Valid JWT required.
//     *
//     * Sirf snippet owner delete kar sakta hai.
//     *
//     * This is soft delete:
//     * status = DELETED
//     */
//    @DeleteMapping("/{snippetId}")
//    public ResponseEntity<ApiResponse>
//    deleteSnippet(
//            @PathVariable
//            UUID snippetId,
//            Authentication authentication) {
//
//        UUID userId =
//                extractRequiredUserId(
//                        authentication
//                );
//
//        ApiResponse response =
//                snippetService.deleteSnippet(
//                        snippetId,
//                        userId
//                );
//
//        return ResponseEntity.ok(response);
//    }
//
//    /*
//     * Protected API ke liye authenticated
//     * user ID extract karta hai.
//     *
//     * JWT filter AuthenticatedUser object ko
//     * SecurityContext mein store karta hai.
//     */
//    private UUID extractRequiredUserId(
//            Authentication authentication) {
//
//        if (authentication == null) {
//            throw new IllegalStateException(
//                    "Authentication is required"
//            );
//        }
//
//        if (!authentication.isAuthenticated()) {
//            throw new IllegalStateException(
//                    "User is not authenticated"
//            );
//        }
//
//        Object principal =
//                authentication.getPrincipal();
//
//        if (!(principal
//                instanceof AuthenticatedUser)) {
//
//            throw new IllegalStateException(
//                    "Invalid authenticated user principal"
//            );
//        }
//
//        AuthenticatedUser authenticatedUser =
//                (AuthenticatedUser) principal;
//
//        UUID userId =
//                authenticatedUser.getUserId();
//
//        if (userId == null) {
//            throw new IllegalStateException(
//                    "User id is missing from authentication"
//            );
//        }
//
//        return userId;
//    }
//
//    /*
//     * Public GET endpoint ke liye optional
//     * authenticated user ID.
//     *
//     * Token absent:
//     * null
//     *
//     * Valid token:
//     * logged-in user ID
//     */
//    private UUID extractOptionalUserId(
//            Authentication authentication) {
//
//        if (authentication == null) {
//            return null;
//        }
//
//        if (!authentication.isAuthenticated()) {
//            return null;
//        }
//
//        Object principal =
//                authentication.getPrincipal();
//
//        if (!(principal
//                instanceof AuthenticatedUser)) {
//
//            return null;
//        }
//
//        AuthenticatedUser authenticatedUser =
//                (AuthenticatedUser) principal;
//
//        return authenticatedUser.getUserId();
//    }
//
//    @PostMapping("/{snippetId}/fork")
//    public ResponseEntity<SnippetResponse> forkSnippet(
//
//            @PathVariable UUID snippetId,
//
//            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
//    ) {
//
//        SnippetResponse response =
//                forkService.forkSnippet(
//                        snippetId,
//                        authenticatedUser.getUserId()
//                );
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(response);
//    }
//}


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