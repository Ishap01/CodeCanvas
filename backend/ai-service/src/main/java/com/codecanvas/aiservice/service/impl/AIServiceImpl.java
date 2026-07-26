package com.codecanvas.aiservice.service.impl;

import com.codecanvas.aiservice.client.GeminiClient;
import com.codecanvas.aiservice.dto.request.ExplainCodeRequest;
import com.codecanvas.aiservice.dto.request.GenerateTagsRequest;
import com.codecanvas.aiservice.dto.request.SummarizeCodeRequest;
import com.codecanvas.aiservice.dto.response.AIResponse;
import com.codecanvas.aiservice.entity.AIHistory;
import com.codecanvas.aiservice.entity.enums.AIOperation;
import com.codecanvas.aiservice.repository.AIHistoryRepository;
import com.codecanvas.aiservice.service.AIService;
import com.codecanvas.aiservice.util.PromptBuilder;
import com.codecanvas.aiservice.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {

    private final AIHistoryRepository historyRepository;
    private final GeminiClient geminiClient;

    @Override
    public AIResponse explainCode(ExplainCodeRequest request) {

        String prompt = PromptBuilder.buildExplainPrompt(request.getCode());

        String result = geminiClient.generateContent(prompt);

        saveHistory(
                UserContext.getUserId(),
                prompt,
                result,
                AIOperation.EXPLAIN_CODE
        );

        return AIResponse.builder()
                .operation(AIOperation.EXPLAIN_CODE.name())
                .result(result)
                .generatedAt(LocalDateTime.now())
                .build();
    }


    @Override
    public AIResponse summarizeCode(SummarizeCodeRequest request) {

        String prompt = PromptBuilder.buildSummaryPrompt(request.getCode());

        String result = geminiClient.generateContent(prompt);

        saveHistory(
                UserContext.getUserId(),
                prompt,
                result,
                AIOperation.SUMMARIZE_CODE
        );

        return AIResponse.builder()
                .operation(AIOperation.SUMMARIZE_CODE.name())
                .result(result)
                .generatedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public AIResponse generateTags(GenerateTagsRequest request) {

        String prompt = PromptBuilder.buildTagPrompt(request.getCode());

        String result = geminiClient.generateContent(prompt);

        saveHistory(
                UserContext.getUserId(),
                prompt,
                result,
                AIOperation.GENERATE_TAGS
        );

        return AIResponse.builder()
                .operation(AIOperation.GENERATE_TAGS.name())
                .result(result)
                .generatedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public List<AIResponse> getHistory(UUID userId) {

        return historyRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(history -> AIResponse.builder()
                        .operation(history.getOperation().name())
                        .result(history.getResponse())
                        .generatedAt(LocalDateTime.now())
                        .build())
                .toList();
    }

    private void saveHistory(
            UUID userId,
            String prompt,
            String response,
            AIOperation operation
    ) {

        AIHistory history = AIHistory.builder()
                .userId(userId)
                .prompt(prompt)
                .response(response)
                .operation(operation)
                .build();

        historyRepository.save(history);
    }



}