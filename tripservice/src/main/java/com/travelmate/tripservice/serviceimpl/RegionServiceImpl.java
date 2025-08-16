package com.travelmate.tripservice.serviceimpl;

import com.travelmate.tripservice.entity.Country;
import com.travelmate.tripservice.entity.Region;
import com.travelmate.tripservice.exceptions.RegionNotFoundException;
import com.travelmate.tripservice.mapper.CountryMapper;
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
    private CountryServiceImpl countryService;

    private static final Logger logger = LoggerFactory.getLogger(RegionServiceImpl.class);

    @Override
    public List<RegionModel> getAllRegions() {
        logger.info("Fetching all regions");
        return regionRepository.findAll().stream().map(RegionMapper::toModel).toList();
    }

    @Override
    public RegionModel getRegionById(Long id) throws RegionNotFoundException {
        return regionRepository.findById(id).map(RegionMapper::toModel).orElseThrow(() -> new RegionNotFoundException(id));
    }

    @Override
    public RegionModel addRegion(RegionModel regionModel) throws RuntimeException {
        logger.info("Adding new region: {}", regionModel.name());
        // Check if region with the same name already exists
        List<Region> existingRegions = regionRepository.findByNameContainingIgnoreCase(regionModel.name());
        if (!existingRegions.isEmpty()) {
            logger.warn("Region with name '{}' already exists", regionModel.name());
            throw new RuntimeException("Region with name '" + regionModel.name() + "' already exists");
        }
        Region region = new Region();
        if (regionModel.country().id() == null) {
            CountryModel countryModel = countryService.addCountry(regionModel.country());
            region.setCountry(CountryMapper.toEntity(countryModel));
        }
        region.setName(regionModel.name());
        Region saved = regionRepository.save(region);
        return RegionMapper.toModel(saved);
    }

    @Override
    public RegionModel updateRegion(Long id, RegionModel regionModel) throws RegionNotFoundException {
        Region existing = regionRepository.findById(id).orElseThrow(() -> new RegionNotFoundException(id));
        existing.setName(regionModel.name());
        if (regionModel.country().id() == null) {
            CountryModel countryModel = countryService.addCountry(regionModel.country());
            existing.setCountry(CountryMapper.toEntity(countryModel));
        } else {
            Country existingCountry = CountryMapper.toEntity(regionModel.country());
            existing.setCountry(existingCountry);
        }
        Region saved = regionRepository.save(existing);
        return RegionMapper.toModel(saved);
    }

    @Override
    public RegionModel deleteRegion(Long id) throws RegionNotFoundException {
        logger.info("Deleting region with id: {}", id);
        Region existing = regionRepository.findById(id).orElseThrow(() -> new RegionNotFoundException(id));

        regionRepository.deleteById(id);
        return RegionMapper.toModel(existing);
    }
}
