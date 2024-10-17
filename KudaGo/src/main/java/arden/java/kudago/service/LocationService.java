package arden.java.kudago.service;

import arden.java.kudago.dto.response.places.Location;

import java.util.List;

public interface LocationService {
    List<Location> getAllLocations();

    Location getLocationBySlug(String slug);

    Location createLocation(Location Location);

    Location updateLocation(String slug, Location Location);

    boolean deleteLocation(String slug);
}
