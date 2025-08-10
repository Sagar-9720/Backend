package com.travelmate.tripservice.controller;

import com.travelmate.tripservice.model.RegionModel;
import com.travelmate.tripservice.response.CustomResponseEntity;
import com.travelmate.tripservice.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/trip/regions")
public class RegionController {
    @Autowired
    private RegionService regionService;

    @GetMapping
    public ResponseEntity<CustomResponseEntity<List<RegionModel>>> getAllRegions() {
        List<RegionModel> regions = regionService.getAllRegions();
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Regions fetched", regions, "/regions"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<RegionModel>> getRegionById(@PathVariable Long id) {
        RegionModel region = regionService.getRegionById(id);
        if (region == null) {
            return ResponseEntity.status(404).body(CustomResponseEntity.error(404, "Region not found", "/regions/" + id));
        }
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Region fetched", region, "/regions/" + id));
    }

    @PostMapping
    public ResponseEntity<CustomResponseEntity<RegionModel>> addRegion(@RequestBody RegionModel regionModel) {
        RegionModel created = regionService.addRegion(regionModel);
        return ResponseEntity.status(201).body(CustomResponseEntity.success(201, "Region created", created, "/regions"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<RegionModel>> updateRegion(@PathVariable Long id, @RequestBody RegionModel regionModel) {
        RegionModel updated = regionService.updateRegion(id, regionModel);
        if (updated == null) {
            return ResponseEntity.status(404).body(CustomResponseEntity.error(404, "Region not found", "/regions/" + id));
        }
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Region updated", updated, "/regions/" + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<Void>> deleteRegion(@PathVariable Long id) {
        regionService.deleteRegion(id);
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Region deleted", null, "/regions/" + id));
    }
}
