package arden.java.kudago.service;

import arden.java.kudago.dto.response.places.LocationDto;
import arden.java.kudago.exception.IdNotFoundException;
import arden.java.kudago.model.Location;
import arden.java.kudago.repository.LocationRepository;
import arden.java.kudago.service.impl.LocationServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {
    @Mock
    private LocationRepository locationRepository;
    @InjectMocks
    private LocationServiceImpl locationService;

    private final List<Optional<Location>> locations = List.of(
            Optional.of(new Location(1L, "Магазин здорового питания", "shop", new HashSet<>())),
            Optional.of(new Location(2L, "Кафе быстрого питания", "cafe", new HashSet<>()))
    );

    private final List<LocationDto> locationsList = List.of(
            new LocationDto("shop", "Магазин здорового питания"),
            new LocationDto("cafe", "Кафе быстрого питания")
    );

    @Test
    @DisplayName("Getting all locations: success test")
    public void getAllLocations_successTest() {
        //Arrange
        when(locationRepository.findAll()).thenReturn(locations.stream().map(Optional::get).toList());

        //Act
        List<LocationDto> locationResponse = locationService.getAllLocations();

        //Assert
        assertThat(locationResponse).isEqualTo(locationsList);
    }

    @Test
    @DisplayName("Getting all locations: fail test")
    public void getAllLocations_failTest() {
        when(locationRepository.findAll()).thenReturn(Collections.emptyList());

        List<LocationDto> locationResponse = locationService.getAllLocations();

        assertThat(Collections.emptyList()).isEqualTo(locationResponse);
    }

    @Test
    @DisplayName("Getting Location by id: success test")
    public void getLocationBySlug_successTest() {
        when(locationRepository.findByIdEager(1L)).thenReturn(locations.getFirst());

        LocationDto LocationDto = locationService.getLocationById(1L);

        assertThat(LocationDto).isEqualTo(locationsList.getFirst());
    }

    @Test
    @DisplayName("Getting Location by id: fail test")
    public void getLocationBySlug_failTest() {
        when(locationRepository.findByIdEager(1L)).thenThrow(new IdNotFoundException("id not found"));

        assertThrows(IdNotFoundException.class, () -> locationService.getLocationById(1L));
    }

    @Test
    @DisplayName("Create new Location: success test")
    public void createLocation_successTest() {
        when(locationRepository.save(any(Location.class))).thenReturn(locations.getFirst().get());

        LocationDto LocationDto = locationService.createLocation(locationsList.getFirst());

        assertThat(LocationDto).isEqualTo(locationsList.getFirst());
    }

    @Test
    @DisplayName("Update Location: success test")
    public void updateLocation_successTest() {
        when(locationRepository.save(any(Location.class))).thenReturn(locations.getLast().get());
        when(locationRepository.findByIdEager(1L)).thenReturn(locations.getFirst());

        LocationDto LocationDto = locationService.updateLocation(1L, locationsList.getLast());

        assertThat(LocationDto).isEqualTo(locationsList.getLast());
    }

    @Test
    @DisplayName("Update Location: fail test")
    public void updateLocation_failTest() {
        assertThrows(IdNotFoundException.class, () -> locationService.updateLocation(2L, locationsList.getLast()));
    }

    @Test
    @DisplayName("Delete Location: success test")
    public void deleteLocation_successTest() {
        assertDoesNotThrow(() -> locationService.deleteLocation(locations.getFirst().get().getId()));
    }

    @Test
    @DisplayName("Location history test")
    public void locationHistoryTest() {
        when(locationRepository.save(any(Location.class))).thenReturn(locations.getFirst().get());
        when(locationRepository.findByIdEager(1L)).thenReturn(locations.getFirst());

        locationService.createLocation(locationsList.getFirst());
        locationService.updateLocation(1L, locationsList.getLast());

        Location orig = locationService.restoreLocationSnapshot(1L, 1);
        assertThat(orig.getName()).isEqualTo(locations.getFirst().get().getName());
    }
}
