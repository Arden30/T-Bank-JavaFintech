package arden.java.kudago.service;

import arden.java.kudago.client.CurrencyRestTemplate;
import arden.java.kudago.client.EventRestTemplate;
import arden.java.kudago.dto.request.CurrencyConvertRequest;
import arden.java.kudago.dto.response.event.Event;
import arden.java.kudago.dto.response.event.EventResponse;
import arden.java.kudago.service.impl.EventServiceReactiveImpl;
import arden.java.kudago.utils.DateParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceReactiveTest {
    @InjectMocks
    private EventServiceReactiveImpl eventService;

    @Mock
    private CurrencyRestTemplate currencyRestTemplate;

    @Mock
    private EventRestTemplate eventRestTemplate;

    @Mock
    private ExecutorService executorService;

    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(10);
        eventService = new EventServiceReactiveImpl(eventRestTemplate, currencyRestTemplate);
    }

    @Test
    @DisplayName("Только одно из двух мероприятий подходит по цене")
    void getSuitableEvents_differentPrices_successTest() {
        CurrencyConvertRequest currencyConvertRequest = CurrencyConvertRequest.builder()
                .fromCurrency("USD")
                .toCurrency("RUB")
                .amount(100D)
                .build();
        EventResponse suitableEventResponse = EventResponse.builder()
                .id(1L)
                .dates(List.of(new EventResponse.Dates(1727740800L, 1738368000L))) // от 01.10.24 до 01.02.25
                .title("Фестиваль осени")
                .description("Какой-то классный фестиваль")
                .siteUrl("https://festival.ru")
                .price("400.0")
                .favoritesCount(30L)
                .build();
        EventResponse unsuitableEventResponse = EventResponse.builder()
                .id(2L)
                .dates(List.of(new EventResponse.Dates(1727740800L, 1738368000L))) // от 01.10.24 до 01.02.25
                .title("Очень дорогое")
                .description("Ну очень дорогое")
                .siteUrl("https://expensive.ru")
                .price("40000000.0")
                .favoritesCount(30L)
                .build();
        LocalDate start = LocalDate.of(2024, 10, 1);
        LocalDate end = LocalDate.of(2025, 3, 1);

        when(eventRestTemplate.getEvents(DateParser.toEpochSeconds(start), DateParser.toEpochSeconds(end))).thenReturn(Optional.of(List.of(
                suitableEventResponse,
                unsuitableEventResponse
        )));
        when(currencyRestTemplate.convertPrice(currencyConvertRequest)).thenReturn(Optional.of(1000D));
        List<Event> events = eventService.getSuitableEvents(100D, "USD", start, end).block();

        assertAll("Check respoonse",
                () -> assertThat(events.size()).isEqualTo(1),
                () -> assertThat(events.getFirst().id()).isEqualTo(1L));
    }

    @Test
    @DisplayName("Дата не указана")
    void getSuitableEvents_differentDates_successTest() {
        CurrencyConvertRequest currencyConvertRequest = CurrencyConvertRequest.builder()
                .fromCurrency("USD")
                .toCurrency("RUB")
                .amount(100D)
                .build();
        EventResponse eventResponse = EventResponse.builder()
                .id(1L)
                .dates(List.of(new EventResponse.Dates(1727740800L, 1738368000L))) // от 01.10.24 до 01.02.25
                .title("Выставка")
                .description("Бесплатная выставка")
                .siteUrl("https://free.ru")
                .price("0.0")
                .favoritesCount(30L)
                .build();

        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusWeeks(1);
        when(eventRestTemplate.getEvents(DateParser.toEpochSeconds(start), DateParser.toEpochSeconds(end))).thenReturn(Optional.of(List.of(
                eventResponse
        )));
        when(currencyRestTemplate.convertPrice(currencyConvertRequest)).thenReturn(Optional.of(1000D));
        List<Event> events = eventService.getSuitableEvents(100D, "USD", null, null).block();

        assertAll("Check response",
                () -> assertThat(events.size()).isEqualTo(1),
                () -> assertThat(events.getFirst().id()).isEqualTo(1L),
                () -> assertThat(events.getFirst().price()).isEqualTo("бесплатно"));
    }
}
