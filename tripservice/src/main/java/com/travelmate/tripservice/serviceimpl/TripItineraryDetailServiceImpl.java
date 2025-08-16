package com.travelmate.tripservice.serviceimpl;

import com.travelmate.tripservice.entity.TripItineraryDetail;
import com.travelmate.tripservice.exceptions.UnauthorizedAccessException;
import com.travelmate.tripservice.mapper.TripItineraryDetailMapper;
import com.travelmate.tripservice.model.TripItineraryDetailModel;
import com.travelmate.tripservice.repository.TripItineraryDetailRepository;
import com.travelmate.tripservice.service.ItineraryService;
import com.travelmate.tripservice.service.TokenValidationService;
import com.travelmate.tripservice.service.TripItineraryDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripItineraryDetailServiceImpl implements TripItineraryDetailService {


    @Autowired
    private TripItineraryDetailRepository repository;

    @Autowired
    private TokenValidationService tokenValidationService;

    @Autowired
    private ItineraryServiceImpl itineraryService;

    private ItineraryActivityServiceImpl itineraryActivityService;


    @Override
    public TripItineraryDetailModel create(String token, TripItineraryDetailModel model) {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Unauthorized access to create TripItineraryDetail");
        }
        TripItineraryDetail entity = TripItineraryDetailMapper.toEntity(model);
        return TripItineraryDetailMapper.toModel(repository.save(entity));
    }

    @Override
    public TripItineraryDetailModel update(String token, Long id, TripItineraryDetailModel model) {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Unauthorized access to update TripItineraryDetail");
        }
        TripItineraryDetail entity = repository.findById(id).orElseThrow(() -> new RuntimeException("TripItineraryDetail not found"));

        entity.setDayNumber(model.dayNumber());
        entity.setArrivalTime(model.arrivalTime());
        entity.setDepartureTime(model.departureTime());

        return TripItineraryDetailMapper.toModel(repository.save(entity));
    }

    @Override
    public void delete(String token, Long id) {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Unauthorized access to delete TripItineraryDetail");
        }

        repository.deleteById(id);
    }

    @Override
    public TripItineraryDetailModel getById(String token, Long id) {
        return repository.findById(id).map(TripItineraryDetailMapper::toModel).orElseThrow();
    }

    @Override
    public List<TripItineraryDetailModel> getAll(String token) {
        if (!tokenValidationService.isTokenValid(token)) {
            throw new UnauthorizedAccessException("Unauthorized access to get all TripItineraryDetails");
        }
        return repository.findAll().stream().map(TripItineraryDetailMapper::toModel).toList();
    }
}
