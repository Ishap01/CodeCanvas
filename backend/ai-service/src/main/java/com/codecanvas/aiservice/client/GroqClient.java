package com.codecanvas.aiservice.client;

import com.codecanvas.aiservice.client.dto.*;
import com.codecanvas.aiservice.exception.AIException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class GroqClient {

    private final RestClient restClient;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.model}")
    private String model;

    public GroqClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public String generateContent(String prompt) {

        GroqRequest request = new GroqRequest(
                "llama-3.3-70b-versatile",
                List.of(
                        new Message("user", prompt)
                )
        );

        try {

            GroqResponse response =
                    restClient.post()
                            .uri(apiUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + apiKey)
                            .body(request)
                            .retrieve()
                            .body(GroqResponse.class);

            if (response == null
                    || response.getChoices() == null
                    || response.getChoices().isEmpty()) {

                throw new AIException("No response received.");

            }

            return response.getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

        } catch (Exception ex) {

            throw new AIException(ex.getMessage());

        }

    }

}