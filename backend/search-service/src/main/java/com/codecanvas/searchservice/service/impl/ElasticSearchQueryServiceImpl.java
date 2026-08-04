package com.codecanvas.searchservice.service.impl;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.codecanvas.searchservice.document.SearchDocument;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.codecanvas.searchservice.document.UserDocument;
import com.codecanvas.searchservice.dto.request.SearchRequest;
import com.codecanvas.searchservice.service.search.ElasticSearchQueryService;
import com.codecanvas.searchservice.service.search.SearchPage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ElasticSearchQueryServiceImpl
        implements ElasticSearchQueryService {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public SearchPage<SearchDocument> search(SearchRequest request) {

        BoolQuery.Builder boolQuery = new BoolQuery.Builder();

        // Keyword Search
        if (request.getKeyword() != null &&
                !request.getKeyword().isBlank()) {

            boolQuery.should(s -> s.multiMatch(mm -> mm
                    .query(request.getKeyword())
                    .fields(
                            "title",
                            "description",
                            "language",
                            "framework",
                            "category",
                            "tags"
                    )
                    .fuzziness("AUTO")
            ));
        }

        // Language Filter
        if (request.getLanguage() != null &&
                !request.getLanguage().isBlank()) {

            boolQuery.filter(f -> f.term(t -> t
                    .field("language.keyword")
                    .value(FieldValue.of(request.getLanguage()))
            ));
        }

        // Framework Filter
        if (request.getFramework() != null &&
                !request.getFramework().isBlank()) {

            boolQuery.filter(f -> f.term(t -> t
                    .field("framework.keyword")
                    .value(FieldValue.of(request.getFramework()))
            ));
        }

        // Category Filter
        if (request.getCategory() != null &&
                !request.getCategory().isBlank()) {

            boolQuery.filter(f -> f.term(t -> t
                    .field("category.keyword")
                    .value(FieldValue.of(request.getCategory()))
            ));
        }

        Query query = Query.of(q -> q.bool(boolQuery.build()));

        int page =
                request.getPage() == null
                        ? 0
                        : Math.max(request.getPage(), 0);

        int size =
                request.getSize() == null
                        ? 10
                        : Math.min(
                        Math.max(request.getSize(), 1),
                        100
                );
        NativeQueryBuilder builder = NativeQuery.builder()
                .withQuery(query)
                .withPageable(PageRequest.of(page, size));

        // Sorting
        if (request.getSortBy() != null) {

            switch (request.getSortBy()) {

                case TRENDING ->

                        builder.withSort(
                                SortOptions.of(s ->
                                        s.field(f ->
                                                f.field("views")
                                                        .order(SortOrder.Desc)))
                        );

                case MOST_LIKED ->

                        builder.withSort(
                                SortOptions.of(s ->
                                        s.field(f ->
                                                f.field("likes")
                                                        .order(SortOrder.Desc)))
                        );

                case NEWEST ->

                        builder.withSort(
                                SortOptions.of(s ->
                                        s.field(f ->
                                                f.field("createdAt")
                                                        .order(SortOrder.Desc)))
                        );

                case MOST_FORKED ->

                        builder.withSort(
                                SortOptions.of(s ->
                                        s.field(f ->
                                                f.field("forks")
                                                        .order(SortOrder.Desc)))
                        );
            }
        }

        NativeQuery nativeQuery = builder.build();

        SearchHits<SearchDocument> hits =
                elasticsearchOperations.search(
                        nativeQuery,
                        SearchDocument.class
                );

        List<SearchDocument> documents =
                hits.stream()
                        .map(SearchHit::getContent)
                        .toList();

        long total = hits.getTotalHits();

        return SearchPage.<SearchDocument>builder()
                .content(documents)
                .totalElements(total)
                .totalPages((int) Math.ceil((double) total / size))
                .currentPage(page)
                .pageSize(size)
                .first(page == 0)
                .last((page + 1) * size >= total)
                .build();
    }



    @Override
    public List<SearchDocument> autocomplete(String keyword) {

        Query query = Query.of(q ->
                q.multiMatch(mm -> mm
                        .query(keyword)
                        .fields(
                                "title",
                                "description",
                                "language",
                                "framework",
                                "category",
                                "tags"
                        )
                        .type(TextQueryType.BoolPrefix)
                )
        );

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query)
                .build();

        SearchHits<SearchDocument> hits =
                elasticsearchOperations.search(
                        nativeQuery,
                        SearchDocument.class
                );

        return hits.stream()
                .map(SearchHit::getContent)
                .distinct()
                .toList();
    }

    /*
     * =========================================================
     * USER AUTOCOMPLETE
     * Search users by full name and username
     * =========================================================
     */
    @Override
    public List<UserDocument> autocompleteUsers(String keyword) {

        Query query = Query.of(q ->
                q.multiMatch(mm -> mm
                        .query(keyword)
                        .fields(
                                "fullName",
                                "username"
                        )
                        .type(TextQueryType.BoolPrefix)
                        .fuzziness("AUTO")
                )
        );

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query)
                .build();

        SearchHits<UserDocument> hits =
                elasticsearchOperations.search(
                        nativeQuery,
                        UserDocument.class
                );

        return hits.stream()
                .map(SearchHit::getContent)
                .distinct()
                .toList();
    }

    /*
     * =========================================================
     * USER FUZZY SEARCH
     * =========================================================
     */
    @Override
    public List<UserDocument> searchUsers(String keyword) {

        Query query = Query.of(q ->
                q.multiMatch(mm -> mm
                        .query(keyword)
                        .fields(
                                "fullName",
                                "username",
                                "bio"
                        )
                        .fuzziness("AUTO")
                )
        );

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(query)
                .build();

        SearchHits<UserDocument> hits =
                elasticsearchOperations.search(
                        nativeQuery,
                        UserDocument.class
                );

        return hits.stream()
                .map(SearchHit::getContent)
                .distinct()
                .toList();
    }
}