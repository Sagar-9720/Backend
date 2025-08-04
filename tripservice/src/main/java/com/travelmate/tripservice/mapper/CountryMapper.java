package com.travelmate.tripservice.mapper;

import com.travelmate.tripservice.domain.Country;
import com.travelmate.tripservice.model.CountryModel;

public class CountryMapper {
    public static CountryModel toModel(Country country) {
        if (country == null) return null;
        return CountryModel.builder()
                .id(country.getId())
                .name(country.getName())
                .build();
    }

    public static Country toEntity(CountryModel model) {
        if (model == null) return null;
        return Country.builder()
                .id(model.getId())
                .name(model.getName())
                .build();
    }
}

