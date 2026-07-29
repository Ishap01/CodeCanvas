package com.codecanvas.searchservice.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Document(indexName = "snippets")
public class SearchDocument {

    @Id
    private String id;

    private UUID snippetId;

    private String title;

    private String description;

    private String language;

    private String framework;

    private String category;

    private List<String> tags;

    private Long likes;

    private Long views;

    private String previewImageUrl;
}