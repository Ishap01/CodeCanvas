package com.codecanvas.searchservice.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PopularSearchResponse {

    private String keyword;

    private Long count;
}