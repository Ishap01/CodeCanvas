package com.codecanvas.aiservice.service.impl;

import com.codecanvas.aiservice.client.GroqClient;
import com.codecanvas.aiservice.dto.request.ExplainCodeRequest;
import com.codecanvas.aiservice.dto.request.GenerateTagsRequest;
import com.codecanvas.aiservice.dto.request.SummarizeCodeRequest;
import com.codecanvas.aiservice.dto.response.AIResponse;
import com.codecanvas.aiservice.entity.AIHistory;
import com.codecanvas.aiservice.enums.AIOperation;
import com.codecanvas.aiservice.exception.PremiumFeatureException;
import com.codecanvas.aiservice.repository.AIHistoryRepository;
import com.codecanvas.aiservice.service.AIService;
import com.codecanvas.aiservice.util.PromptBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.codecanvas.aiservice.client.UserServiceClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {

    private final AIHistoryRepository historyRepository;
    private final GroqClient groqClient;
    private final UserServiceClient userServiceClient;

    @Override
    public AIResponse explainCode(
            UUID userId,
            ExplainCodeRequest request) {

        if (!userServiceClient.isPremiumUser(userId)) {
            throw new PremiumFeatureException(
                    "Code explanation is available only for Premium users."
            );
        }

        String prompt =
                PromptBuilder.buildExplainPrompt(
                        request.getCode()
                );

        String result =
                groqClient.generateContent(prompt);

        saveHistory(
                userId,
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
    public AIResponse summarizeCode(UUID userId,
                                    SummarizeCodeRequest request) {

        String prompt = PromptBuilder.buildSummaryPrompt(request.getCode());

        String result = groqClient.generateContent(prompt);

        saveHistory(
                userId,
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
    public AIResponse generateTags(UUID userId,
                                   GenerateTagsRequest request) {

        String prompt = PromptBuilder.buildTagPrompt(request.getCode());

        String result = groqClient.generateContent(prompt);

        saveHistory(
                userId,
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