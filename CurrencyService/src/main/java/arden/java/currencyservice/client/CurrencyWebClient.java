package arden.java.currencyservice.client;

import arden.java.currencyservice.exception.CurrencyException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurrencyWebClient {
    private final WebClient webClient;

    @CircuitBreaker(name = "currencySystem", fallbackMethod = "fallbackMethod")
    @Cacheable(value = "currencyRateCache", key = "#root.methodName")
    public String currencyRate() {
        return webClient.get()
                .header("date_req", LocalDate.now().toString())
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, (response) -> {
                    throw new CurrencyException("Сервис не доступен", HttpStatus.SERVICE_UNAVAILABLE).withHeader("Retry-After:", "3600");
                })
                .bodyToMono(String.class)
                .log("Получен ответ от сервера")
                .block();
    }

    private String fallbackMethod(Throwable throwable) {
        log.error("Проблемы с запросом к API {}", throwable.getMessage());
        return "Запросы приостановлены";
    }
}
