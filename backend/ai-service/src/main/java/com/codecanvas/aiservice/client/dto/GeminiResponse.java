package com.codecanvas.aiservice.client.dto;

import lombok.Data;

import java.util.List;

@Data
public class GeminiResponse {

    private List<Candidate> candidates;

}