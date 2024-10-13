package arden.java.kudago.client;

import arden.java.kudago.dto.Category;
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
public class CategoryRestTemplateTest {
    @Autowired
    private CategoryRestTemplate categoryRestTemplate;

    @Container
    public static WireMockContainer wiremockServer = new WireMockContainer("wiremock/wiremock:3.6.0")
            .withMappingFromResource("kudago-stub.json");

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("base-config.url", wiremockServer::getBaseUrl);
    }

    @Test
    public void testGetAllLocations() {
        Optional<List<Category>> categories = categoryRestTemplate.getAllCategories();

        assertAll("Check response",
                () -> assertThat(categories.isPresent()).isTrue(),
                () -> assertThat(categories.get().size()).isEqualTo(2),
                () -> assertThat(categories.get().getFirst().id()).isEqualTo(1L),
                () -> assertThat(categories.get().getFirst().name()).isEqualTo("Магазин здорового питания"));
    }
}
