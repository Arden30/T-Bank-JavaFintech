package arden.java.kudago.controller;

import arden.java.kudago.dto.response.places.LocationDto;
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
    public ResponseEntity<List<LocationDto>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDto> getLocation(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getLocationById(id));
    }

    @PostMapping
    public ResponseEntity<LocationDto> createLocation(@RequestBody LocationDto locationDto) {
        return ResponseEntity.ok(locationService.createLocation(locationDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationDto> updateLocation(@PathVariable Long id, @RequestBody LocationDto locationDto) {
        return ResponseEntity.ok(locationService.updateLocation(id, locationDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.ok(true);
    }
}
