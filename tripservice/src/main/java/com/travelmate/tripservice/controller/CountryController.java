package com.travelmate.tripservice.controller;

import com.travelmate.tripservice.domain.Country;
import com.travelmate.tripservice.model.CountryModel;
import com.travelmate.tripservice.response.CustomResponseEntity;
import com.travelmate.tripservice.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/countries")
public class CountryController {
    @Autowired
    private CountryService countryService;

    @GetMapping
    public ResponseEntity<CustomResponseEntity<List<CountryModel>>> getAllCountries() {
        List<CountryModel> countries = countryService.getAllCountries();
        return ResponseEntity.ok(CustomResponseEntity.success(200, "Countries fetched", countries, "/countries"));
    }
}
