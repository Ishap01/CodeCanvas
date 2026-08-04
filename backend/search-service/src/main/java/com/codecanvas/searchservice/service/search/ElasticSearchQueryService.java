package com.codecanvas.searchservice.service.search;

import com.codecanvas.searchservice.document.SearchDocument;
import com.codecanvas.searchservice.document.UserDocument;
import com.codecanvas.searchservice.dto.request.SearchRequest;

import java.util.List;

public interface ElasticSearchQueryService {

    SearchPage<SearchDocument> search(SearchRequest request);

    List<SearchDocument> autocomplete(String keyword);

    /*
     * =========================================================
     * USER AUTOCOMPLETE
     * =========================================================
     */
    List<UserDocument> autocompleteUsers(String keyword);

    /*
     * =========================================================
     * USER FUZZY SEARCH
     * =========================================================
     */
    List<UserDocument> searchUsers(String keyword);
}