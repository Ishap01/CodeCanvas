package com.codecanvas.searchservice.config;

import com.codecanvas.searchservice.document.SearchDocument;
import com.codecanvas.searchservice.repository.SearchDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final SearchDocumentRepository repository;

    @Override
    public void run(String... args) {

        if (repository.count() > 0) {
            System.out.println("Elasticsearch already contains data.");
            return;
        }

        List<SearchDocument> documents = List.of(

                createDocument(
                        "JWT Authentication",
                        "Spring Boot JWT Authentication Example",
                        "Java",
                        "Spring Boot",
                        "Security",
                        List.of("jwt", "security", "authentication"),
                        320L,
                        1250L,
                        "/images/jwt.png"
                ),

                createDocument(
                        "Spring Security OAuth2",
                        "OAuth2 Login using Spring Security",
                        "Java",
                        "Spring Boot",
                        "Security",
                        List.of("oauth2", "security"),
                        980L,
                        5400L,
                        "/images/oauth2.png"
                ),

                createDocument(
                        "Spring Data JPA CRUD",
                        "CRUD Operations using Spring Data JPA",
                        "Java",
                        "Spring Boot",
                        "Database",
                        List.of("jpa", "crud", "mysql"),
                        180L,
                        950L,
                        "/images/jpa.png"
                ),

                createDocument(
                        "Spring Boot REST API",
                        "Build REST APIs using Spring Boot",
                        "Java",
                        "Spring Boot",
                        "REST API",
                        List.of("rest", "api", "json"),
                        420L,
                        3100L,
                        "/images/rest.png"
                ),

                createDocument(
                        "Spring Boot Microservices",
                        "Microservices using Spring Cloud",
                        "Java",
                        "Spring Boot",
                        "Microservices",
                        List.of("microservices", "eureka", "gateway"),
                        760L,
                        4700L,
                        "/images/microservice.png"
                )
        );

        repository.saveAll(documents);

        System.out.println("----------------------------------------");
        System.out.println("5 Sample Documents Inserted Successfully");
        System.out.println("----------------------------------------");
    }

    private SearchDocument createDocument(
            String title,
            String description,
            String language,
            String framework,
            String category,
            List<String> tags,
            Long likes,
            Long views,
            String previewImageUrl
    ) {

        UUID snippetId = UUID.randomUUID();

        return SearchDocument.builder()
                .id(snippetId.toString())
                .snippetId(snippetId)
                .title(title)
                .description(description)
                .language(language)
                .framework(framework)
                .category(category)
                .tags(tags)
                .likes(likes)
                .views(views)
                .bookmarks(0L)
                .forks(0L)
                .createdAt(LocalDateTime.now())
                .previewImageUrl(previewImageUrl)
                .build();
    }
}