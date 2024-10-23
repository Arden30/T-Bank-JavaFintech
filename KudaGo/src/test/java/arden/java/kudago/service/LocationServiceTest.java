package arden.java.kudago.service;

import arden.java.kudago.dto.response.places.LocationResponse;
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
            Optional.of(new Location(1L, "Магазин здорового питания", "shop", null)),
            Optional.of(new Location(2L, "Кафе быстрого питания", "cafe", null))
    );

    private final List<LocationResponse> locationsList = List.of(
            new LocationResponse("shop", "Магазин здорового питания"),
            new LocationResponse("cafe", "Кафе быстрого питания")
    );

    @Test
    @DisplayName("Getting all locations: success test")
    public void getAllLocations_successTest() {
        //Arrange
        when(locationRepository.findAll()).thenReturn(locations.stream().map(location -> location.get()).toList());

        //Act
        List<LocationResponse> locationResponses = locationService.getAllLocations();

        //Assert
        assertThat(locationResponses).isEqualTo(locationsList);
    }

    @Test
    @DisplayName("Getting all locations: fail test")
    public void getAllLocations_failTest() {
        when(locationRepository.findAll()).thenReturn(Collections.emptyList());

        List<LocationResponse> locationResponses = locationService.getAllLocations();

        assertThat(Collections.emptyList()).isEqualTo(locationResponses);
    }

    @Test
    @DisplayName("Getting Location by id: success test")
    public void getLocationBySlug_successTest() {
        when(locationRepository.findById(1L)).thenReturn(locations.getFirst());

        LocationResponse LocationResponse = locationService.getLocationById(1L);

        assertThat(LocationResponse).isEqualTo(locationsList.getFirst());
    }

    @Test
    @DisplayName("Getting Location by id: fail test")
    public void getLocationBySlug_failTest() {
        when(locationRepository.findById(1L)).thenThrow(new IdNotFoundException("id not found"));

        assertThrows(IdNotFoundException.class, () -> locationService.getLocationById(1L));
    }

    @Test
    @DisplayName("Create new Location: success test")
    public void createLocation_successTest() {
        when(locationRepository.save(any(Location.class))).thenReturn(locations.getFirst().get());

        LocationResponse LocationResponse = locationService.createLocation(locationsList.getFirst());

        assertThat(LocationResponse).isEqualTo(locationsList.getFirst());
    }

    @Test
    @DisplayName("Update Location: success test")
    public void updateLocation_successTest() {
        when(locationRepository.save(any(Location.class))).thenReturn(locations.getLast().get());
        when(locationRepository.findById(1L)).thenReturn(locations.getFirst());

        LocationResponse LocationResponse = locationService.updateLocation(1L, locationsList.getLast());

        assertThat(LocationResponse).isEqualTo(locationsList.getLast());
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
}
