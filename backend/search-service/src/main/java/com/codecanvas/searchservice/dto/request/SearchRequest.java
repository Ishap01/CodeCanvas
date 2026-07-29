package com.codecanvas.searchservice.dto.request;

import com.codecanvas.searchservice.enums.SortBy;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchRequest {


    private String keyword;

    private String language;

    private String framework;
    private String category;

    private SortBy sortBy;

    @Min(value = 0, message = "Page cannot be negative")
    private Integer page;

    @Min(value = 1, message = "Size must be at least 1")
    private Integer size;
}