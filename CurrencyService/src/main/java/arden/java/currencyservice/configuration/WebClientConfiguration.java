package arden.java.currencyservice.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfiguration {
    private final UrlConfig urlConfig;

    @Bean
    public WebClient webClient() {
        return WebClient
                .builder()
                .baseUrl(urlConfig.url())
                .build();
    }

}
