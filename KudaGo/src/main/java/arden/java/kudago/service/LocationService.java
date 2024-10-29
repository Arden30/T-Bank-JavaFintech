package arden.java.kudago.service;

import arden.java.kudago.dto.response.places.LocationDto;

import java.util.List;

public interface LocationService {
    List<LocationDto> getAllLocations();

    LocationDto getLocationById(Long id);

    LocationDto createLocation(LocationDto LocationDto);

    LocationDto updateLocation(Long id, LocationDto locationDto);

    void deleteLocation(Long id);
}
