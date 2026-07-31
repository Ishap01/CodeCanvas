package com.codecanvas.searchservice.repository;

import com.codecanvas.searchservice.document.SearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchDocumentRepository
        extends ElasticsearchRepository<SearchDocument, String> {

    List<SearchDocument> findByTitleContainingIgnoreCase(String keyword);

    List<SearchDocument> findByLanguageIgnoreCase(String language);

    List<SearchDocument> findByFrameworkIgnoreCase(String framework);


}