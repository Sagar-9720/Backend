package com.travelmate.tripservice.service;

import com.travelmate.tripservice.exceptions.CountryNotFoundException;
import com.travelmate.tripservice.model.CountryModel;

import java.util.List;

public interface CountryService {

    List<CountryModel> getAllCountries();

    CountryModel getCountryById(Long id) throws CountryNotFoundException;

    CountryModel addCountry(CountryModel countryModel) throws RuntimeException;

    CountryModel updateCountry(Long id, CountryModel countryModel) throws CountryNotFoundException;

    CountryModel deleteCountry(Long id) throws CountryNotFoundException;

}
