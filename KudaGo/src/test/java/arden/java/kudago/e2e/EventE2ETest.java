package arden.java.kudago.e2e;

import arden.java.kudago.dto.response.event.EventDto;
import arden.java.kudago.model.Location;
import arden.java.kudago.repository.LocationRepository;
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

import java.time.OffsetDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Transactional
public class EventE2ETest {
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
            .withDatabaseName("test-locationDto")
            .withUsername("test-locationDto")
            .withPassword("test-locationDto");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LocationRepository locationRepository;

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
        entityManager.createNativeQuery("TRUNCATE TABLE events RESTART IDENTITY CASCADE").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE locations RESTART IDENTITY CASCADE").executeUpdate();

        Location location1 = new Location();
        location1.setId(1L);
        location1.setName("Some Name");
        location1.setSlug("Some Slug");
        locationRepository.save(location1);

        Location location2 = new Location();
        location2.setId(2L);
        location2.setName("name");
        location2.setSlug("slug");
        locationRepository.save(location2);
    }

    @Test
    void createAndUpdate_shouldCreateAndRetrieveEvent() throws Exception {
        EventDto eventDto = new EventDto("event", OffsetDateTime.parse("2024-10-23T15:30:00+01:00"), 1L);

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("event"))
                .andExpect(jsonPath("$.date").value("2024-10-23T14:30:00Z"));

        mockMvc.perform(get("/api/v1/events/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("event"))
                .andExpect(jsonPath("$.date").value("2024-10-23T14:30:00Z"));
    }

    @Test
    void shouldUpdateEvent() throws Exception {
        EventDto eventDto = new EventDto("event", OffsetDateTime.parse("2024-10-23T15:30:00+01:00"), 1L);

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isOk());

        EventDto updatedEventDto = new EventDto("event-updated", OffsetDateTime.parse("2024-10-23T15:30:00+01:00"), 1L);
        mockMvc.perform(put("/api/v1/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEventDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("event-updated"))
                .andExpect(jsonPath("$.date").value("2024-10-23T14:30:00Z"));

        mockMvc.perform(get("/api/v1/events/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("event-updated"))
                .andExpect(jsonPath("$.date").value("2024-10-23T14:30:00Z"));
    }

    @Test
    void getAllEventsByFilter_shouldReturnFilteredEvents() throws Exception {
        EventDto eventDto1 = new EventDto("Art Exhibition", OffsetDateTime.parse("2024-10-23T15:30:00+01:00"), 2L);
        EventDto eventDto2 = new EventDto("Music Concert", OffsetDateTime.parse("2024-10-24T19:00:00+01:00"), 1L);
        EventDto eventDto3 = new EventDto("Theater Play", OffsetDateTime.parse("2024-10-26T20:00:00+01:00"), 2L);

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto1)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto2)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto3)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/events/filter")
                        .param("name", "Art Exhibition"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Art Exhibition"));


        mockMvc.perform(get("/api/v1/events/filter")
                        .param("location.name", "Some Name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Music Concert"));
    }

}
