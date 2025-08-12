package com.travelmate.tripservice.mapper;

import com.travelmate.tripservice.entity.Country;
import com.travelmate.tripservice.model.CountryModel;

public class CountryMapper {
    public static CountryModel toModel(Country country) {
        if (country == null) return null;
        return new CountryModel(
            country.getId(),
            country.getName()
        );
    }

    public static Country toEntity(CountryModel model) {
        if (model == null) return null;
        return Country.builder()
                .id(model.id())
                .name(model.name())
                .build();
    }
}
