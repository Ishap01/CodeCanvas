package com.codecanvas.snippetservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnippetFileResponse {

    private String filename;

    private String code;

    private Integer fileOrder;
}