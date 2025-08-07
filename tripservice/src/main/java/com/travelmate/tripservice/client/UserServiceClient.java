package com.travelmate.tripservice.client;

import com.travelmate.tripservice.config.FeignClientConfig;
import com.travelmate.tripservice.dto.TripInteractionDTO;
import com.travelmate.tripservice.dto.UserDetailsDTO;
import com.travelmate.tripservice.dto.UserPreferencesDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", configuration = FeignClientConfig.class)
public interface UserServiceClient {
    @PostMapping("/api/interactions/trip")
    void saveInteraction(@RequestBody TripInteractionDTO interaction,
                        @RequestHeader("Authorization") String token);

    @GetMapping("/api/users/{userId}/interactions/trip/{tripId}")
    TripInteractionDTO[] getUserTripInteractions(@PathVariable("userId") String userId,
                                               @PathVariable("tripId") String tripId,
                                               @RequestHeader("Authorization") String token);

    @GetMapping("/api/users/{userId}/saved-trips")
    String[] getSavedTrips(@PathVariable("userId") String userId,
                          @RequestHeader("Authorization") String token);

    @GetMapping("/api/users/{userId}/preferences")
    UserPreferencesDTO getUserPreferences(@PathVariable("userId") String userId,
                                        @RequestHeader("Authorization") String token);

    @GetMapping("/api/users/{userId}")
    UserDetailsDTO getUserDetails(@PathVariable("userId") String userId,
                                @RequestHeader("Authorization") String token);

    @GetMapping("/api/users/{userId}/journals")
    String[] getUserJournals(@PathVariable("userId") String userId,
                           @RequestHeader("Authorization") String token);
}
