package arden.java.kudago.client;

import arden.java.kudago.dto.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Testcontainers
public class LocationRestTemplateTest {
    @Autowired
    private LocationRestTemplate locationRestTemplate;

    @Container
    public static WireMockContainer wiremockServer = new WireMockContainer("wiremock/wiremock:3.6.0")
            .withMappingFromResource("kudago-stub.json");

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("base-config.url", wiremockServer::getBaseUrl);
    }

    @Test
    public void testGetAllLocations() {
        Optional<List<Location>> locations = locationRestTemplate.getLocations();

        assertAll("Check response",
                () -> assertThat(locations.isPresent()).isTrue(),
                () -> assertThat(locations.get().size()).isEqualTo(2),
                () -> assertThat(locations.get().getFirst().slug()).isEqualTo("spb"),
                () -> assertThat(locations.get().getFirst().name()).isEqualTo("Санкт-Петербург"));
    }
}
