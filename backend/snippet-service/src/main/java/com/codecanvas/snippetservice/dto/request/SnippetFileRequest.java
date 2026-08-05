package com.codecanvas.snippetservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SnippetFileRequest {

    @NotBlank(message = "Filename is required")
    private String filename;

    @NotBlank(message = "Code is required")
    private String code;

}