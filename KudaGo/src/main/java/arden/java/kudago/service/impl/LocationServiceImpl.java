package arden.java.kudago.service.impl;

import arden.java.kudago.dto.response.places.LocationResponse;
import arden.java.kudago.exception.IdNotFoundException;
import arden.java.kudago.model.Location;
import arden.java.kudago.repository.LocationRepository;
import arden.java.kudago.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    @Override
    public List<LocationResponse> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(this::createResponseFromLocation)
                .toList();
    }

    @Override
    public LocationResponse getLocationById(Long id) {
        return createResponseFromLocation(locationRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Location with id '" + id + "' not found")));
    }

    @Override
    public LocationResponse createLocation(LocationResponse locationResponse) {
        return createResponseFromLocation(locationRepository.save(createLocationFromResponse(locationResponse)));
    }

    @Override
    public LocationResponse updateLocation(Long id, LocationResponse locationResponse) {
        Optional<Location> location = locationRepository.findById(id);
        if (location.isPresent()) {
            return createResponseFromLocation(locationRepository.save(createLocationFromResponse(locationResponse)));
        } else throw new IdNotFoundException("Location with id '" + id + "' not found");
    }

    @Override
    public void deleteLocation(Long id) {
        locationRepository.deleteById(id);
    }

    private LocationResponse createResponseFromLocation(Location location) {
        return new LocationResponse(location.getSlug(), location.getName());
    }

    private Location createLocationFromResponse(LocationResponse locationResponse) {
        Location location = new Location();
        location.setName(locationResponse.name());
        location.setSlug(locationResponse.slug());

        return location;
    }
}
