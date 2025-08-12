package com.travelmate.tripservice.service;

import com.travelmate.tripservice.exceptions.RegionNotFoundException;
import com.travelmate.tripservice.model.RegionModel;

import java.util.List;

public interface RegionService {

    List<RegionModel> getAllRegions();

    RegionModel getRegionById(Long id) throws RegionNotFoundException;

    RegionModel addRegion(RegionModel regionModel) throws RuntimeException;

    RegionModel updateRegion(Long id, RegionModel regionModel) throws RegionNotFoundException;

    RegionModel deleteRegion(Long id) throws RegionNotFoundException;
}
