package com.codecanvas.searchservice.config;

import com.codecanvas.searchservice.document.SearchDocument;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ElasticsearchIndexConfig {

    private final ElasticsearchOperations elasticsearchOperations;

    @PostConstruct
    public void createIndex() {

        IndexOperations indexOps =
                elasticsearchOperations.indexOps(SearchDocument.class);

        if (!indexOps.exists()) {

            indexOps.create();

            indexOps.putMapping(
                    indexOps.createMapping(SearchDocument.class)
            );
        }
    }
}