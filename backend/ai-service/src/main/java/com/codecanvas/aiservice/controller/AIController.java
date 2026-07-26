package com.codecanvas.aiservice.controller;

import com.codecanvas.aiservice.dto.request.ExplainCodeRequest;
import com.codecanvas.aiservice.dto.request.GenerateTagsRequest;
import com.codecanvas.aiservice.dto.request.SummarizeCodeRequest;
import com.codecanvas.aiservice.dto.response.AIResponse;
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

    @PostMapping("/explain")
    public ResponseEntity<AIResponse> explainCode(
            @Valid @RequestBody ExplainCodeRequest request) {

        return ResponseEntity.ok(aiService.explainCode(request));
    }

    @PostMapping("/summarize")
    public ResponseEntity<AIResponse> summarizeCode(
            @Valid @RequestBody SummarizeCodeRequest request) {

        return ResponseEntity.ok(aiService.summarizeCode(request));
    }

    @PostMapping("/generate-tags")
    public ResponseEntity<AIResponse> generateTags(
            @Valid @RequestBody GenerateTagsRequest request) {

        return ResponseEntity.ok(aiService.generateTags(request));
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<AIResponse>> getHistory(
            @PathVariable UUID userId) {

        return ResponseEntity.ok(aiService.getHistory(userId));
    }

}