package arden.java.kudago.client;

import arden.java.kudago.dto.response.event.EventResponse;
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
public class EventRestTemplateTest {
    @Autowired
    private EventRestTemplate eventRestTemplate;

    @Container
    public static WireMockContainer wiremockServer = new WireMockContainer("wiremock/wiremock:3.6.0")
            .withMappingFromResource("events-stub.json");

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("base-config.kuda-go-url", wiremockServer::getBaseUrl);
    }

    @Test
    public void testGetAllEvents_SuccessTest() {
        Optional<List<EventResponse>> events = eventRestTemplate.getEvents(1727740800, 1730419199);

        assertAll("Check response",
                () -> assertThat(events.isPresent()).isTrue(),
                () -> assertThat(events.get().size()).isEqualTo(2),
                () -> assertThat(events.get().getFirst().id()).isEqualTo(1L),
                () -> assertThat(events.get().getFirst().title()).isEqualTo("Фестиваль осени"));
    }
}
