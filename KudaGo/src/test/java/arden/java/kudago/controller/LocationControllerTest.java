package arden.java.kudago.controller;

import arden.java.kudago.dto.response.places.LocationDto;
import arden.java.kudago.exception.CreationObjectException;
import arden.java.kudago.exception.IdNotFoundException;
import arden.java.kudago.service.LocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationController.class)
public class LocationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationService locationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetLocationsSuccess() throws Exception {
        List<LocationDto> locationResponse = List.of(
                new LocationDto("spb", "Кафе быстрого питания"),
                new LocationDto("ekb", "Магазин одежды")
        );

        when(locationService.getAllLocations()).thenReturn(locationResponse);

        mockMvc.perform(get("/api/v1/locations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].slug").value("spb"));
    }

    @Test
    void testGetLocationBySlugSuccess() throws Exception {
        LocationDto LocationDto = new LocationDto("spb", "Кафе быстрого питания");
        when(locationService.getLocationById(1L)).thenReturn(LocationDto);

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
        LocationDto createdLocationDto = new LocationDto("spb", "Музей воды");

        when(locationService.createLocation(any(LocationDto.class))).thenReturn(createdLocationDto);

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdLocationDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.slug").value("spb"));
    }

    @Test
    void testCreateLocationInvalidData() throws Exception {
        LocationDto invalidLocationDto = new LocationDto(null, "");
        when(locationService.createLocation(any(LocationDto.class))).thenThrow(new CreationObjectException("Can't create an object"));

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLocationDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateLocationSuccess() throws Exception {
        LocationDto updatedLocationDto = new LocationDto("spb", "Restaurant");

        when(locationService.updateLocation(anyLong(), any(LocationDto.class))).thenReturn(updatedLocationDto);

        mockMvc.perform(put("/api/v1/locations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedLocationDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.slug").value("spb"));
    }

    @Test
    void testUpdateLocationNotFound() throws Exception {
        when(locationService.updateLocation(anyLong(), any(LocationDto.class)))
                .thenThrow(new IdNotFoundException("Location not found"));

        mockMvc.perform(put("/api/v1/locations/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LocationDto("gfg", "Unknown"))))
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
