package com.codecanvas.searchservice.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutocompleteResponse {

    private UUID snippetId;

    private String suggestion;
}