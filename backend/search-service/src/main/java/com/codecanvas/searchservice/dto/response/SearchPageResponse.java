package com.codecanvas.searchservice.dto.response;


import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchPageResponse {

    private List<SearchResponse> snippets;

    private Integer currentPage;

    private Integer pageSize;

    private Long totalElements;

    private Integer totalPages;

    private Boolean first;

    private Boolean last;

}