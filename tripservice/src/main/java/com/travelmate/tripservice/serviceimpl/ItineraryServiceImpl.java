package com.travelmate.tripservice.serviceimpl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.travelmate.tripservice.entity.Destination;
import com.travelmate.tripservice.entity.Itinerary;
import com.travelmate.tripservice.exceptions.DestinationNotFoundException;
import com.travelmate.tripservice.exceptions.ItineraryNotFoundException;
import com.travelmate.tripservice.exceptions.UnauthorizedAccessException;
import com.travelmate.tripservice.mapper.DestinationMapper;
import com.travelmate.tripservice.mapper.ItineraryMapper;
import com.travelmate.tripservice.model.ItineraryModel;
import com.travelmate.tripservice.repository.DestinationRepository;
import com.travelmate.tripservice.repository.ItineraryRepository;
import com.travelmate.tripservice.service.ItineraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItineraryServiceImpl implements ItineraryService {

    @Autowired
    private ItineraryRepository itineraryRepository;

    @Autowired
    private DestinationRepository destinationRepository;

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

        Itinerary itinerary = new Itinerary();
        itinerary.setItineraryName(itineraryModel.itineraryName());
        itinerary.setDescription(itineraryModel.description());

        // handle destination properly
        if (itineraryModel.destination() != null) {
            if (itineraryModel.destination().id() != null) {
                Destination destination = destinationRepository.findById(itineraryModel.destination().id()).orElseThrow(() -> new DestinationNotFoundException(itineraryModel.destination().id()));
                itinerary.setDestination(destination);
            } else {
                itinerary.setDestination(DestinationMapper.toEntity(itineraryModel.destination()));
            }
        }

        Itinerary saved = itineraryRepository.save(itinerary);
        ItineraryModel savedModel = ItineraryMapper.toModel(saved);

        indexItinerary(savedModel);

        return savedModel;
    }

    private void indexItinerary(ItineraryModel model) {
        try {
            IndexRequest<ItineraryModel> request = IndexRequest.of(i -> i.index(itineraryIndex).id(model.id() != null ? model.id().toString() : model.itineraryName()).document(model));
            elasticsearchClient.index(request);
        } catch (Exception e) {
            logger.error("Failed to index itinerary in Elasticsearch: {}", e.getMessage(), e);
        }
    }

    @Override
    public ItineraryModel getItineraryById(Long id) throws ItineraryNotFoundException {
        logger.info("Fetching itinerary by id: {}", id);
        return itineraryRepository.findById(id).map(ItineraryMapper::toModel).orElseThrow(() -> new ItineraryNotFoundException("Itinerary not found with id: " + id));
    }

    @Override
    public List<ItineraryModel> getAllItineraries() {
        logger.info("Fetching all itineraries");
        return itineraryRepository.findAll().stream().map(ItineraryMapper::toModel).toList();
    }

    @Override
    public ItineraryModel updateItinerary(String role, ItineraryModel updatedItineraryModel) throws ItineraryNotFoundException, UnauthorizedAccessException {
        if ("user".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("User does not have permission to update itineraries");
        }
        logger.info("Updating itinerary: {}", updatedItineraryModel.itineraryName());

        Long id = updatedItineraryModel.id();
        Itinerary existing = itineraryRepository.findById(id).orElseThrow(() -> new ItineraryNotFoundException(id));

        existing.setItineraryName(updatedItineraryModel.itineraryName());
        existing.setDescription(updatedItineraryModel.description());

        // handle destination properly
        if (updatedItineraryModel.destination() != null) {
            if (updatedItineraryModel.destination().id() != null) {
                Destination destination = destinationRepository.findById(updatedItineraryModel.destination().id()).orElseThrow(() -> new DestinationNotFoundException(updatedItineraryModel.destination().id()));
                existing.setDestination(destination);
            } else {
                existing.setDestination(DestinationMapper.toEntity(updatedItineraryModel.destination()));
            }
        }

        Itinerary saved = itineraryRepository.save(existing);
        ItineraryModel savedModel = ItineraryMapper.toModel(saved);

        indexItinerary(savedModel);

        return savedModel;
    }

    @Override
    public List<ItineraryModel> getItinerariesByDestinationId(Long destinationId) throws DestinationNotFoundException {
        logger.info("Fetching itineraries by destination id: {}", destinationId);
        if (!destinationRepository.existsById(destinationId)) {
            logger.error("Destination with id {} not found", destinationId);
            throw new DestinationNotFoundException(destinationId);
        }
        return itineraryRepository.findByDestinationId(destinationId).stream().map(ItineraryMapper::toModel).collect(Collectors.toList());
    }

    @Override
    public List<ItineraryModel> suggestItineraries(String keyword, Long destinationId) {
        logger.info("Suggesting itineraries for keyword: {} and destinationId: {}", keyword, destinationId);
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s.index(itineraryIndex).query(q -> q.bool(b -> b.should(sh -> sh.prefix(p -> p.field("itineraryName.keyword").value(keyword.toLowerCase()))).should(sh -> sh.wildcard(w -> w.field("itineraryName").value("*" + keyword.toLowerCase() + "*"))).minimumShouldMatch("1"))).size(10));

            SearchResponse<ItineraryModel> response = elasticsearchClient.search(searchRequest, ItineraryModel.class);
            return response.hits().hits().stream().map(Hit::source).filter(java.util.Objects::nonNull).filter(itinerary -> destinationId == null || (itinerary.destination() != null && itinerary.destination().id() != null && itinerary.destination().id().equals(destinationId))).toList();
        } catch (Exception e) {
            logger.error("Failed to suggest itineraries from Elasticsearch: {}", e.getMessage(), e);
            return List.of();
        }
    }
}
