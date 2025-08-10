package com.travelmate.tripservice.service;

import com.travelmate.tripservice.model.RegionModel;

import java.util.List;

public interface RegionService {

    List<RegionModel> getAllRegions();

    RegionModel getRegionById(Long id);

    RegionModel addRegion(RegionModel regionModel);

    RegionModel updateRegion(Long id, RegionModel regionModel);

    void deleteRegion(Long id);
}
