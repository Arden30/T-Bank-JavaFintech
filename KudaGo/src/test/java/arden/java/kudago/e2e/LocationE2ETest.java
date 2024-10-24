package arden.java.kudago.e2e;

import arden.java.kudago.dto.response.places.LocationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Transactional
public class LocationE2ETest {
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
            .withDatabaseName("test-location")
            .withUsername("test-location")
            .withPassword("test-location");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @DynamicPropertySource
    static void jpaProperties(@NotNull DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @BeforeEach
    void setUp() {
        entityManager.createNativeQuery("TRUNCATE TABLE locations RESTART IDENTITY CASCADE").executeUpdate();
    }

    @Test
    void createAndUpdate_shouldCreateAndRetrieveLocation() throws Exception {
        LocationDto newLocation = new LocationDto("slug-1", "Location 1");

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLocation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("slug-1"))
                .andExpect(jsonPath("$.name").value("Location 1"));

        mockMvc.perform(get("/api/v1/locations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("slug-1"))
                .andExpect(jsonPath("$.name").value("Location 1"));
    }

    @Test
    void shouldUpdateLocation() throws Exception {
        LocationDto newLocation = new LocationDto("slug-2", "Location 2");

        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLocation)))
                .andExpect(status().isOk());

        LocationDto updatedLocation = new LocationDto("updated-slug", "Updated Location");
        mockMvc.perform(put("/api/v1/locations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedLocation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("updated-slug"))
                .andExpect(jsonPath("$.name").value("Updated Location"));

        mockMvc.perform(get("/api/v1/locations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("updated-slug"))
                .andExpect(jsonPath("$.name").value("Updated Location"));
    }
}
