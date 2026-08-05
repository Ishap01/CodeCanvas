package com.codecanvas.snippetservice.service.impl;

import java.util.UUID;

import com.codecanvas.snippetservice.exception.ResourceNotFoundException;
import com.codecanvas.snippetservice.kafka.mapper.SnippetEventMapper;
import com.codecanvas.snippetservice.kafka.producer.SnippetEventProducer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codecanvas.snippetservice.dto.response.SnippetResponse;
import com.codecanvas.snippetservice.entity.Snippet;
import com.codecanvas.snippetservice.entity.SnippetTag;
import com.codecanvas.snippetservice.repository.SnippetRepository;
import com.codecanvas.snippetservice.mapper.SnippetMapper;
import com.codecanvas.snippetservice.service.ForkService;
import com.codecanvas.snippetservice.service.SearchIndexService;

import com.codecanvas.snippetservice.kafka.event.SnippetForkedEvent;

@Service
@Transactional
public class ForkServiceImpl implements ForkService {

        private final SnippetRepository snippetRepository;
        private final SnippetMapper snippetMapper;
        private final SearchIndexService searchIndexService;

        // Kafka Producer
        private final SnippetEventProducer snippetEventProducer;

        // Converts entity into Kafka event.
        private final SnippetEventMapper snippetEventMapper;

        public ForkServiceImpl(
                        SnippetRepository snippetRepository,
                        SnippetMapper snippetMapper,
                        SearchIndexService searchIndexService,
                        SnippetEventProducer snippetEventProducer,
                        SnippetEventMapper snippetEventMapper) {

                this.snippetRepository = snippetRepository;
                this.snippetMapper = snippetMapper;
                this.searchIndexService = searchIndexService;
                this.snippetEventProducer = snippetEventProducer;
                this.snippetEventMapper = snippetEventMapper;
        }

        @Override
        @CacheEvict(value = { "snippets", "public_snippets", "user_snippets" }, allEntries = true)
        public SnippetResponse forkSnippet(
                        UUID snippetId,
                        UUID currentUserId) {

                if (snippetId == null) {
                        throw new IllegalArgumentException(
                                        "Snippet id is required");
                }

                if (currentUserId == null) {
                        throw new IllegalArgumentException(
                                        "Authenticated user id is required");
                }

                Snippet originalSnippet = snippetRepository.findById(snippetId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Snippet not found"));

                /*
                 * Optional:
                 * Prevent users from forking their own snippet.
                 */
                if (originalSnippet.getUserId().equals(currentUserId)) {
                        throw new IllegalArgumentException(
                                        "You cannot fork your own snippet.");
                }

                Snippet forkedSnippet = new Snippet();

                forkedSnippet.setUserId(currentUserId);

                forkedSnippet.setTitle(
                                originalSnippet.getTitle());

                forkedSnippet.setDescription(
                                originalSnippet.getDescription());

                forkedSnippet.setCode(
                                originalSnippet.getCode());

                forkedSnippet.setLanguage(
                                originalSnippet.getLanguage());

                forkedSnippet.setFramework(
                                originalSnippet.getFramework());

                forkedSnippet.setVisibility(
                                originalSnippet.getVisibility());

                forkedSnippet.setStatus(
                                originalSnippet.getStatus());

                forkedSnippet.setCategory(
                                originalSnippet.getCategory());

                forkedSnippet.setPreviewImageUrl(
                                originalSnippet.getPreviewImageUrl());

                forkedSnippet.setPreviewImagePublicId(
                                originalSnippet.getPreviewImagePublicId());

                forkedSnippet.setParentSnippetId(
                                originalSnippet.getSnippetId());

                /*
                 * Fork starts with fresh engagement.
                 */
                forkedSnippet.setViewCount(0L);
                forkedSnippet.setLikeCount(0L);
                forkedSnippet.setBookmarkCount(0L);
                forkedSnippet.setCommentCount(0L);
                forkedSnippet.setForkCount(0L);

                /*
                 * Copy tags.
                 */
                if (originalSnippet.getSnippetTags() != null) {

                        for (SnippetTag originalSnippetTag : originalSnippet.getSnippetTags()) {

                                SnippetTag newSnippetTag = new SnippetTag();

                                newSnippetTag.setSnippet(
                                                forkedSnippet);

                                newSnippetTag.setTag(
                                                originalSnippetTag.getTag());

                                forkedSnippet.getSnippetTags()
                                                .add(newSnippetTag);
                        }
                }

                /*
                 * Increase fork count of original snippet.
                 */
                originalSnippet.setForkCount(
                                originalSnippet.getForkCount() + 1);

                /*
                 * Save fork.
                 */
                Snippet savedFork = snippetRepository.save(
                                forkedSnippet);

                /*
                 * Save original snippet.
                 */
                snippetRepository.save(
                                originalSnippet);

                /*
                 * Create Kafka event
                 * after successful fork.
                 */
                SnippetForkedEvent event = snippetEventMapper.toSnippetForkedEvent(
                                originalSnippet,
                                savedFork);

                /*
                 * Publish fork event.
                 */
                snippetEventProducer.publishSnippetForkedEvent(
                                event);

                /*
                 * Update Elasticsearch.
                 */
                searchIndexService.indexSnippet(
                                originalSnippet);

                searchIndexService.indexSnippet(
                                savedFork);

                return snippetMapper.toResponse(
                                savedFork);
        }

}