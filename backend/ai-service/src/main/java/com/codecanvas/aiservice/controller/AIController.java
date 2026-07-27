package com.codecanvas.aiservice.controller;

import com.codecanvas.aiservice.dto.request.ExplainCodeRequest;
import com.codecanvas.aiservice.dto.request.GenerateTagsRequest;
import com.codecanvas.aiservice.dto.request.SummarizeCodeRequest;
import com.codecanvas.aiservice.dto.response.AIResponse;
import com.codecanvas.aiservice.security.JwtService;
import com.codecanvas.aiservice.service.AIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;
    private final JwtService jwtService;

    @PostMapping("/explain")
    public AIResponse explain(
            @RequestHeader("Authorization") String authorization,
            @RequestBody ExplainCodeRequest request) {

        System.out.println("===== CONTROLLER HIT =====");

        System.out.println("Authorization: " + authorization);

        String token = authorization.substring(7);

        System.out.println("Before extracting UUID");

        UUID userId = jwtService.extractUserId(token);

        System.out.println("User ID = " + userId);

        return aiService.explainCode(userId, request);
    }

    @PostMapping("/summarize")
    public ResponseEntity<AIResponse> summarizeCode(

            @RequestHeader("Authorization")
            String authorization,

            @Valid @RequestBody SummarizeCodeRequest request) {

        String token = authorization.substring(7);

        UUID userId = jwtService.extractUserId(token);

        return ResponseEntity.ok(
                aiService.summarizeCode(userId, request)
        );
    }

    @PostMapping("/generate-tags")
    public ResponseEntity<AIResponse> generateTags(

            @RequestHeader("Authorization")
            String authorization,

            @Valid @RequestBody GenerateTagsRequest request) {

        String token = authorization.substring(7);

        UUID userId = jwtService.extractUserId(token);

        return ResponseEntity.ok(
                aiService.generateTags(userId, request)
        );
    }

    @GetMapping("/history")
    public ResponseEntity<List<AIResponse>> getHistory(

            @RequestHeader("Authorization")
            String authorization) {

        String token = authorization.substring(7);

        UUID userId = jwtService.extractUserId(token);

        return ResponseEntity.ok(
                aiService.getHistory(userId)
        );
    }

}