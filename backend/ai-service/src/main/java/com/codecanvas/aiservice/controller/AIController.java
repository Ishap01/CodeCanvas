package com.codecanvas.aiservice.controller;

import com.codecanvas.aiservice.client.UserServiceClient;
import com.codecanvas.aiservice.dto.request.ExplainCodeRequest;
import com.codecanvas.aiservice.dto.request.GenerateTagsRequest;
import com.codecanvas.aiservice.dto.request.SummarizeCodeRequest;
import com.codecanvas.aiservice.dto.response.AIResponse;
import com.codecanvas.aiservice.security.AuthenticatedUser;
import com.codecanvas.aiservice.service.AIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;
    private final UserServiceClient userServiceClient;

    @PostMapping("/explain")
    public AIResponse explain(
            Authentication authentication,
            @RequestBody ExplainCodeRequest request) {

        UUID userId = extractRequiredUserId(authentication);

        return aiService.explainCode(userId, request);
    }

    @PostMapping("/summarize")
    public ResponseEntity<AIResponse> summarizeCode(

            Authentication authentication,

            @Valid @RequestBody SummarizeCodeRequest request) {

        UUID userId = extractRequiredUserId(authentication);

        return ResponseEntity.ok(
                aiService.summarizeCode(userId, request)
        );
    }

    @PostMapping("/generate-tags")
    public ResponseEntity<AIResponse> generateTags(

            Authentication authentication,

            @Valid @RequestBody GenerateTagsRequest request) {

        UUID userId = extractRequiredUserId(authentication);

        return ResponseEntity.ok(
                aiService.generateTags(userId, request)
        );
    }

    @GetMapping("/history")
    public ResponseEntity<List<AIResponse>> getHistory(

            Authentication authentication) {

        UUID userId = extractRequiredUserId(authentication);

        return ResponseEntity.ok(
                aiService.getHistory(userId)
        );
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

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof AuthenticatedUser authenticatedUser)) {
            throw new IllegalStateException(
                    "Invalid authenticated user principal"
            );
        }

        UUID userId = authenticatedUser.getUserId();

        if (userId == null) {
            throw new IllegalStateException(
                    "User id is missing from authentication"
            );
        }

        return userId;
    }

    @GetMapping("/test-premium/{userId}")
    public ResponseEntity<Boolean> testPremium(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(
                userServiceClient.isPremiumUser(userId)
        );
    }

}