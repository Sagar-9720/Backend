package com.travelmate.tripservice.serviceimpl;

import com.travelmate.tripservice.model.RegionModel;
import com.travelmate.tripservice.mapper.RegionMapper;
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

    private static final Logger logger = LoggerFactory.getLogger(RegionServiceImpl.class);

    @Override
    public List<RegionModel> getAllRegions() {
        logger.info("Fetching all regions");
        return regionRepository.findAll().stream().map(RegionMapper::toModel).toList();
    }

    @Override
    public RegionModel getRegionById(Long id) {
        return regionRepository.findById(id)
                .map(RegionMapper::toModel)
                .orElse(null);
    }

    @Override
    public RegionModel addRegion(RegionModel regionModel) {
        var entity = RegionMapper.toEntity(regionModel);
        var saved = regionRepository.save(entity);
        return RegionMapper.toModel(saved);
    }

    @Override
    public RegionModel updateRegion(Long id, RegionModel regionModel) {
        var existing = regionRepository.findById(id).orElse(null);
        if (existing == null) return null;
        existing.setName(regionModel.getName());
        // Update country if provided
        if (regionModel.getCountryId() != null) {
            var country = existing.getCountry();
            if (country == null || !country.getId().equals(regionModel.getCountryId())) {
                // ...fetch and set new country entity as needed...
            }
        }
        var saved = regionRepository.save(existing);
        return RegionMapper.toModel(saved);
    }

    @Override
    public void deleteRegion(Long id) {
        regionRepository.deleteById(id);
    }
}
