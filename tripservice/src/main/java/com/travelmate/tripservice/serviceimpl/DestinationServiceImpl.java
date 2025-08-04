package com.travelmate.tripservice.serviceimpl;

import com.travelmate.tripservice.domain.Country;
import com.travelmate.tripservice.domain.Destination;
import com.travelmate.tripservice.domain.Region;
import com.travelmate.tripservice.exceptions.DestinationExistException;
import com.travelmate.tripservice.exceptions.DestinationNotFoundException;
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
    public DestinationModel updateDestination(Long id, DestinationModel updatedDestinationModel) throws DestinationNotFoundException {
        logger.info("Updating destination id: {}", id);
        Destination entity = DestinationMapper.toEntity(updatedDestinationModel);
        entity.setId(id);
        Destination saved = destinationRepository.save(entity);
        return DestinationMapper.toModel(saved);
    }

    @Override
    public void deleteDestination(Long id) {
        logger.info("Deleting destination id: {}", id);
        destinationRepository.deleteById(id);
    }

    @Override
    public List<DestinationModel> searchDestinationsByName(String name) {
        logger.info("Searching destinations by name: {}", name);
        return destinationRepository.findByNameContainingIgnoreCase(name).stream().map(DestinationMapper::toModel).toList();
    }
}
