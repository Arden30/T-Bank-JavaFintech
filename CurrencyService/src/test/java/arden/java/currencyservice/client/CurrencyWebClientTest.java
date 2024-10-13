package arden.java.currencyservice.client;

import arden.java.currencyservice.exception.CurrencyException;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@WireMockTest(httpPort = 8081)
@ExtendWith(MockitoExtension.class)
public class CurrencyWebClientTest {

    @SpyBean
    private CurrencyWebClient currencyWebClient;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private final String body = """
            <ValCurs Date="05.10.2024" name="Foreign Currency Market">
                <Valute ID="R01010">
                    <NumCode>036</NumCode>
                    <CharCode>AUD</CharCode>
                    <Nominal>1</Nominal>
                    <Name>Австралийский доллар</Name>
                    <Value>64,9670</Value>
                    <VunitRate>64,967</VunitRate>
                </Valute>
                <Valute ID="R01020A">
                    <NumCode>944</NumCode>
                    <CharCode>AZN</CharCode>
                    <Nominal>1</Nominal>
                    <Name>Азербайджанский манат</Name>
                    <Value>55,8059</Value>
                    <VunitRate>55,8059</VunitRate>
                </Valute>
            </ValCurs>
            """;

    @BeforeEach
    void setUp() {
        Mockito.reset(currencyWebClient);
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("currencySystem");
        circuitBreaker.reset();
        cacheManager.getCache("currencyRateCache").clear();
    }

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("app.client.url", () -> "http://localhost:8081/XML_daily.asp");
    }

    @Test
    @DisplayName("Сервер доступен, корректный ответ")
    public void currencyRate_ServerIsAvailable_ReturnsOk() {
        stubFor(get("/XML_daily.asp")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(body)));

        String response = currencyWebClient.currencyRate();

        assertThat(response).isEqualTo(body);
    }

    @Test
    @DisplayName("Сервер недоступен, ошибка")
    public void currencyRate_ServerIsUnavailable_ReturnsServerInternalError() {
        stubFor(get("/XML_daily.asp")
                .willReturn(aResponse()
                        .withStatus(503)));

        assertThatThrownBy(() -> currencyWebClient.currencyRate())
                .isInstanceOf(CurrencyException.class)
                .hasMessageContaining("Сервис не доступен")
                .hasFieldOrPropertyWithValue("httpStatusCode", HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Test
    @DisplayName("Кэшируемость")
    public void testCacheableClient_IsBeingCached() {
        stubFor(get("/XML_daily.asp")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(body)));

        String response1 = currencyWebClient.currencyRate();
        assertThat(response1).isEqualTo(body);

        String response2 = currencyWebClient.currencyRate();
        assertThat(response2).isEqualTo(body);

        verify(currencyWebClient, times(1)).currencyRate();
    }
}
