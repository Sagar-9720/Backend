package com.travelmate.tripservice.controller;

import com.travelmate.tripservice.model.CountryModel;
import com.travelmate.tripservice.response.CustomResponseEntity;
import com.travelmate.tripservice.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trip/countries")
public class CountryController {

    @Autowired
    private CountryService countryService;

    @GetMapping
    public ResponseEntity<CustomResponseEntity<List<CountryModel>>> getAllCountries() {
        List<CountryModel> countries = countryService.getAllCountries();
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Countries fetched", countries, "/countries"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<CountryModel>> getCountryById(@PathVariable Long id) {
        CountryModel country = countryService.getCountryById(id);
        if (country == null) {
            return ResponseEntity.status(404).body(CustomResponseEntity.error(404, "Country not found", "/countries/" + id));
        }
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Country fetched", country, "/countries/" + id));
    }

    @PostMapping
    public ResponseEntity<CustomResponseEntity<CountryModel>> addCountry(@RequestBody CountryModel countryModel) throws Exception {
        CountryModel created = countryService.addCountry(countryModel);
        return ResponseEntity.status(201).body(CustomResponseEntity.success(201, "Country created", created, "/countries"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomResponseEntity<CountryModel>> updateCountry(@PathVariable Long id, @RequestBody CountryModel countryModel) {
        CountryModel updated = countryService.updateCountry(id, countryModel);
        if (updated == null) {
            return ResponseEntity.status(404).body(CustomResponseEntity.error(404, "Country not found", "/countries/" + id));
        }
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Country updated", updated, "/countries/" + id));
    }
}
