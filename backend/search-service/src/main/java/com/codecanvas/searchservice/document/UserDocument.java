package com.codecanvas.searchservice.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Document(indexName = "users")
public class UserDocument {

    @Id
    private String id;

    private UUID userId;

    private String fullName;

    private String username;

    private String email;

    private String bio;

    private String profileImage;
}