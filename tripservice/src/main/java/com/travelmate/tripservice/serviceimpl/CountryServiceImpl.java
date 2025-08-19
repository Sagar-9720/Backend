package com.travelmate.tripservice.serviceimpl;

import com.travelmate.tripservice.entity.Country;
import com.travelmate.tripservice.exceptions.CountryNotFoundException;
import com.travelmate.tripservice.repository.CountryRepository;
import com.travelmate.tripservice.service.CountryService;
import com.travelmate.tripservice.model.CountryModel;
import com.travelmate.tripservice.mapper.CountryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class CountryServiceImpl implements CountryService {

    @Autowired
    private CountryRepository countryRepository;

    private static final Logger logger = LoggerFactory.getLogger(CountryServiceImpl.class);

    @Override
    public List<CountryModel> getAllCountries() {
        logger.info("Fetching all countries");
        return countryRepository.findAll().stream().map(CountryMapper::toModel).toList();
    }

    @Override
    public CountryModel addCountry(CountryModel countryModel) throws RuntimeException {
        Country existing = countryRepository.findByName(countryModel.name());
        if (existing != null) {
            logger.warn("Country with name {} already exists", countryModel.name());
            throw new RuntimeException("Country already exists");
        }
        Country country = CountryMapper.toEntity(countryModel);
        Country saved = countryRepository.save(country);
        logger.info("Country {} added successfully", countryModel.name());
        return CountryMapper.toModel(saved);
    }

    @Override
    public CountryModel updateCountry(Long id, CountryModel countryModel) throws CountryNotFoundException {
        var existing = countryRepository.findById(id).orElseThrow(() -> {
            logger.error("Country with id {} not found for update", id);
            return new CountryNotFoundException(id);
        });
        if (existing == null) return null;
        existing.setName(countryModel.name());
        var saved = countryRepository.save(existing);
        return CountryMapper.toModel(saved);
    }

    public CountryModel getCountryById(Long countryId) {
        logger.info("Fetching country by id: {}", countryId);
        return countryRepository.findById(countryId)
                .map(CountryMapper::toModel)
                .orElseThrow(() -> new CountryNotFoundException(countryId));
    }
}
