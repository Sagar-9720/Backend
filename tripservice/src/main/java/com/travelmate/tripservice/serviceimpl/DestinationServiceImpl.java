package com.travelmate.tripservice.serviceimpl;

import com.travelmate.tripservice.client.AuthServiceClient;
import com.travelmate.tripservice.entity.Country;
import com.travelmate.tripservice.entity.Destination;
import com.travelmate.tripservice.entity.Region;
import com.travelmate.tripservice.exceptions.DestinationExistException;
import com.travelmate.tripservice.exceptions.DestinationNotFoundException;
import com.travelmate.tripservice.exceptions.RegionNotFoundException;
import com.travelmate.tripservice.mapper.DestinationMapper;
import com.travelmate.tripservice.model.DestinationModel;
import com.travelmate.tripservice.repository.CountryRepository;
import com.travelmate.tripservice.repository.DestinationRepository;
import com.travelmate.tripservice.repository.RegionRepository;
import com.travelmate.tripservice.service.DestinationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DestinationServiceImpl implements DestinationService {

    @Autowired
    private DestinationRepository destinationRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private AuthServiceClient authServiceClient;

    private static final Logger logger = LoggerFactory.getLogger(DestinationServiceImpl.class);

    @Override
    public DestinationModel createDestination(DestinationModel destinationModel) throws DestinationExistException {
        logger.info("Creating destination: {}", destinationModel.getName());
        Destination entity = DestinationMapper.toEntity(destinationModel);
        List<Destination> existing = destinationRepository.findByNameContainingIgnoreCase(entity.getName());
        if (!existing.isEmpty()) {
            throw new DestinationExistException(entity.getName());
        }
        Region region = entity.getRegion();
        Country country = region.getCountry();
        if (region.getId() != null) {
            entity.setRegion(regionRepository.findById(region.getId()).get());
        }
        if (country.getId() != null) {
            entity.getRegion().setCountry(countryRepository.findById(country.getId()).get());
        }
        Destination saved = destinationRepository.save(entity);
        return DestinationMapper.toModel(saved);
    }

    @Override
    public Optional<DestinationModel> getDestinationById(Long id) {
        logger.info("Fetching destination by id: {}", id);
        return destinationRepository.findById(id).map(DestinationMapper::toModel);
    }

    @Override
    public List<DestinationModel> getAllDestinations() {
        logger.info("Fetching all destinations");
        return destinationRepository.findAll().stream().map(DestinationMapper::toModel).toList();
    }

    @Override
    public DestinationModel updateDestination(String token, DestinationModel model) throws DestinationNotFoundException {
        String role = authServiceClient.validateToken(token).getRole();
        if (role != null && !"user".equalsIgnoreCase(role)) {
            logger.info("Updating destination id: {}", model.getId());
            Destination entity = DestinationMapper.toEntity(model);
            entity.setId(model.getId());
            Destination saved = destinationRepository.save(entity);
            return DestinationMapper.toModel(saved);
        } else {
            throw new SecurityException("User role is not authorized to update destination");
        }
    }

    @Override
    public List<DestinationModel> getDestinationsByRegionId(Long regionId) {
        logger.info("Fetching destinations by region id: {}", regionId);
        Optional<Region> region = regionRepository.findById(regionId);
        if (region.isPresent()) {
            List<Destination> destinations = destinationRepository.findByRegion(region.get());
            return destinations.stream().map(DestinationMapper::toModel).toList();
        }
        throw new RegionNotFoundException(regionId);
    }

    @Override
    public List<DestinationModel> getDestinationsByCountryId(Long countryId) {
        return List.of();
    }

    @Override
    public List<DestinationModel> searchDestinationByName(String name) {
        logger.info("Searching destinations by name: {}", name);
        List<Destination> destinations = destinationRepository.findByNameContainingIgnoreCase(name);
        if (destinations.isEmpty()) {
            throw new DestinationNotFoundException(name);
        }
        return destinations.stream().map(DestinationMapper::toModel).toList();
    }


    @Override
    public DestinationModel deleteDestination(String token, DestinationModel model) {
        String role = authServiceClient.validateToken(token).getRole();
        if (role != null && !"user".equalsIgnoreCase(role)) {
            DestinationModel deleted = destinationRepository.findById(model.getId()).map(DestinationMapper::toModel).orElse(null);
            destinationRepository.deleteById(model.getId());
            logger.info("Deleting destination id: {}", model.getId());
            return deleted;
        } else {
            throw new SecurityException("User role is not authorized to delete destination");
        }
    }


}
