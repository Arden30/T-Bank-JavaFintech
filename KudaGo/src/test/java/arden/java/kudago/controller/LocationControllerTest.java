package arden.java.kudago.controller;

import arden.java.kudago.dto.response.places.LocationResponse;
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
import static org.mockito.Mockito.*;
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
    void testGetLocationsSuccess() throws Exception {
        List<LocationResponse> locationResponses = List.of(
                new LocationResponse("spb",  "Кафе быстрого питания"),
                new LocationResponse("ekb",  "Магазин одежды")
        );

        when(locationService.getAllLocations()).thenReturn(locationResponses);

        mockMvc.perform(get("/api/v1/locations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].slug").value("spb"));
    }

    @Test
    void testGetLocationBySlugSuccess() throws Exception {
        LocationResponse LocationResponse = new LocationResponse("spb",  "Кафе быстрого питания");
        when(locationService.getLocationById(1L)).thenReturn(LocationResponse);

        mockMvc.perform(get("/api/v1/locations/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.slug").value("spb"));
    }

    @Test
    void testGetLocationBySlugNotFound() throws Exception {
        when(locationService.getLocationById(anyLong())).thenThrow(new IdNotFoundException("Location not found"));

        mockMvc.perform(get("/api/v1/locations/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateLocationSuccess() throws Exception {
        LocationResponse createdLocationResponse = new LocationResponse("spb", "Музей воды");

        when(locationService.createLocation(any(LocationResponse.class))).thenReturn(createdLocationResponse);

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdLocationResponse)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.slug").value("spb"));
    }

    @Test
    void testCreateLocationInvalidData() throws Exception {
        LocationResponse invalidLocationResponse = new LocationResponse(null, "");
        when(locationService.createLocation(any(LocationResponse.class))).thenThrow(new CreationObjectException("Can't create an object"));

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLocationResponse)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateLocationSuccess() throws Exception {
        LocationResponse updatedLocationResponse = new LocationResponse("spb",  "Restaurant");

        when(locationService.updateLocation(anyLong(), any(LocationResponse.class))).thenReturn(updatedLocationResponse);

        mockMvc.perform(put("/api/v1/locations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedLocationResponse)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.slug").value("spb"));
    }

    @Test
    void testUpdateLocationNotFound() throws Exception {
        when(locationService.updateLocation(anyLong(), any(LocationResponse.class)))
                .thenThrow(new IdNotFoundException("Location not found"));

        mockMvc.perform(put("/api/v1/locations/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LocationResponse("gfg", "Unknown"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteLocationSuccess() throws Exception {
        doNothing().when(locationService).deleteLocation(1L);

        mockMvc.perform(delete("/api/v1/locations/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));
    }

    @Test
    void testDeleteLocationNotFound() throws Exception {
        doThrow(new IdNotFoundException("Location not found")).when(locationService).deleteLocation(anyLong());

        mockMvc.perform(delete("/api/v1/locations/999"))
                .andExpect(status().isNotFound());
    }
}
