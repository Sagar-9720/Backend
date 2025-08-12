package com.travelmate.tripservice.serviceimpl;

import com.travelmate.tripservice.entity.Tag;
import com.travelmate.tripservice.exceptions.UnauthorizedAccessException;
import com.travelmate.tripservice.repository.TagRepository;
import com.travelmate.tripservice.service.TagService;
import com.travelmate.tripservice.model.TagModel;
import com.travelmate.tripservice.mapper.TagMapper;
import com.travelmate.tripservice.service.TokenValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Autowired
    private TokenValidationService tokenValidationService;

    @Value("${elasticsearch.index.tags:tags}")
    private String tagIndex;

    private static final Logger logger = LoggerFactory.getLogger(TagServiceImpl.class);

    @Override
    public TagModel saveTag(TagModel tagModel) {
        logger.info("Saving tag: {}", tagModel.name());
        Tag tag = TagMapper.toEntity(tagModel);
        Tag saved = tagRepository.save(tag);
        // Index in Elasticsearch
        indexTag(TagMapper.toModel(saved));
        return TagMapper.toModel(saved);
    }

    @Override
    public void indexTag(TagModel tagModel) {
        try {
            IndexRequest<TagModel> request = IndexRequest.of(i -> i
                .index(tagIndex)
                .id(tagModel.id() != null ? tagModel.id().toString() : tagModel.name())
                .document(tagModel)
            );
            elasticsearchClient.index(request);
        } catch (Exception e) {
            logger.error("Failed to index tag in Elasticsearch: {}", e.getMessage());
        }
    }

    @Override
    public Optional<TagModel> getTagById(Long id) {
        logger.info("Fetching tag by id: {}", id);
        return tagRepository.findById(id).map(TagMapper::toModel);
    }

    @Override
    public Optional<TagModel> getTagByName(String name) {
        logger.info("Fetching tag by name: {}", name);
        return tagRepository.findByName(name).map(TagMapper::toModel);
    }

    @Override
    public List<TagModel> getAllTags() {
        logger.info("Fetching all tags");
        return tagRepository.findAll().stream().map(TagMapper::toModel).toList();
    }

    @Override
    public void deleteTag(Long id) {
        logger.info("Deleting tag id: {}", id);
        try {
            tagRepository.deleteById(id);
        } catch (RuntimeException e) {
            logger.warn("Tag not found for deletion: {}", id);

        }
    }

    @Override
    public List<String> suggestTags(String query) {
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(tagIndex)
                .query(q -> q
                    .fuzzy(f -> f
                        .field("name")
                        .value(query)
                        .fuzziness("AUTO")
                    )
                )
                .size(10)
            );
            SearchResponse<TagModel> response = elasticsearchClient.search(searchRequest, TagModel.class);
            return response.hits().hits().stream()
                .map(Hit::source)
                .filter(java.util.Objects::nonNull)
                .map(TagModel::name)
                .toList();
        } catch (Exception e) {
            logger.error("Failed to suggest tags from Elasticsearch: {}", e.getMessage());
            return List.of();
        }
    }

    // Example of a secured method
    public List<String> suggestTags(String token, String query) {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Invalid token or unauthorized access");
        }
        return suggestTags(query);
    }
}
