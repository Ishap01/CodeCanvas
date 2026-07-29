package com.codecanvas.searchservice.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchHistoryResponse {

    private String keyword;

    private String language;

    private String framework;

    private LocalDateTime searchedAt;

}