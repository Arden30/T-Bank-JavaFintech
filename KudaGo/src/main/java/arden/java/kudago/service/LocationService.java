package arden.java.kudago.service;

import arden.java.kudago.dto.response.places.LocationResponse;

import java.util.List;

public interface LocationService {
    List<LocationResponse> getAllLocations();

    LocationResponse getLocationById(Long id);

    LocationResponse createLocation(LocationResponse LocationResponse);

    LocationResponse updateLocation(Long id, LocationResponse locationResponse);

    void deleteLocation(Long id);
}
