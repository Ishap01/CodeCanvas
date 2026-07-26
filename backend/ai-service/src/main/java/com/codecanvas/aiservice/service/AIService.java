package com.codecanvas.aiservice.service;

import com.codecanvas.aiservice.dto.request.ExplainCodeRequest;
import com.codecanvas.aiservice.dto.request.GenerateTagsRequest;
import com.codecanvas.aiservice.dto.request.SummarizeCodeRequest;
import com.codecanvas.aiservice.dto.response.AIResponse;

import java.util.List;
import java.util.UUID;

public interface AIService {

    AIResponse explainCode(ExplainCodeRequest request);

    AIResponse summarizeCode(SummarizeCodeRequest request);

    AIResponse generateTags(GenerateTagsRequest request);

    List<AIResponse> getHistory(UUID userId);
}