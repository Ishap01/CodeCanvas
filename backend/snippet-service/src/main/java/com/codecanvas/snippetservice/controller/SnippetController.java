package com.codecanvas.snippetservice.controller;

import com.codecanvas.snippetservice.dto.request.CreateSnippetRequest;
import com.codecanvas.snippetservice.dto.request.UpdateSnippetRequest;
import com.codecanvas.snippetservice.dto.response.ApiResponse;
import com.codecanvas.snippetservice.dto.response.SnippetResponse;
import com.codecanvas.snippetservice.security.AuthenticatedUser;
import com.codecanvas.snippetservice.service.CloudinaryService;
import com.codecanvas.snippetservice.service.SnippetService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/snippets")
public class SnippetController {

  private final SnippetService snippetService;
  private final CloudinaryService cloudinaryService;

  public SnippetController(SnippetService snippetService, CloudinaryService cloudinaryService) {

    this.snippetService = snippetService;
    this.cloudinaryService = cloudinaryService;
  }

  @PostMapping(consumes = "multipart/form-data")
  public ResponseEntity<SnippetResponse> createSnippet(
      Authentication authentication,
      @Valid @RequestPart("request") CreateSnippetRequest request,
      @RequestPart(name = "previewImage", required = false) MultipartFile previewImage) {
    UUID userId = extractUserId(authentication);
    String previewImageUrl = null;
    if (previewImage != null && !previewImage.isEmpty()) {
      previewImageUrl = cloudinaryService.uploadImage(previewImage);
    }
    SnippetResponse response = snippetService.createSnippet(userId, request, previewImageUrl);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{snippetId}")
  public ResponseEntity<SnippetResponse> getSnippetById(
      @PathVariable UUID snippetId, Authentication authentication) {

    UUID currentUserId = extractOptionalUserId(authentication);

    SnippetResponse response = snippetService.getSnippetById(snippetId, currentUserId);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/public")
  public ResponseEntity<List<SnippetResponse>> getPublicSnippets() {

    List<SnippetResponse> response = snippetService.getPublicSnippets();

    return ResponseEntity.ok(response);
  }

  @GetMapping("/user/me")
  public ResponseEntity<List<SnippetResponse>> getMySnippets(Authentication authentication) {

    UUID userId = extractUserId(authentication);

    List<SnippetResponse> response = snippetService.getSnippetsByUserId(userId);

    return ResponseEntity.ok(response);
  }

  @PutMapping("/{snippetId}")
  public ResponseEntity<SnippetResponse> updateSnippet(
      @PathVariable UUID snippetId,
      Authentication authentication,
      @Valid @RequestBody UpdateSnippetRequest request,
      @RequestParam(name = "previewImageUrl", required = false) String previewImageUrl) {

    UUID userId = extractUserId(authentication);

    SnippetResponse response =
        snippetService.updateSnippet(snippetId, userId, request, previewImageUrl);

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{snippetId}")
  public ResponseEntity<ApiResponse> deleteSnippet(
      @PathVariable UUID snippetId, Authentication authentication) {

    UUID userId = extractUserId(authentication);

    ApiResponse response = snippetService.deleteSnippet(snippetId, userId);

    return ResponseEntity.ok(response);
  }

  private UUID extractUserId(Authentication authentication) {

    if (authentication == null || !authentication.isAuthenticated()) {

      throw new IllegalStateException("Authenticated user is required");
    }

    Object principal = authentication.getPrincipal();

    if (!(principal instanceof AuthenticatedUser authenticatedUser)) {

      throw new IllegalStateException("Invalid authenticated user");
    }

    if (authenticatedUser.getUserId() == null) {
      throw new IllegalStateException("User id is missing from authentication");
    }

    return authenticatedUser.getUserId();
  }

  private UUID extractOptionalUserId(Authentication authentication) {

    if (authentication == null || !authentication.isAuthenticated()) {

      return null;
    }

    Object principal = authentication.getPrincipal();

    if (!(principal instanceof AuthenticatedUser authenticatedUser)) {

      return null;
    }

    return authenticatedUser.getUserId();
  }
}
