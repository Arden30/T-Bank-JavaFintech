package arden.java.kudago.service.impl;

import arden.java.kudago.dto.Location;
import arden.java.kudago.exception.CreationObjectException;
import arden.java.kudago.exception.IdNotFoundException;
import arden.java.kudago.repository.StorageRepository;
import arden.java.kudago.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationServiceImpl implements LocationService {
    private final StorageRepository<String, Location> locationStorage;

    @Override
    public List<Location> getAllLocations() {
        return locationStorage.readAll();
    }

    @Override
    public Location getLocationBySlug(String slug) {
        if (locationStorage.read(slug) == null) {
            throw new IdNotFoundException("Location with slug '" + slug + "' not found");
        }

        return locationStorage.read(slug);
    }

    @Override
    public Location createLocation(Location location) {
        if (locationStorage.create(location.slug(), location) == null) {
            throw new CreationObjectException("Could not create location, because your input format is wrong, check again");
        }

        return locationStorage.create(location.slug(), location);
    }

    @Override
    public Location updateLocation(String slug, Location location) {
        if (locationStorage.read(slug) == null) {
            throw new IdNotFoundException("Location with slug '" + slug + "' not found");
        }

        if (locationStorage.update(slug, location) == null) {
            throw new CreationObjectException("Could not update location, because your input format is wrong, check again");
        }

        return locationStorage.update(slug, location);
    }

    @Override
    public boolean deleteLocation(String slug) {
        if (locationStorage.read(slug) == null || !locationStorage.delete(slug)) {
            throw new IdNotFoundException("Location with slug '" + slug + "' not found");
        }

        return locationStorage.delete(slug);
    }
}
