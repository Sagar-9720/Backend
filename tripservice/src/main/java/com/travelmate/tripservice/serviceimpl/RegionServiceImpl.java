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
}
