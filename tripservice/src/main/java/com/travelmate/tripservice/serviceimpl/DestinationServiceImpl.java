package com.travelmate.tripservice.serviceimpl;

import com.travelmate.tripservice.entity.Country;
import com.travelmate.tripservice.entity.Destination;
import com.travelmate.tripservice.entity.Region;
import com.travelmate.tripservice.exceptions.*;
import com.travelmate.tripservice.mapper.DestinationMapper;
import com.travelmate.tripservice.model.DestinationModel;
import com.travelmate.tripservice.repository.CountryRepository;
import com.travelmate.tripservice.repository.DestinationRepository;
import com.travelmate.tripservice.repository.RegionRepository;
import com.travelmate.tripservice.service.DestinationService;
import com.travelmate.tripservice.service.TokenValidationService;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private ElasticsearchClient elasticsearchClient;

    @Autowired
    private TokenValidationService tokenValidationService;

    @Value("${elasticsearch.index.destinations:destinations}")
    private String destinationIndex;

    private static final Logger logger = LoggerFactory.getLogger(DestinationServiceImpl.class);

    @Override
    public DestinationModel createDestination(String token, DestinationModel destinationModel) throws DestinationExistException, UnauthorizedAccessException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Unauthorized access to create destination");
        }
        String role = tokenValidationService.getRole(token);
        if (!"admin".equalsIgnoreCase(role) && !"subadmin".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("User role is not authorized to create destination");
        }
        logger.info("Creating destination: {}", destinationModel.name());
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
        indexDestination(DestinationMapper.toModel(saved));
        return DestinationMapper.toModel(saved);
    }

    @Override
    public DestinationModel getDestinationById(String token, Long id) throws DestinationNotFoundException, UnauthorizedAccessException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Unauthorized access to destination");
        }
        logger.info("Fetching destination by id: {}", id);
        return destinationRepository.findById(id).map(DestinationMapper::toModel).orElseThrow(() -> new DestinationNotFoundException(id));
    }

    @Override
    public List<DestinationModel> getAllDestinations(String token) throws UnauthorizedAccessException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Unauthorized access to destinations");
        }
        logger.info("Fetching all destinations");
        return destinationRepository.findAll().stream().map(DestinationMapper::toModel).toList();
    }

    @Override
    public DestinationModel updateDestination(String token, DestinationModel model) throws DestinationNotFoundException, UnauthorizedAccessException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Unauthorized access to update destination");
        }
        String role = tokenValidationService.getRole(token);
        if (role != null && !"user".equalsIgnoreCase(role)) {
            logger.info("Updating destination id: {}", model.id());
            Optional<Destination> existingDestination = destinationRepository.findById(model.id());
            if (existingDestination.isEmpty()) {
                throw new DestinationNotFoundException(model.id());
            }
            Destination existing = existingDestination.get();
            existing.setName(model.name());
            existing.setDescription(model.description());
            existing.setImageUrl(model.imageUrl());
            existing.setRegion(regionRepository.findById(existing.getRegion().getId()).get());
            Destination saved = destinationRepository.save(existing);
            indexDestination(DestinationMapper.toModel(saved));
            return DestinationMapper.toModel(saved);
        } else {
            throw new UnauthorizedAccessException("Not authorized to update destination");
        }
    }

    @Override
    public List<DestinationModel> getDestinationsByRegionId(String token, Long regionId) throws RegionNotFoundException, UnauthorizedAccessException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Unauthorized access to destinations by region");
        }
        logger.info("Fetching destinations by region id: {}", regionId);
        Optional<Region> region = regionRepository.findById(regionId);
        if (region.isPresent()) {
            List<Destination> destinations = destinationRepository.findByRegion(region.get());
            return destinations.stream().map(DestinationMapper::toModel).toList();
        }
        throw new RegionNotFoundException(regionId);
    }

    @Override
    public List<DestinationModel> getDestinationsByCountryId(String token, Long countryId) throws CountryNotFoundException, UnauthorizedAccessException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Unauthorized access to destinations by country");
        }
        logger.info("Fetching destinations by country id: {}", countryId);
        Optional<Country> country = countryRepository.findById(countryId);
        if (country.isPresent()) {
            List<Destination> destinations = destinationRepository.findByRegion_Country(country.get());
            return destinations.stream().map(DestinationMapper::toModel).toList();
        }
        throw new CountryNotFoundException(countryId);
    }

    @Override
    public List<DestinationModel> searchDestinationByName(String token, String name) throws DestinationNotFoundException, UnauthorizedAccessException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Unauthorized access to search destinations");
        }
        logger.info("Searching destinations by name: {}", name);
        List<Destination> destinations = destinationRepository.findByNameContainingIgnoreCase(name);
        if (destinations.isEmpty()) {
            throw new DestinationNotFoundException(name);
        }
        return destinations.stream().map(DestinationMapper::toModel).toList();
    }


    @Override
    public DestinationModel deleteDestination(String token, DestinationModel model) throws DestinationNotFoundException, UnauthorizedAccessException {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Unauthorized access to delete destination");
        }
        String role = tokenValidationService.getRole(token);
        if ("admin".equalsIgnoreCase(role) || "subadmin".equalsIgnoreCase(role)) {
            DestinationModel deleted = destinationRepository.findById(model.id()).map(DestinationMapper::toModel).orElseThrow(() -> new DestinationNotFoundException(model.id()));
            destinationRepository.deleteById(model.id());
            logger.info("Deleting destination id: {}", model.id());
            return deleted;
        } else {
            throw new UnauthorizedAccessException("Not authorized to delete destination");
        }
    }

    @Override
    public void indexDestination(DestinationModel destinationModel) {
        try {
            IndexRequest<DestinationModel> request = IndexRequest.of(i -> i
                    .index(destinationIndex)
                    .id(destinationModel.id() != null ? destinationModel.id().toString() : destinationModel.name())
                    .document(destinationModel)
            );
            elasticsearchClient.index(request);
        } catch (Exception e) {
            logger.error("Failed to index destination in Elasticsearch: {}", e.getMessage());
        }
    }

    @Override
    public List<String> suggestDestinations(String query) {
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index(destinationIndex)
                    .query(q -> q
                            .fuzzy(f -> f
                                    .field("name")
                                    .value(query)
                                    .fuzziness("AUTO")
                            )
                    )
                    .size(10)
            );
            SearchResponse<DestinationModel> response = elasticsearchClient.search(searchRequest, DestinationModel.class);
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .filter(java.util.Objects::nonNull)
                    .map(DestinationModel::name)
                    .toList();
        } catch (Exception e) {
            logger.error("Failed to suggest destinations from Elasticsearch: {}", e.getMessage());
            return List.of();
        }
    }

}
