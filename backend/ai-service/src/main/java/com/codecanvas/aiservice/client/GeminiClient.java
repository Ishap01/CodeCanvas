package com.codecanvas.aiservice.client;

import com.codecanvas.aiservice.client.dto.*;
import com.codecanvas.aiservice.exception.GeminiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class GeminiClient {

    private final RestClient restClient;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    public GeminiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public String generateContent(String prompt) {

        try{
            GeminiRequest request = new GeminiRequest(
                    List.of(
                            new Content(
                                    List.of(
                                            new Part(prompt)
                                    )
                            )
                    )
            );

            GeminiResponse response =
                    restClient.post()
                            .uri(apiUrl + "?key=" + apiKey)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(request)
                            .retrieve()
                            .body(GeminiResponse.class);

            if (response == null
                    || response.getCandidates() == null
                    || response.getCandidates().isEmpty()) {

                throw new GeminiException("No response received from Gemini.");
            }

            return response.getCandidates()
                    .get(0)
                    .getContent()
                    .getParts()
                    .get(0)
                    .getText();
        } catch (Exception ex) {

            throw new GeminiException("Failed to connect with Gemini API.");
        }
    }

}