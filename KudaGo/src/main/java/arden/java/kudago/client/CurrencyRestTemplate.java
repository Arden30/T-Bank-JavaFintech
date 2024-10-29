package arden.java.kudago.client;

import arden.java.kudago.config.UrlConfig;
import arden.java.kudago.dto.request.CurrencyConvertRequest;
import arden.java.kudago.dto.response.currency.CurrencyConvertResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RequiredArgsConstructor
public class CurrencyRestTemplate {
    private final RestTemplate restTemplate;
    private final UrlConfig urlConfig;

    public Optional<Double> convertPrice(CurrencyConvertRequest currencyConvertRequest) {
        ResponseEntity<CurrencyConvertResponse> response = restTemplate
                .exchange(urlConfig.currencyUrl() + "/convert",
                        HttpMethod.POST,
                        new HttpEntity<>(currencyConvertRequest),
                        new ParameterizedTypeReference<>() {
                        });

        return Optional.ofNullable(response.getBody().convertedAmount());
    }
}
