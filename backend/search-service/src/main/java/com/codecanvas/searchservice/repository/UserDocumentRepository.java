package com.codecanvas.searchservice.repository;

import com.codecanvas.searchservice.document.UserDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.UUID;

public interface UserDocumentRepository
        extends ElasticsearchRepository<UserDocument, String> {

    void deleteByUserId(UUID userId);


    /*
     * =========================================================
     * USER SEARCH
     * Search users by full name, username or bio
     * =========================================================
     */
    List<UserDocument>
    findByFullNameContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrBioContainingIgnoreCase(
            String fullName,
            String username,
            String bio
    );

}