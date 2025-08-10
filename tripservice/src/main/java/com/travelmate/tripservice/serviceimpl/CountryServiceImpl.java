package com.travelmate.tripservice.serviceimpl;

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
    public CountryModel getCountryById(Long id) {
        return countryRepository.findById(id)
                .map(CountryMapper::toModel)
                .orElse(null);
    }

    @Override
    public CountryModel addCountry(CountryModel countryModel) {
        var entity = CountryMapper.toEntity(countryModel);
        var saved = countryRepository.save(entity);
        return CountryMapper.toModel(saved);
    }

    @Override
    public CountryModel updateCountry(Long id, CountryModel countryModel) {
        var existing = countryRepository.findById(id).orElse(null);
        if (existing == null) return null;
        existing.setName(countryModel.getName());
        var saved = countryRepository.save(existing);
        return CountryMapper.toModel(saved);
    }

    @Override
    public void deleteCountry(Long id) {
        countryRepository.deleteById(id);
    }
}
