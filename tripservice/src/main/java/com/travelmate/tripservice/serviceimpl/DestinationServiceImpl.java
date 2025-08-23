package com.travelmate.tripservice.serviceimpl;

import com.travelmate.tripservice.entity.Country;
import com.travelmate.tripservice.entity.Destination;
import com.travelmate.tripservice.entity.Region;
import com.travelmate.tripservice.exceptions.*;
import com.travelmate.tripservice.mapper.CountryMapper;
import com.travelmate.tripservice.mapper.DestinationMapper;
import com.travelmate.tripservice.mapper.RegionMapper;
import com.travelmate.tripservice.model.CountryModel;
import com.travelmate.tripservice.model.DestinationModel;
import com.travelmate.tripservice.model.RegionModel;
import com.travelmate.tripservice.repository.CountryRepository;
import com.travelmate.tripservice.repository.DestinationRepository;
import com.travelmate.tripservice.repository.RegionRepository;
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
    private RegionRepository regionRepository;

    @Autowired
    private CountryRepository countryRepository;

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
    public DestinationModel createDestination(String role, DestinationModel destinationModel) {
        if (!"admin".equalsIgnoreCase(role) && !"subadmin".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("Role is not authorized to create destination");
        }

        logger.info("Creating destination: {}", destinationModel.name());
        boolean exists = !destinationRepository.findByNameContainingIgnoreCase(destinationModel.name()).isEmpty();
        if (exists) {
            throw new DestinationExistException(destinationModel.name());
        }

        Destination destination = DestinationMapper.toEntity(destinationModel);

        if (destinationModel.region() != null) {
            if (destinationModel.region().id() == null) {
                // Create new region
                var createdRegion = regionService.addRegion(destinationModel.region());
                Region managedRegion = regionRepository.findById(createdRegion.id()).orElseThrow(() -> new RegionNotFoundException(createdRegion.id()));
                destination.setRegion(managedRegion);
            } else {
                // Attach existing managed region
                Region managedRegion = regionRepository.findById(destinationModel.region().id()).orElseThrow(() -> new RegionNotFoundException(destinationModel.region().id()));
                destination.setRegion(managedRegion);
            }
        }

        Destination saved = destinationRepository.save(destination);
        indexDestination(DestinationMapper.toModel(saved));
        return DestinationMapper.toModel(saved);
    }

    @Override
    @Cacheable(value = "destinations", key = "#id")
    public DestinationModel getDestinationById(Long id) throws DestinationNotFoundException {
        logger.info("Fetching destination by id: {}", id);
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
    public DestinationModel updateDestination(String role, DestinationModel model) {
        if (role == null || "user".equalsIgnoreCase(role)) {
            throw new UnauthorizedAccessException("Not authorized to update destination");
        }

        logger.info("Updating destination id: {}", model.id());
        Destination existing = destinationRepository.findById(model.id()).orElseThrow(() -> new DestinationNotFoundException(model.id()));

        existing.setName(model.name());
        existing.setDescription(model.description());
        existing.setImageUrl(model.imageUrl());

        if (model.region() != null) {
            if (model.region().id() == null) {
                var createdRegion = regionService.addRegion(model.region());
                Region managedRegion = regionRepository.findById(createdRegion.id()).orElseThrow(() -> new RegionNotFoundException(createdRegion.id()));
                existing.setRegion(managedRegion);
            } else {
                Region managedRegion = regionRepository.findById(model.region().id()).orElseThrow(() -> new RegionNotFoundException(model.region().id()));
                existing.setRegion(managedRegion);
            }
        }

        Destination saved = destinationRepository.save(existing);
        indexDestination(DestinationMapper.toModel(saved));
        return DestinationMapper.toModel(saved);
    }

    @Override
    @Cacheable(value = "destinationsByRegion", key = "#regionId")
    public List<DestinationModel> getDestinationsByRegionId(Long regionId) throws RegionNotFoundException {
        logger.info("Cache miss: Fetching destinations by region id: {}", regionId);
        Region region = regionRepository.findById(regionId).orElseThrow(() -> new RegionNotFoundException(regionId));
        return destinationRepository.findByRegion(region).stream().map(DestinationMapper::toModel).toList();
    }

    @Override
    @Cacheable(value = "destinationsByCountry", key = "#countryId")
    public List<DestinationModel> getDestinationsByCountryId(Long countryId) throws CountryNotFoundException, UnauthorizedAccessException {
        logger.info("Cache miss: Fetching destinations by country id: {}", countryId);
        Country country = countryRepository.findById(countryId).orElseThrow(() -> new CountryNotFoundException(countryId));
        return destinationRepository.findByRegion_Country(country).stream().map(DestinationMapper::toModel).toList();
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
    public List<DestinationModel> suggestDestinations(String query) {
        logger.info("Cache miss: Fetching destination suggestions for query: {}", query);
        try {
            SearchRequest searchRequest = SearchRequest.of(s -> s.index(destinationIndex).query(q -> q.bool(b -> b.should(sh -> sh
                    // Match prefixes (starts with)
                    .prefix(p -> p.field("name.keyword").value(query.toLowerCase()))).should(sh -> sh
                    // Match anywhere in the text
                    .wildcard(w -> w.field("name").value("*" + query.toLowerCase() + "*"))).minimumShouldMatch("1"))).size(10));

            SearchResponse<DestinationModel> response = elasticsearchClient.search(searchRequest, DestinationModel.class);
            return response.hits().hits().stream().map(Hit::source).filter(java.util.Objects::nonNull).toList();
        } catch (Exception e) {
            logger.error("Failed to suggest destinations from Elasticsearch: {}", e.getMessage());
            return List.of();
        }
    }

}
