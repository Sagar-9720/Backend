package com.travelmate.tripservice.serviceimpl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.travelmate.tripservice.entity.ItineraryActivity;
import com.travelmate.tripservice.exceptions.UnauthorizedAccessException;
import com.travelmate.tripservice.mapper.ItineraryActivityMapper;
import com.travelmate.tripservice.model.ItineraryActivityModel;
import com.travelmate.tripservice.repository.ItineraryActivityRepository;
import com.travelmate.tripservice.service.ItineraryActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItineraryActivityServiceImpl implements ItineraryActivityService {

    @Autowired
    private ItineraryActivityRepository repository;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Value("${elasticsearch.index.itineraryactivities:itineraryactivities}")
    private String itineraryActivityIndex;

    @Override
    public ItineraryActivityModel create(ItineraryActivityModel model) {
        ItineraryActivity entity = ItineraryActivityMapper.toEntity(model);
        ItineraryActivityModel savedModel = ItineraryActivityMapper.toModel(repository.save(entity));
        indexItineraryActivity(savedModel);
        return savedModel;
    }

    @Override
    public ItineraryActivityModel update(Long id, ItineraryActivityModel model) {
        ItineraryActivity entity = repository.findById(id).orElseThrow(() -> new RuntimeException("ItineraryActivity not found"));
        entity.setActivityName(model.activityName());
        entity.setDescription(model.description());
        ItineraryActivityModel savedModel = ItineraryActivityMapper.toModel(repository.save(entity));
        indexItineraryActivity(savedModel);
        return savedModel;
    }

    @Override
    public ItineraryActivityModel getById(Long id) {
        return repository.findById(id).map(ItineraryActivityMapper::toModel).orElseThrow();
    }

    @Override
    public List<ItineraryActivityModel> getAll() {
        return repository.findAll().stream().map(ItineraryActivityMapper::toModel).toList();
    }

    @Override
    public List<Map<String, String>> suggest(String keyword) {
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(itineraryActivityIndex)
                .query(q -> q
                    .bool(b -> b
                        .should(sh -> sh
                            // Match prefixes (starts with)
                            .prefix(p -> p
                                .field("activityName")
                                .value(keyword.toLowerCase())
                            )
                        )
                        .should(sh -> sh
                            // Match anywhere in the text
                            .wildcard(w -> w
                                .field("activityName")
                                .value("*" + keyword.toLowerCase() + "*")
                            )
                        )
                        .minimumShouldMatch("1")
                    )
                )
                .size(10)
            );

            SearchResponse<ItineraryActivityModel> response = elasticsearchClient.search(searchRequest, ItineraryActivityModel.class);
            return response.hits().hits().stream()
                .map(Hit::source)
                .filter(java.util.Objects::nonNull)
                .map(activityModel -> {
                    Map<String, String> result = new HashMap<>();
                    result.put("id", activityModel.id().toString());
                    result.put("activityName", activityModel.activityName());
                    return result;
                })
                .toList();
        } catch (Exception e) {
            // Log error
            return List.of();
        }
    }

    public void indexItineraryActivity(ItineraryActivityModel model) {
        try {
            IndexRequest<ItineraryActivityModel> request = IndexRequest.of(i -> i.index(itineraryActivityIndex).id(model.id() != null ? model.id().toString() : model.activityName()).document(model));
            elasticsearchClient.index(request);
        } catch (Exception e) {
            // Log error
        }
    }
}
