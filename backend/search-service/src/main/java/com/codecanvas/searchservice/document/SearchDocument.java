package com.codecanvas.searchservice.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
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

    // Community Statistics
    @Field(type = FieldType.Long)
    private Long likes;

    @Field(type = FieldType.Long)
    private Long views;

    @Field(type = FieldType.Long)
    private Long bookmarks;

    @Field(type = FieldType.Long)
    private Long forks;

    @Field(
            type = FieldType.Date,
            format = DateFormat.date_hour_minute_second_fraction
    )
    private LocalDateTime createdAt;

    // Preview
    private String previewImageUrl;

}