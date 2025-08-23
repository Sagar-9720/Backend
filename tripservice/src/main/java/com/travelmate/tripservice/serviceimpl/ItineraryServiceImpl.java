package com.travelmate.tripservice.serviceimpl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.travelmate.tripservice.entity.Itinerary;
import com.travelmate.tripservice.exceptions.DestinationNotFoundException;
import com.travelmate.tripservice.exceptions.ItineraryNotFoundException;
import com.travelmate.tripservice.exceptions.UnauthorizedAccessException;
import com.travelmate.tripservice.model.ItineraryModel;
import com.travelmate.tripservice.mapper.ItineraryMapper;
import com.travelmate.tripservice.repository.ItineraryRepository;
import com.travelmate.tripservice.service.ItineraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItineraryServiceImpl implements ItineraryService {

    @Autowired
    private ItineraryRepository itineraryRepository;

    @Autowired
    private DestinationServiceImpl destinationService;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Value("${elasticsearch.index.itineraries:itineraries}")
    private String itineraryIndex;

    private static final Logger logger = LoggerFactory.getLogger(ItineraryServiceImpl.class);

    @Override
    public ItineraryModel createItinerary(String role, ItineraryModel itineraryModel) throws UnauthorizedAccessException {
        if (!"admin".equalsIgnoreCase(role) && !"subadmin".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("User does not have permission to create itineraries");
        }
        logger.info("Creating itinerary: {}", itineraryModel.itineraryName());
        Itinerary entity = ItineraryMapper.toEntity(itineraryModel);
        Itinerary saved = itineraryRepository.save(entity);
        ItineraryModel savedModel = ItineraryMapper.toModel(saved);
        indexItinerary(savedModel);
        return savedModel;
    }

    public void indexItinerary(ItineraryModel model) {
        try {
            IndexRequest<ItineraryModel> request = IndexRequest.of(i -> i.index(itineraryIndex).id(model.id() != null ? model.id().toString() : model.itineraryName()).document(model));
            elasticsearchClient.index(request);
        } catch (Exception e) {
            logger.error("Failed to index itinerary in Elasticsearch: {}", e.getMessage());
        }
    }

    @Override
    public Optional<ItineraryModel> getItineraryById(Long id) throws ItineraryNotFoundException {
        logger.info("Fetching itinerary by id: {}", id);
        return itineraryRepository.findById(id).map(ItineraryMapper::toModel).or(() -> {
            logger.error("Itinerary with id {} not found", id);
            throw new ItineraryNotFoundException("Itinerary not found with id: " + id);
        });
    }

    @Override
    public List<ItineraryModel> getAllItineraries() throws UnauthorizedAccessException {
        logger.info("Fetching all itineraries");
        return itineraryRepository.findAll().stream().map(ItineraryMapper::toModel).toList();
    }

    @Override
    public ItineraryModel updateItinerary(String role, ItineraryModel updatedItineraryModel) throws ItineraryNotFoundException, UnauthorizedAccessException {
        if (role != null && role.compareToIgnoreCase("user") == 0) {
            throw new UnauthorizedAccessException("User does not have permission to update itineraries");
        }
        logger.info("Updating itinerary: {}", updatedItineraryModel.itineraryName());
        Long id = updatedItineraryModel.id();
        if (id == null || !itineraryRepository.existsById(id)) {
            logger.error("Itinerary with id {} not found for update", id);
            throw new ItineraryNotFoundException(id);
        }
        Itinerary updatedEntity = ItineraryMapper.toEntity(updatedItineraryModel);
        Itinerary saved = itineraryRepository.save(updatedEntity);
        ItineraryModel savedModel = ItineraryMapper.toModel(saved);
        indexItinerary(savedModel);
        return savedModel;
    }

    @Override
    public List<ItineraryModel> getItinerariesByDestinationId(Long destinationId) throws DestinationNotFoundException {
        logger.info("Fetching itineraries by destination id: {}", destinationId);
        if (destinationService.getDestinationById(destinationId) != null) {
            return itineraryRepository.findByDestinationId(destinationId).stream().map(ItineraryMapper::toModel).collect(Collectors.toList());
        } else {
            logger.error("Destination with id {} not found", destinationId);
            throw new DestinationNotFoundException(destinationId);
        }
    }

    @Override
    public List<Map<String, String>> suggestItineraries(String keyword, Long destinationId) {
        logger.info("Suggesting itineraries for keyword: {} and destinationId: {}", keyword, destinationId);
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(itineraryIndex)
                .query(q -> q
                    .bool(b -> b
                        .should(sh -> sh
                            // Match prefixes (starts with)
                            .prefix(p -> p
                                .field("itineraryName")
                                .value(keyword.toLowerCase())
                            )
                        )
                        .should(sh -> sh
                            // Match anywhere in the text
                            .wildcard(w -> w
                                .field("itineraryName")
                                .value("*" + keyword.toLowerCase() + "*")
                            )
                        )
                        .minimumShouldMatch("1")
                    )
                )
                .size(10)
            );

            SearchResponse<ItineraryModel> response = elasticsearchClient.search(searchRequest, ItineraryModel.class);
            return response.hits().hits().stream()
                .map(Hit::source)
                .filter(java.util.Objects::nonNull)
                .filter(itinerary -> destinationId == null ||
                       (itinerary.destination() != null &&
                        itinerary.destination().getId() != null &&
                        itinerary.destination().getId().equals(destinationId)))
                .map(itineraryModel -> {
                    Map<String, String> result = new HashMap<>();
                    result.put("id", itineraryModel.id().toString());
                    result.put("name", itineraryModel.itineraryName());
                    return result;
                })
                .toList();
        } catch (Exception e) {
            logger.error("Failed to suggest itineraries from Elasticsearch: {}", e.getMessage());
            return List.of();
        }
    }

}
