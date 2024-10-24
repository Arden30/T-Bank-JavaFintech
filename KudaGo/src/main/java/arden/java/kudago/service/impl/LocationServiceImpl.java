package arden.java.kudago.service.impl;

import arden.java.kudago.dto.response.places.LocationDto;
import arden.java.kudago.exception.IdNotFoundException;
import arden.java.kudago.model.Location;
import arden.java.kudago.repository.LocationRepository;
import arden.java.kudago.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    @Override
    public List<LocationDto> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(this::createResponseFromLocation)
                .toList();
    }

    @Override
    public LocationDto getLocationById(Long id) {
        return createResponseFromLocation(locationRepository.findByIdEager(id)
                .orElseThrow(() -> new IdNotFoundException("Location with id '" + id + "' not found")));
    }

    @Override
    public LocationDto createLocation(LocationDto locationDto) {
        return createResponseFromLocation(locationRepository.save(createLocationFromResponse(locationDto)));
    }

    @Override
    public LocationDto updateLocation(Long id, LocationDto locationDto) {
        Location existingLocation = locationRepository.findByIdEager(id)
                .orElseThrow(() -> new IdNotFoundException("Location with id '" + id + "' not found"));

        existingLocation.setName(locationDto.name());
        existingLocation.setSlug(locationDto.slug());

        return createResponseFromLocation(locationRepository.save(existingLocation));
    }

    @Override
    public void deleteLocation(Long id) {
        locationRepository.deleteById(id);
    }

    private LocationDto createResponseFromLocation(Location location) {
        return new LocationDto(location.getSlug(), location.getName());
    }

    private Location createLocationFromResponse(LocationDto locationDto) {
        Location location = new Location();
        location.setName(locationDto.name());
        location.setSlug(locationDto.slug());

        return location;
    }
}
