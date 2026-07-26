package com.codecanvas.aiservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SummarizeCodeRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    private UUID snippetId;

    @NotBlank(message = "Code cannot be empty")
    private String code;
}