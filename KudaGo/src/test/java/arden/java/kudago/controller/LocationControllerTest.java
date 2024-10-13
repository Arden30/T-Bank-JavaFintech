package arden.java.kudago.controller;

import arden.java.kudago.dto.Location;
import arden.java.kudago.exception.CreationObjectException;
import arden.java.kudago.exception.IdNotFoundException;
import arden.java.kudago.service.LocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationController.class)
public class LocationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private LocationService locationService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetlocationsSuccess() throws Exception {
        List<Location> locations = List.of(
                new Location("spb",  "Кафе быстрого питания"),
                new Location("ekb",  "Магазин одежды")
        );

        when(locationService.getAllLocations()).thenReturn(locations);

        mockMvc.perform(get("/api/v1/locations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].slug").value("spb"));
    }

    @Test
    void testGetLocationBySlugSuccess() throws Exception {
        Location Location = new Location("spb",  "Кафе быстрого питания");
        when(locationService.getLocationBySlug("spb")).thenReturn(Location);

        mockMvc.perform(get("/api/v1/locations/spb"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.slug").value("spb"));
    }

    @Test
    void testGetLocationBySlugNotFound() throws Exception {
        when(locationService.getLocationBySlug(anyString())).thenThrow(new IdNotFoundException("Location not found"));

        mockMvc.perform(get("/api/v1/locations/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateLocationSuccess() throws Exception {
        Location newLocation = new Location(null, "Музей воды");
        Location createdLocation = new Location("spb", "Музей воды");

        when(locationService.createLocation(any(Location.class))).thenReturn(createdLocation);

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLocation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.slug").value("spb"));
    }

    @Test
    void testCreateLocationInvalidData() throws Exception {
        Location invalidLocation = new Location(null, "");
        when(locationService.createLocation(any(Location.class))).thenThrow(new CreationObjectException("Can't create an object"));

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLocation)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateLocationSuccess() throws Exception {
        Location updatedLocation = new Location("spb",  "Restaurant");

        when(locationService.updateLocation(anyString(), any(Location.class))).thenReturn(updatedLocation);

        mockMvc.perform(put("/api/v1/locations/spb")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedLocation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.slug").value("spb"));
    }

    @Test
    void testUpdateLocationNotFound() throws Exception {
        when(locationService.updateLocation(anyString(), any(Location.class)))
                .thenThrow(new IdNotFoundException("Location not found"));

        mockMvc.perform(put("/api/v1/locations/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Location("gfg", "Unknown"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteLocationSuccess() throws Exception {
        when(locationService.deleteLocation("spb")).thenReturn(true);

        mockMvc.perform(delete("/api/v1/locations/spb"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));
    }

    @Test
    void testDeleteLocationNotFound() throws Exception {
        when(locationService.deleteLocation(anyString())).thenThrow(new IdNotFoundException("Location not found"));

        mockMvc.perform(delete("/api/v1/locations/999"))
                .andExpect(status().isNotFound());
    }
}
