package arden.java.kudago.controller;

import arden.java.kudago.dto.response.places.Location;
import arden.java.kudago.service.LocationService;
import configuration.annotation.logtimexec.LogTimeExec;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@LogTimeExec
public class LocationController {
    private final LocationService locationService;

    @GetMapping
    public ResponseEntity<List<Location>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<Location> getLocation(@PathVariable String slug) {
        return ResponseEntity.ok(locationService.getLocationBySlug(slug));
    }

    @PostMapping
    public ResponseEntity<Location> createLocation(@RequestBody Location location) {
        return ResponseEntity.ok(locationService.createLocation(location));
    }

    @PutMapping("/{slug}")
    public ResponseEntity<Location> updateCategory(@PathVariable String slug, @RequestBody Location location) {
        return ResponseEntity.ok(locationService.updateLocation(slug, location));
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<Boolean> deleteLocation(@PathVariable String slug) {
        return ResponseEntity.ok(locationService.deleteLocation(slug));
    }
}
