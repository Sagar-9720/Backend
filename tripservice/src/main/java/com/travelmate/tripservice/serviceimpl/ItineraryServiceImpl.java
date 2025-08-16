package com.travelmate.tripservice.serviceimpl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.travelmate.tripservice.entity.Itinerary;
import com.travelmate.tripservice.exceptions.DestinationNotFoundException;
import com.travelmate.tripservice.exceptions.ItineraryNotFoundException;
import com.travelmate.tripservice.exceptions.UnauthorizedAccessException;
import com.travelmate.tripservice.model.ItineraryModel;
import com.travelmate.tripservice.mapper.ItineraryMapper;
import com.travelmate.tripservice.repository.DestinationRepository;
import com.travelmate.tripservice.repository.ItineraryRepository;
import com.travelmate.tripservice.service.ItineraryService;
import com.travelmate.tripservice.service.TokenValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItineraryServiceImpl implements ItineraryService {

    @Autowired
    private ItineraryRepository itineraryRepository;

    @Autowired
    private DestinationServiceImpl destinationService;

    @Autowired
    private TokenValidationService tokenValidationService;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Value("${elasticsearch.index.itineraries:itineraries}")
    private String itineraryIndex;

    private static final Logger logger = LoggerFactory.getLogger(ItineraryServiceImpl.class);

    @Override
    public ItineraryModel createItinerary(String token, ItineraryModel itineraryModel) throws UnauthorizedAccessException, JsonProcessingException {
        String role = tokenValidationService.getRole(token);
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
    public Optional<ItineraryModel> getItineraryById(String token, Long id) throws ItineraryNotFoundException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Unauthorized access to Itinerary by id: " + id);
        }
        logger.info("Fetching itinerary by id: {}", id);
        return itineraryRepository.findById(id).map(ItineraryMapper::toModel).or(() -> {
            logger.error("Itinerary with id {} not found", id);
            throw new ItineraryNotFoundException("Itinerary not found with id: " + id);
        });
    }

    @Override
    public List<ItineraryModel> getAllItineraries(String token) throws UnauthorizedAccessException, JsonProcessingException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Unauthorized access to itineraries");
        }
        logger.info("Fetching all itineraries");
        return itineraryRepository.findAll().stream().map(ItineraryMapper::toModel).toList();
    }

    @Override
    public ItineraryModel updateItinerary(String token, ItineraryModel updatedItineraryModel) throws ItineraryNotFoundException, UnauthorizedAccessException, JsonProcessingException {
        String role = tokenValidationService.getRole(token);
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
    public ItineraryModel deleteItinerary(String token, Long id) throws ItineraryNotFoundException, UnauthorizedAccessException, JsonProcessingException {
        String role = tokenValidationService.getRole(token);
        if (!"admin".equalsIgnoreCase(role) && !"subadmin".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("User does not have permission to delete itineraries");
        }
        logger.info("Deleting itinerary id: {}", id);
        if (!itineraryRepository.existsById(id)) {
            logger.error("Itinerary with id {} not found for deletion", id);
            throw new ItineraryNotFoundException(id);
        }
        ItineraryModel itineraryModel = itineraryRepository.findById(id).map(ItineraryMapper::toModel).orElseThrow(() -> new ItineraryNotFoundException(id));
        itineraryRepository.deleteById(id);
        logger.info("Itinerary with id {} deleted successfully", id);
        return itineraryModel;
    }

    @Override
    public List<ItineraryModel> getItinerariesByDestinationId(String token, Long destinationId) throws DestinationNotFoundException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Unauthorized access to itineraries by destination id: " + destinationId);
        }
        logger.info("Fetching itineraries by destination id: {}", destinationId);
        if (destinationService.getDestinationById(token, destinationId) != null) {
            return itineraryRepository.findByDestinationId(destinationId).stream().map(ItineraryMapper::toModel).collect(Collectors.toList());
        } else {
            logger.error("Destination with id {} not found", destinationId);
            throw new DestinationNotFoundException(destinationId);
        }
    }

    @Override
    public List<ItineraryModel> suggestItineraries(String token, String keyword, Long destinationId) {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Unauthorized access to suggest itineraries");
        }
        logger.info("Suggesting itineraries for keyword: {} and destinationId: {}", keyword, destinationId);
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s.index(itineraryIndex).query(q -> q.fuzzy(f -> f.field("itineraryName").value(keyword).fuzziness("AUTO"))).size(10));
            SearchResponse<ItineraryModel> response = elasticsearchClient.search(searchRequest, ItineraryModel.class);
            return response.hits().hits().stream().map(Hit::source).filter(java.util.Objects::nonNull).filter(itinerary -> destinationId == null || (itinerary.destination().getId() != null && itinerary.destination().getId().equals(destinationId))).toList();
        } catch (Exception e) {
            logger.error("Failed to suggest itineraries from Elasticsearch: {}", e.getMessage());
            return List.of();
        }
    }

}
