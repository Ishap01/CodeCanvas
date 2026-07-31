package com.codecanvas.snippetservice.service.impl;

import com.codecanvas.snippetservice.entity.Snippet;
import com.codecanvas.snippetservice.entity.SnippetView;
import com.codecanvas.snippetservice.repository.SnippetRepository;
import com.codecanvas.snippetservice.repository.SnippetViewRepository;
import com.codecanvas.snippetservice.service.SearchIndexService;
import com.codecanvas.snippetservice.service.ViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ViewServiceImpl implements ViewService {

    private final SnippetViewRepository snippetViewRepository;
    private final SnippetRepository snippetRepository;
    private final SearchIndexService searchIndexService;

    @Override
    @Transactional(
            propagation = Propagation.REQUIRES_NEW
    )
    public void recordView(
            Snippet snippet,
            UUID currentUserId) {

        if (snippet == null || currentUserId == null) {
            return;
        }

        /*
         * Owner's own views are not counted.
         */
        if (currentUserId.equals(snippet.getUserId())) {
            return;
        }

        /*
         * Count only one view per user.
         */
        if (snippetViewRepository.existsBySnippetIdAndUserId(
                snippet.getSnippetId(),
                currentUserId)) {

            return;
        }

        SnippetView snippetView =
                SnippetView.builder()
                        .snippetId(snippet.getSnippetId())
                        .userId(currentUserId)
                        .build();

        snippetViewRepository.save(snippetView);

        snippet.setViewCount(
                snippet.getViewCount() + 1
        );

        Snippet updatedSnippet =
                snippetRepository.save(snippet);

        searchIndexService.indexSnippet(updatedSnippet);
    }
}