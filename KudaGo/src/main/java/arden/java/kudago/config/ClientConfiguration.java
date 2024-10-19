package arden.java.kudago.config;

import arden.java.kudago.client.CategoryRestTemplate;
import arden.java.kudago.client.CurrencyRestTemplate;
import arden.java.kudago.client.EventRestTemplate;
import arden.java.kudago.client.LocationRestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class ClientConfiguration {
    private final RestTemplate restTemplate;
    private final UrlConfig urlConfig;

    @Bean
    public CategoryRestTemplate categoryRestTemplate() {
        return new CategoryRestTemplate(restTemplate, urlConfig);
    }

    @Bean
    public LocationRestTemplate locationRestTemplate() {
        return new LocationRestTemplate(restTemplate, urlConfig);
    }

    @Bean
    public CurrencyRestTemplate currencyRestTemplate() {
        return new CurrencyRestTemplate(restTemplate, urlConfig);
    }

    @Bean
    public EventRestTemplate eventRestTemplate() {
        return new EventRestTemplate(restTemplate, urlConfig);
    }
}
