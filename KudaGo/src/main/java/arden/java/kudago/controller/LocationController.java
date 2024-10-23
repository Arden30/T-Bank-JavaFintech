package arden.java.kudago.controller;

import arden.java.kudago.dto.response.places.LocationResponse;
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
    public ResponseEntity<List<LocationResponse>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationResponse> getLocation(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getLocationById(id));
    }

    @PostMapping
    public ResponseEntity<LocationResponse> createLocation(@RequestBody LocationResponse locationResponse) {
        return ResponseEntity.ok(locationService.createLocation(locationResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationResponse> updateCategory(@PathVariable Long id, @RequestBody LocationResponse locationResponse) {
        return ResponseEntity.ok(locationService.updateLocation(id, locationResponse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.ok(true);
    }
}
