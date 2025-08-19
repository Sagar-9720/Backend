package com.travelmate.journalservice.serviceimpl;

import com.travelmate.journalservice.repository.TagRepository;
import com.travelmate.journalservice.service.TagService;
import com.travelmate.journalservice.model.TagModel;
import com.travelmate.journalservice.mapper.TagMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Value("${elasticsearch.index.tags:tags}")
    private String tagIndex;

    private static final Logger logger = LoggerFactory.getLogger(TagServiceImpl.class);


    @Override
    public void indexTag(TagModel tagModel) {
        try {
            IndexRequest<TagModel> request = IndexRequest.of(i -> i.index(tagIndex).id(tagModel.id() != null ? tagModel.id().toString() : tagModel.name()).document(tagModel));
            elasticsearchClient.index(request);
        } catch (Exception e) {
            logger.error("Failed to index tag in Elasticsearch: {}", e.getMessage());
        }
    }


    @Override
    public List<TagModel> getAllTags() {
        logger.info("Fetching all tags");
        return tagRepository.findAll().stream().map(TagMapper::toModel).toList();
    }


    @Override
    public List<String> suggestTags(String query) {
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s.index(tagIndex).query(q -> q.fuzzy(f -> f.field("name").value(query).fuzziness("AUTO"))).size(10));
            SearchResponse<TagModel> response = elasticsearchClient.search(searchRequest, TagModel.class);
            return response.hits().hits().stream().map(Hit::source).filter(java.util.Objects::nonNull).map(TagModel::name).toList();
        } catch (Exception e) {
            logger.error("Failed to suggest tags from Elasticsearch: {}", e.getMessage());
            return List.of();
        }
    }
}
