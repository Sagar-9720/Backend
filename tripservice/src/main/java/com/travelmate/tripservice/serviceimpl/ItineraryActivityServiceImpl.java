package com.travelmate.tripservice.serviceimpl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.travelmate.tripservice.entity.ItineraryActivity;
import com.travelmate.tripservice.mapper.ItineraryActivityMapper;
import com.travelmate.tripservice.model.ItineraryActivityModel;
import com.travelmate.tripservice.repository.ItineraryActivityRepository;
import com.travelmate.tripservice.service.ItineraryActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItineraryActivityServiceImpl implements ItineraryActivityService {

    @Autowired
    private ItineraryActivityRepository repository;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Value("${elasticsearch.index.itineraryactivities:itineraryactivities}")
    private String itineraryActivityIndex;

    private static final Logger logger = LoggerFactory.getLogger(ItineraryActivityServiceImpl.class);

    @Override
    public ItineraryActivityModel create(ItineraryActivityModel model) {
        logger.info("Creating itinerary activity: {}", model.activityName());

        ItineraryActivity entity = new ItineraryActivity();
        entity.setActivityName(model.activityName());
        entity.setDescription(model.description());

        ItineraryActivity saved = repository.save(entity);
        ItineraryActivityModel savedModel = ItineraryActivityMapper.toModel(saved);
        indexItineraryActivity(savedModel);
        return savedModel;
    }

    @Override
    public ItineraryActivityModel update(Long id, ItineraryActivityModel model) {
        logger.info("Updating itinerary activity with id: {}", id);

        ItineraryActivity entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Itinerary activity not found with id: " + id));

        entity.setActivityName(model.activityName());
        entity.setDescription(model.description());

        ItineraryActivity saved = repository.save(entity);
        ItineraryActivityModel savedModel = ItineraryActivityMapper.toModel(saved);
        indexItineraryActivity(savedModel);
        return savedModel;
    }

    @Override
    public ItineraryActivityModel getById(Long id) {
        logger.info("Fetching itinerary activity by id: {}", id);
        return repository.findById(id).map(ItineraryActivityMapper::toModel).orElseThrow(() -> new RuntimeException("Itinerary activity not found with id: " + id));
    }

    @Override
    public List<ItineraryActivityModel> getAll() {
        logger.info("Fetching all itinerary activities");
        return repository.findAll().stream().map(ItineraryActivityMapper::toModel).toList();
    }

    @Override
    public List<ItineraryActivityModel> suggest(String keyword) {
        logger.info("Suggesting itinerary activities for keyword: {}", keyword);
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s.index(itineraryActivityIndex).query(q -> q.bool(b -> b.should(sh -> sh.prefix(p -> p.field("activityName.keyword").value(keyword.toLowerCase()))).should(sh -> sh.wildcard(w -> w.field("activityName").value("*" + keyword.toLowerCase() + "*"))).minimumShouldMatch("1"))).size(10));

            SearchResponse<ItineraryActivityModel> response = elasticsearchClient.search(searchRequest, ItineraryActivityModel.class);

            return response.hits().hits().stream().map(Hit::source).filter(java.util.Objects::nonNull).toList();

        } catch (Exception e) {
            logger.error("Failed to suggest itinerary activities from Elasticsearch: {}", e.getMessage(), e);
            return List.of();
        }
    }

    private void indexItineraryActivity(ItineraryActivityModel model) {
        try {
            IndexRequest<ItineraryActivityModel> request = IndexRequest.of(i -> i.index(itineraryActivityIndex).id(model.id() != null ? model.id().toString() : model.activityName()).document(model));
            elasticsearchClient.index(request);
        } catch (Exception e) {
            logger.error("Failed to index itinerary activity in Elasticsearch: {}", e.getMessage(), e);
        }
    }
}
