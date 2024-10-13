package arden.java.kudago.service;

import arden.java.kudago.dto.Location;
import arden.java.kudago.exception.CreationObjectException;
import arden.java.kudago.exception.IdNotFoundException;
import arden.java.kudago.repository.StorageRepository;
import arden.java.kudago.service.impl.LocationServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {
    @Mock
    private StorageRepository<String, Location> storage;
    @InjectMocks
    private LocationServiceImpl locationService;

    private final List<Location> locationsList = List.of(
            new Location("shop", "Магазин здорового питания"),
            new Location("cafe", "Кафе быстрого питания")
    );

    @Test
    @DisplayName("Getting all locations: success test")
    public void getAllLocations_successTest() {
        //Arrange
        when(storage.readAll()).thenReturn(locationsList);

        //Act
        List<Location> locations = locationService.getAllLocations();

        //Assert
        assertThat(locations).isEqualTo(locationsList);
    }

    @Test
    @DisplayName("Getting all locations: fail test")
    public void getAllLocations_failTest() {
        when(storage.readAll()).thenReturn(Collections.emptyList());

        List<Location> locations = locationService.getAllLocations();

        assertThat(Collections.emptyList()).isEqualTo(locations);
    }

    @Test
    @DisplayName("Getting Location by id: success test")
    public void getLocationBySlug_successTest() {
        when(storage.read("shop")).thenReturn(locationsList.getFirst());

        Location Location = locationService.getLocationBySlug("shop");

        assertThat(Location).isEqualTo(locationsList.getFirst());
    }

    @Test
    @DisplayName("Getting Location by id: fail test")
    public void getLocationBySlug_failTest() {
        when(storage.read("shop")).thenReturn(null);

        assertThrows(IdNotFoundException.class, () -> locationService.getLocationBySlug("shop"));
    }

    @Test
    @DisplayName("Create new Location: success test")
    public void createLocation_successTest() {
        when(storage.create("shop", locationsList.getFirst())).thenReturn(locationsList.getFirst());

        Location Location = locationService.createLocation(locationsList.getFirst());

        assertThat(Location).isEqualTo(locationsList.getFirst());
    }

    @Test
    @DisplayName("Create new Location: fail test")
    public void createLocation_failTest() {
        when(storage.create("shop", locationsList.getFirst())).thenReturn(null);

        assertThrows(CreationObjectException.class, () -> locationService.createLocation(locationsList.getFirst()));
    }

    @Test
    @DisplayName("Update Location: success test")
    public void updateLocation_successTest() {
        when(storage.update("shop", locationsList.getLast())).thenReturn(locationsList.getLast());
        when(storage.read("shop")).thenReturn(locationsList.getFirst());

        Location Location = locationService.updateLocation("shop", locationsList.getLast());

        assertThat(Location).isEqualTo(locationsList.getLast());
    }

    @Test
    @DisplayName("Update Location: fail test")
    public void updateLocation_failTest() {
        assertThrows(IdNotFoundException.class, () -> locationService.updateLocation("shop", locationsList.getLast()));
    }

    @Test
    @DisplayName("Delete Location: success test")
    public void deleteLocation_successTest() {
        when(storage.read("shop")).thenReturn(locationsList.getFirst());
        when(storage.delete("shop")).thenReturn(true);

        boolean isDeleted = locationService.deleteLocation("shop");

        assertThat(isDeleted).isTrue();
    }

    @Test
    @DisplayName("Delete Location: fail test")
    public void deleteLocation_failTest() {
        assertThrows(IdNotFoundException.class, () -> locationService.deleteLocation(locationsList.getFirst().slug()));
    }
}
