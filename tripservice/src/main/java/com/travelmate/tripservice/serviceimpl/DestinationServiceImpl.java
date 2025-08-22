package com.travelmate.tripservice.serviceimpl;

import com.travelmate.tripservice.entity.Destination;
import com.travelmate.tripservice.exceptions.*;
import com.travelmate.tripservice.mapper.CountryMapper;
import com.travelmate.tripservice.mapper.DestinationMapper;
import com.travelmate.tripservice.mapper.RegionMapper;
import com.travelmate.tripservice.model.CountryModel;
import com.travelmate.tripservice.model.DestinationModel;
import com.travelmate.tripservice.model.RegionModel;
import com.travelmate.tripservice.repository.DestinationRepository;
import com.travelmate.tripservice.service.DestinationService;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private CountryServiceImpl countryService;

    @Autowired
    private RegionServiceImpl regionService;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Value("${elasticsearch.index.destinations:destinations}")
    private String destinationIndex;

    private static final Logger logger = LoggerFactory.getLogger(DestinationServiceImpl.class);

    @Override
    @CacheEvict(value = {"destinations", "allDestinations", "destinationsByRegion", "destinationsByCountry"}, allEntries = true)
    public DestinationModel createDestination(String role, DestinationModel destinationModel) throws DestinationExistException, UnauthorizedAccessException {

        if (!"admin".equalsIgnoreCase(role) && !"subadmin".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("Role is not authorized to create destination");
        }
        logger.info("Creating destination: {}", destinationModel.name());
        List<Destination> existing = destinationRepository.findByNameContainingIgnoreCase(destinationModel.name());
        if (!existing.isEmpty()) {
            throw new DestinationExistException(destinationModel.name());
        }
        Destination destination = DestinationMapper.toEntity(destinationModel);
        if (destinationModel.region().id() == null) {
            destination.setRegion(RegionMapper.toEntity(regionService.addRegion(destinationModel.region())));
        }
        Destination saved = destinationRepository.save(destination);
        indexDestination(DestinationMapper.toModel(saved));
        return DestinationMapper.toModel(saved);
    }

    @Override
    @Cacheable(value = "destinations", key = "#id")
    public DestinationModel getDestinationById(Long id) throws DestinationNotFoundException {
        logger.info("Cache miss: Fetching destination by id: {} from database", id);
        return destinationRepository.findById(id).map(DestinationMapper::toModel).orElseThrow(() -> new DestinationNotFoundException(id));
    }

    @Override
    @Cacheable(value = "allDestinations")
    public List<DestinationModel> getAllDestinations() {
        logger.info("Cache miss: Fetching all destinations from database");
        return destinationRepository.findAll().stream().map(DestinationMapper::toModel).toList();
    }

    @Override
    @CacheEvict(value = {"destinations", "allDestinations", "destinationsByRegion", "destinationsByCountry"}, allEntries = true)
    public DestinationModel updateDestination(String role, DestinationModel model) throws DestinationNotFoundException, UnauthorizedAccessException {

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
            if (model.region() != null) {
                if (model.region().id() == null) {
                    existing.setRegion(RegionMapper.toEntity(regionService.addRegion(model.region())));
                } else {
                    existing.setRegion(RegionMapper.toEntity(regionService.getRegionById(model.region().id())));
                }
            }
            Destination saved = destinationRepository.save(existing);
            indexDestination(DestinationMapper.toModel(saved));
            return DestinationMapper.toModel(saved);
        } else {
            throw new UnauthorizedAccessException("Not authorized to update destination");
        }
    }

    @Override
    @Cacheable(value = "destinationsByRegion", key = "#regionId")
    public List<DestinationModel> getDestinationsByRegionId(Long regionId) throws RegionNotFoundException {

        logger.info("Cache miss: Fetching destinations by region id: {}", regionId);
        RegionModel regionModel = regionService.getRegionById(regionId);
        if (regionModel == null) {
            throw new RegionNotFoundException(regionId);
        }
        return destinationRepository.findByRegion(RegionMapper.toEntity(regionModel)).stream().map(DestinationMapper::toModel).toList();
    }

    @Override
    @Cacheable(value = "destinationsByCountry", key = "#countryId")
    public List<DestinationModel> getDestinationsByCountryId(Long countryId) throws CountryNotFoundException, UnauthorizedAccessException {
        logger.info("Cache miss: Fetching destinations by country id: {}", countryId);
        CountryModel countryModel = countryService.getCountryById(countryId);
        if (countryModel == null) {
            throw new CountryNotFoundException(countryId);
        }
        return destinationRepository.findByRegion_Country(CountryMapper.toEntity(countryModel)).stream().map(DestinationMapper::toModel).toList();
    }

    @Override
    public List<DestinationModel> searchDestinationByName(String name) throws DestinationNotFoundException, UnauthorizedAccessException {
        logger.info("Searching destinations by name: {}", name);
        List<Destination> destinations = destinationRepository.findByNameContainingIgnoreCase(name);
        if (destinations.isEmpty()) {
            throw new DestinationNotFoundException(name);
        }
        return destinations.stream().map(DestinationMapper::toModel).toList();
    }


    @Override
    public void indexDestination(DestinationModel destinationModel) {
        try {
            IndexRequest<DestinationModel> request = IndexRequest.of(i -> i.index(destinationIndex).id(destinationModel.id() != null ? destinationModel.id().toString() : destinationModel.name()).document(destinationModel));
            elasticsearchClient.index(request);
        } catch (Exception e) {
            logger.error("Failed to index destination in Elasticsearch: {}", e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "destinationSuggestions", key = "#query")
    public List<String> suggestDestinations(String query) {
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s.index(destinationIndex).query(q -> q.fuzzy(f -> f.field("name").value(query).fuzziness("AUTO"))).size(10));
            SearchResponse<DestinationModel> response = elasticsearchClient.search(searchRequest, DestinationModel.class);
            return response.hits().hits().stream().map(Hit::source).filter(java.util.Objects::nonNull).map(DestinationModel::name).toList();
        } catch (Exception e) {
            logger.error("Failed to suggest destinations from Elasticsearch: {}", e.getMessage());
            return List.of();
        }
    }

}
