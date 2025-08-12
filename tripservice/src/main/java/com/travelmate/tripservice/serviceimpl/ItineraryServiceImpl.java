package com.travelmate.tripservice.serviceimpl;

import com.travelmate.tripservice.entity.Itinerary;
import com.travelmate.tripservice.exceptions.DestinationNotFoundException;
import com.travelmate.tripservice.exceptions.ItineraryNotFoundException;
import com.travelmate.tripservice.exceptions.UnauthorizedAccessException;
import com.travelmate.tripservice.model.ItineraryModel;
import com.travelmate.tripservice.mapper.ItineraryMapper;
import com.travelmate.tripservice.repository.DestinationRepository;
import com.travelmate.tripservice.repository.ItineraryRepository;
import com.travelmate.tripservice.service.ItineraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.travelmate.tripservice.client.AuthServiceClient;
import com.travelmate.tripservice.client.UserServiceClient;

@Service
public class ItineraryServiceImpl implements ItineraryService {
    @Autowired
    private ItineraryRepository itineraryRepository;

    @Autowired
    private DestinationRepository destinationRepository;

    @Autowired
    private AuthServiceClient authServiceClient;

    @Autowired
    private UserServiceClient userServiceClient;

    private static final Logger logger = LoggerFactory.getLogger(ItineraryServiceImpl.class);

    @Override
    public ItineraryModel createItinerary(String token, ItineraryModel itineraryModel) throws UnauthorizedAccessException {
        String role = authServiceClient.validateToken(token).getRole();
        if (!"admin".equalsIgnoreCase(role) && !"subadmin".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("User does not have permission to create itineraries");
        }
        logger.info("Creating itinerary: {}", itineraryModel.itineraryName());
        Itinerary entity = ItineraryMapper.toEntity(itineraryModel);
        Itinerary saved = itineraryRepository.save(entity);
        return ItineraryMapper.toModel(saved);
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
    public List<ItineraryModel> getAllItineraries() {
        logger.info("Fetching all itineraries");
        return itineraryRepository.findAll().stream().map(ItineraryMapper::toModel).toList();
    }

    @Override
    public ItineraryModel updateItinerary(String token, ItineraryModel updatedItineraryModel) throws ItineraryNotFoundException, UnauthorizedAccessException {
        String role = authServiceClient.validateToken(token).getRole();
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
        return ItineraryMapper.toModel(saved);
    }

    @Override
    public ItineraryModel deleteItinerary(String token, Long id) throws ItineraryNotFoundException, UnauthorizedAccessException {
        String role = authServiceClient.validateToken(token).getRole();
        if (!"admin".equalsIgnoreCase(role) && !"subadmin".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("User does not have permission to delete itineraries");
        }
        logger.info("Deleting itinerary id: {}", id);
        ItineraryModel itineraryModel = getItineraryById(id).orElseThrow(() -> new ItineraryNotFoundException(id));
        if (itineraryModel != null) {
            itineraryRepository.deleteById(id);
        }
        return itineraryModel;
    }

    @Override
    public List<ItineraryModel> getItinerariesByDestinationId(Long destinationId) throws DestinationNotFoundException {
        logger.info("Fetching itineraries by destination id: {}", destinationId);
        if (destinationRepository.findById(destinationId).isPresent()) {
            return itineraryRepository.findByDestinationId(destinationId).stream().map(ItineraryMapper::toModel).collect(Collectors.toList());
        } else {
            logger.error("Destination with id {} not found", destinationId);
            throw new DestinationNotFoundException(destinationId);
        }
    }

}
