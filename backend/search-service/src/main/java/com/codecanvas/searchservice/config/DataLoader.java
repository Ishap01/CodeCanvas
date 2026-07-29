package com.codecanvas.searchservice.config;

import com.codecanvas.searchservice.document.SearchDocument;
import com.codecanvas.searchservice.repository.SearchDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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

                SearchDocument.builder()
                        .snippetId(UUID.randomUUID())
                        .title("JWT Authentication")
                        .description("Spring Boot JWT Authentication Example")
                        .language("Java")
                        .framework("Spring Boot")
                        .category("Security")
                        .tags(List.of("jwt", "security", "authentication"))
                        .likes(320L)
                        .views(1250L)
                        .forks(0L)
                        .previewImageUrl("/images/jwt.png")
                        .build(),

                SearchDocument.builder()
                        .snippetId(UUID.randomUUID())
                        .title("Spring Security OAuth2")
                        .description("OAuth2 Login using Spring Security")
                        .language("Java")
                        .framework("Spring Boot")
                        .category("Security")
                        .tags(List.of("oauth2", "security"))
                        .likes(980L)
                        .views(5400L)
                        .forks(0L)
                        .previewImageUrl("/images/oauth2.png")
                        .build(),

                SearchDocument.builder()
                        .snippetId(UUID.randomUUID())
                        .title("Spring Data JPA CRUD")
                        .description("CRUD Operations using Spring Data JPA")
                        .language("Java")
                        .framework("Spring Boot")
                        .category("Database")
                        .tags(List.of("jpa", "crud", "mysql"))
                        .likes(180L)
                        .views(950L)
                        .forks(0L)
                        .previewImageUrl("/images/jpa.png")
                        .build(),

                SearchDocument.builder()
                        .snippetId(UUID.randomUUID())
                        .title("Spring Boot REST API")
                        .description("Build REST APIs using Spring Boot")
                        .language("Java")
                        .framework("Spring Boot")
                        .category("REST API")
                        .tags(List.of("rest", "api", "json"))
                        .likes(420L)
                        .views(3100L)
                        .forks(0L)
                        .previewImageUrl("/images/rest.png")
                        .build(),

                SearchDocument.builder()
                        .snippetId(UUID.randomUUID())
                        .title("Spring Boot Microservices")
                        .description("Microservices using Spring Cloud")
                        .language("Java")
                        .framework("Spring Boot")
                        .category("Microservices")
                        .tags(List.of("microservices", "eureka", "gateway"))
                        .likes(760L)
                        .views(4700L)
                        .forks(0L)
                        .previewImageUrl("/images/microservice.png")
                        .build()

        );

        repository.saveAll(documents);

        System.out.println("----------------------------------------");
        System.out.println("5 Sample Documents Inserted Successfully");
        System.out.println("----------------------------------------");
    }
}