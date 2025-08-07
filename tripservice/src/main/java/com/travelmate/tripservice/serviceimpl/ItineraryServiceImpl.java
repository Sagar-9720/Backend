package com.travelmate.tripservice.serviceimpl;

import com.travelmate.tripservice.entity.Itinerary;
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

import com.travelmate.tripservice.client.AuthServiceClient;
import com.travelmate.tripservice.client.UserServiceClient;

@Service
public class ItineraryServiceImpl implements ItineraryService {
    @Autowired
    private ItineraryRepository itineraryRepository;

    @Autowired
    private AuthServiceClient authServiceClient;

    @Autowired
    private UserServiceClient userServiceClient;

    private static final Logger logger = LoggerFactory.getLogger(ItineraryServiceImpl.class);

    @Override
    public ItineraryModel createItinerary(String token, ItineraryModel itineraryModel) {
        String role = authServiceClient.validateToken(token).getRole();
        if (role != null && role.compareToIgnoreCase("user") == 0) {
            throw new IllegalStateException("User does not have permission to create itineraries");
        }
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
    public ItineraryModel updateItinerary(String token, ItineraryModel updatedItineraryModel) {
        String role = authServiceClient.validateToken(token).getRole();
        if (role != null && role.compareToIgnoreCase("user") == 0) {
            throw new IllegalStateException("User does not have permission to update itineraries");
        }
        logger.info("Updating itinerary: {}", updatedItineraryModel.getItineraryName());
        Itinerary updatedEntity = ItineraryMapper.toEntity(updatedItineraryModel);
        Itinerary saved = itineraryRepository.save(updatedEntity);
        return ItineraryMapper.toModel(saved);
    }

    @Override
    public ItineraryModel deleteItinerary(String token, Long id) {
        String role = authServiceClient.validateToken(token).getRole();
        if (role != null && role.compareToIgnoreCase("user") == 0) {
            throw new IllegalStateException("User does not have permission to delete itineraries");
        }
        logger.info("Deleting itinerary id: {}", id);
        ItineraryModel itineraryModel = getItineraryById(id).orElse(null);
        if (itineraryModel != null) {
            itineraryRepository.deleteById(id);
        }
        return itineraryModel;
    }

    @Override
    public List<ItineraryModel> getItinerariesByDestinationId(Long destinationId) {
        logger.info("Fetching itineraries by destination id: {}", destinationId);
        return itineraryRepository.findByDestinationId(destinationId).stream()
                .map(ItineraryMapper::toModel)
                .toList();
    }

}
