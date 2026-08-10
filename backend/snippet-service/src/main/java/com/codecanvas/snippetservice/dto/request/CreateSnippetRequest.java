package com.codecanvas.snippetservice.dto.request;

import java.util.List;

import com.codecanvas.snippetservice.enums.Visibility;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSnippetRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    // Optional for single-file mode
    private String filename;

    @NotBlank(message = "Code is required")
    private String code;

    // Will be used when frontend supports multiple files
    private List<SnippetFileRequest> files;

    @NotBlank(message = "Language is required")
    private String language;

    private String framework;

    @NotBlank(message = "Category is required")
    private String category;

    @NotEmpty(message = "At least one tag is required")
    private List<String> tags;

    @NotNull(message = "Visibility is required")
    private Visibility visibility;
}