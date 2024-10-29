package arden.java.kudago.repository;

import arden.java.kudago.model.Event;
import arden.java.kudago.model.Location;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Transactional
public class EventRepositoryTest {
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private EventRepository eventRepository;

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
        entityManager.createNativeQuery("TRUNCATE TABLE locations RESTART IDENTITY CASCADE").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE events RESTART IDENTITY CASCADE").executeUpdate();

        Location location = new Location();
        location.setName("Test Location");
        location.setSlug("test-locationDto");
        locationRepository.save(location);

        Event event1 = new Event();
        event1.setName("Party");
        event1.setDate(OffsetDateTime.parse("2024-10-23T15:30:00+01:00"));
        event1.setLocation(location);
        eventRepository.save(event1);

        Event event2 = new Event();
        event2.setName("Concert");
        event2.setDate(OffsetDateTime.parse("2024-10-21T15:30:00+01:00"));
        event2.setLocation(location);
        eventRepository.save(event2);
    }

    @Test
    @DisplayName("Find all events from the list")
    void testFindAllEvents() {
        List<Event> events = eventRepository.findAll();
        assertThat(events).hasSize(2);
    }

    @Test
    @DisplayName("Filter events by name")
    void testFindByName() {
        Specification<Event> spec = buildSpecification("Party", null, null, null);
        List<Event> events = eventRepository.findAll(spec);
        assertThat(events).hasSize(1);
        assertThat(events.getFirst().getName()).isEqualTo("Party");
    }

    @Test
    @DisplayName("Filter events by date")
    void testFilterByDate() {
        Specification<Event> spec = buildSpecification(null, null,
                OffsetDateTime.parse("2024-10-22T15:30:00+01:00"),
                OffsetDateTime.parse("2024-10-24T15:30:00+01:00"));
        List<Event> events = eventRepository.findAll(spec);
        assertThat(events).hasSize(1);
        assertThat(events.getFirst().getName()).isEqualTo("Party");
    }

    @Test
    @DisplayName("Filter events by locationDto")
    void testFindByLocation() {
        Specification<Event> spec = buildSpecification(null, "Test Location", null, null);
        List<Event> events = eventRepository.findAll(spec);
        assertThat(events).hasSize(2);
    }

    @Test
    @DisplayName("Empty list in case of filter mismatch")
    void testEmptyList() {
        Specification<Event> spec = buildSpecification("?", null, null, null);
        List<Event> events = eventRepository.findAll(spec);
        assertThat(events).hasSize(0);
    }

    static Specification<Event> buildSpecification(String name, String location, OffsetDateTime fromDate, OffsetDateTime toDate) {
        List<Specification<Event>> specs = new ArrayList<>();
        if (name != null) {
            specs.add((Specification<Event>) (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.<String>get("name"), name));
        }

        if (location != null) {
            specs.add((Specification<Event>) (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.<String>get("location").get("name"), location));
        }

        if (fromDate != null && toDate != null) {
            specs.add((Specification<Event>) (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("date"), fromDate, toDate));
        }

        return specs.stream().reduce(Specification::and).orElse(null);
    }
}

