package com.codecanvas.snippetservice.dto.request;

import java.util.List;

import com.codecanvas.snippetservice.enums.Visibility;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class UpdateSnippetRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Language is required")
    private String language;

    private String framework;

    @NotBlank(message = "Category is required")
    private String category;

    @NotEmpty(message = "At least one tag is required")
    private List<String> tags;

    @NotNull(message = "Visibility is required")
    private Visibility visibility;

    public UpdateSnippetRequest() {
    }

    public UpdateSnippetRequest(
            String title,
            String description,
            String code,
            String language,
            String framework,
            String category,
            List<String> tags,
            Visibility visibility) {

        this.title = title;
        this.description = description;
        this.code = code;
        this.language = language;
        this.framework = framework;
        this.category = category;
        this.tags = tags;
        this.visibility = visibility;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }
}