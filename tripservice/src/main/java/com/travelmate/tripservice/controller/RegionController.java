package com.travelmate.tripservice.controller;

import com.travelmate.tripservice.domain.Region;
import com.travelmate.tripservice.model.RegionModel;
import com.travelmate.tripservice.response.CustomResponseEntity;
import com.travelmate.tripservice.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/regions")
public class RegionController {
    @Autowired
    private RegionService regionService;

    @GetMapping
    public ResponseEntity<CustomResponseEntity<List<RegionModel>>> getAllRegions() {
        List<RegionModel> regions = regionService.getAllRegions();
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Regions fetched", regions, "/regions"));
    }
}
