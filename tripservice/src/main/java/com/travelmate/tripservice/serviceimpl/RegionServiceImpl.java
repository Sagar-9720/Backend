package com.travelmate.tripservice.serviceimpl;

import com.travelmate.tripservice.entity.Country;
import com.travelmate.tripservice.entity.Region;
import com.travelmate.tripservice.exceptions.RegionNotFoundException;
import com.travelmate.tripservice.model.CountryModel;
import com.travelmate.tripservice.model.RegionModel;
import com.travelmate.tripservice.mapper.RegionMapper;
import com.travelmate.tripservice.repository.CountryRepository;
import com.travelmate.tripservice.repository.RegionRepository;
import com.travelmate.tripservice.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class RegionServiceImpl implements RegionService {


    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CountryServiceImpl countryService;

    private static final Logger logger = LoggerFactory.getLogger(RegionServiceImpl.class);

    @Override
    public List<RegionModel> getAllRegions() {
        List<RegionModel> regions = regionRepository.findAll().stream().map(RegionMapper::toModel).toList();
        logger.info("getAllRegions returns {} regions", regions.size());
        return regions;
    }

    @Override
    public RegionModel getRegionById(Long id) throws RegionNotFoundException {
        return regionRepository.findById(id).map(RegionMapper::toModel).orElseThrow(() -> new RegionNotFoundException(id));
    }

    @Override
    public RegionModel addRegion(RegionModel regionModel) {
        logger.info("Adding new region: {}", regionModel.name());

        // Prevent duplicates
        boolean exists = !regionRepository.findByNameContainingIgnoreCase(regionModel.name()).isEmpty();
        if (exists) {
            logger.warn("Region with name '{}' already exists", regionModel.name());
            throw new RuntimeException("Region with name '" + regionModel.name() + "' already exists");
        }

        Region region = new Region();
        region.setName(regionModel.name());

        if (regionModel.country().id() == null) {
            // Add new country and use it
            CountryModel createdCountry = countryService.addCountry(regionModel.country());
            Country managedCountry = countryRepository.findById(createdCountry.id()).orElseThrow(() -> new RuntimeException("Country not found after creation"));
            region.setCountry(managedCountry);
        } else {
            // Use managed entity directly
            Country managedCountry = countryRepository.findById(regionModel.country().id()).orElseThrow(() -> new RuntimeException("Country not found with id " + regionModel.country().id()));
            region.setCountry(managedCountry);
        }

        Region saved = regionRepository.save(region);
        logger.info("Region '{}' added successfully with id {}", saved.getName(), saved.getId());
        return RegionMapper.toModel(saved);
    }

    @Override
    public RegionModel updateRegion(Long id, RegionModel regionModel) throws RegionNotFoundException {
        Region existing = regionRepository.findById(id).orElseThrow(() -> new RegionNotFoundException(id));

        existing.setName(regionModel.name());

        if (regionModel.country().id() == null) {
            CountryModel createdCountry = countryService.addCountry(regionModel.country());
            Country managedCountry = countryRepository.findById(createdCountry.id()).orElseThrow(() -> new RuntimeException("Country not found after creation"));
            existing.setCountry(managedCountry);
        } else {
            Country managedCountry = countryRepository.findById(regionModel.country().id()).orElseThrow(() -> new RuntimeException("Country not found with id " + regionModel.country().id()));
            existing.setCountry(managedCountry);
        }

        Region saved = regionRepository.save(existing);
        logger.info("Region '{}' updated successfully with id {}", saved.getName(), saved.getId());
        return RegionMapper.toModel(saved);
    }
}
