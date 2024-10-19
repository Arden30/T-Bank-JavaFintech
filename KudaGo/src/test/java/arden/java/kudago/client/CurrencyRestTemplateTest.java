package arden.java.kudago.client;

import arden.java.kudago.dto.request.CurrencyConvertRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Testcontainers
public class CurrencyRestTemplateTest {
    @Autowired
    private CurrencyRestTemplate currencyRestTemplate;

    @Container
    public static WireMockContainer wiremockServer = new WireMockContainer("wiremock/wiremock:3.6.0")
            .withMappingFromResource("currency-stub.json");

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("base-config.currency-url", wiremockServer::getBaseUrl);
    }

    @Test
    public void testConvertPrice_SuccessTest() {
        Optional<Double> currency = currencyRestTemplate.convertPrice(CurrencyConvertRequest.builder()
                .fromCurrency("USD")
                .toCurrency("RUB")
                .amount(100D)
                .build());

        assertAll("Check response",
                () -> assertThat(currency.isPresent()).isTrue(),
                () -> assertThat(currency.get()).isEqualTo(10000D));
    }
}
