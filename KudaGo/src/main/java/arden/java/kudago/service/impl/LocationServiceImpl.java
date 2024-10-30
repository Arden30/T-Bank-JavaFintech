package arden.java.kudago.service.impl;

import arden.java.kudago.dto.response.places.LocationDto;
import arden.java.kudago.exception.IdNotFoundException;
import arden.java.kudago.model.Category;
import arden.java.kudago.model.Location;
import arden.java.kudago.repository.HistoryCaretaker;
import arden.java.kudago.repository.LocationRepository;
import arden.java.kudago.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final Map<Long, HistoryCaretaker<Location.LocationMemento>> locationHistory = new HashMap<>();

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
        Location location = createLocationFromResponse(locationDto);
        saveLocationSnapshot(location);

        return createResponseFromLocation(locationRepository.save(location));
    }

    @Override
    public LocationDto updateLocation(Long id, LocationDto locationDto) {
        Location existingLocation = locationRepository.findByIdEager(id)
                .orElseThrow(() -> new IdNotFoundException("Location with id '" + id + "' not found"));
        saveLocationSnapshot(existingLocation);

        existingLocation.setName(locationDto.name());
        existingLocation.setSlug(locationDto.slug());

        return createResponseFromLocation(locationRepository.save(existingLocation));
    }

    @Override
    public void deleteLocation(Long id) {
        locationRepository.deleteById(id);
    }

    public LocationDto createResponseFromLocation(Location location) {
        return new LocationDto(location.getSlug(), location.getName());
    }

    public Location createLocationFromResponse(LocationDto locationDto) {
        Location location = new Location();
        location.setName(locationDto.name());
        location.setSlug(locationDto.slug());

        return location;
    }

    private void saveLocationSnapshot(Location location) {
        var memento = location.save();

        locationHistory
                .computeIfAbsent(location.getId(), k -> new HistoryCaretaker<>())
                .save(memento);
    }

    public Location restoreLocationSnapshot(Long locationId, int snapshotIndex) {
        var history = locationHistory.get(locationId);

        if (history == null) {
            throw new IllegalArgumentException("History wasn't found");
        }

        var memento = history.undo(snapshotIndex);
        Location location = locationRepository.findByIdEager(locationId)
                .orElseThrow(() -> new IdNotFoundException("Location with id = " + locationId + " not found"));
        location.restore(memento);

        return locationRepository.save(location);
    }
}
