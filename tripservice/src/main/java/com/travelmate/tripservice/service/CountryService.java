package com.travelmate.tripservice.service;

import com.travelmate.tripservice.model.CountryModel;

import java.util.List;

public interface CountryService {

    List<CountryModel> getAllCountries();

    CountryModel getCountryById(Long id);
    CountryModel addCountry(CountryModel countryModel);
    CountryModel updateCountry(Long id, CountryModel countryModel);
    void deleteCountry(Long id);

}
