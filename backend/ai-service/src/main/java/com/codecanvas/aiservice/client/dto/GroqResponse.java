package com.codecanvas.aiservice.client.dto;

import lombok.Data;

import java.util.List;

@Data
public class GroqResponse {

    private List<Choice> choices;

}