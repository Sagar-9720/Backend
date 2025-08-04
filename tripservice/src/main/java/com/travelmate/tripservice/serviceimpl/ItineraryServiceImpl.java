package com.travelmate.tripservice.serviceimpl;

import com.travelmate.tripservice.domain.Itinerary;
import com.travelmate.tripservice.model.ItineraryModel;
import com.travelmate.tripservice.mapper.ItineraryMapper;
import com.travelmate.tripservice.repository.ItineraryRepository;
import com.travelmate.tripservice.service.ItineraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class ItineraryServiceImpl implements ItineraryService {
    @Autowired
    private ItineraryRepository itineraryRepository;

    private static final Logger logger = LoggerFactory.getLogger(ItineraryServiceImpl.class);

    @Override
    public ItineraryModel createItinerary(ItineraryModel itineraryModel) {
        logger.info("Creating itinerary: {}", itineraryModel.getItineraryName());
        Itinerary entity = ItineraryMapper.toEntity(itineraryModel);
        Itinerary saved = itineraryRepository.save(entity);
        return ItineraryMapper.toModel(saved);
    }

    @Override
    public Optional<ItineraryModel> getItineraryById(Long id) {
        logger.info("Fetching itinerary by id: {}", id);
        return itineraryRepository.findById(id).map(ItineraryMapper::toModel);
    }

    @Override
    public List<ItineraryModel> getAllItineraries() {
        logger.info("Fetching all itineraries");
        return itineraryRepository.findAll().stream().map(ItineraryMapper::toModel).toList();
    }

    @Override
    public ItineraryModel updateItinerary(Long id, ItineraryModel updatedItineraryModel) {
        logger.info("Updating itinerary id: {}", id);
        return itineraryRepository.findById(id)
                .map(existing -> {
                    Itinerary updated = ItineraryMapper.toEntity(updatedItineraryModel);
                    updated.setId(id);
                    Itinerary saved = itineraryRepository.save(updated);
                    return ItineraryMapper.toModel(saved);
                })
                .orElse(null);
    }

    @Override
    public void deleteItinerary(Long id) {
        logger.info("Deleting itinerary id: {}", id);
        itineraryRepository.deleteById(id);
    }

    @Override
    public List<ItineraryModel> getItinerariesByDestinationId(Long destinationId) {
        logger.info("Fetching itineraries by destination id: {}", destinationId);
        // Implement custom query in repository if needed
        return List.of();
    }
}
