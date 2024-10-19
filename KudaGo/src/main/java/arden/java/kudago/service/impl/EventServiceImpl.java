package arden.java.kudago.service.impl;

import arden.java.kudago.client.CurrencyRestTemplate;
import arden.java.kudago.client.EventRestTemplate;
import arden.java.kudago.dto.request.CurrencyConvertRequest;
import arden.java.kudago.dto.response.event.Event;
import arden.java.kudago.dto.response.event.EventResponse;
import arden.java.kudago.exception.GeneralException;
import arden.java.kudago.service.EventService;
import arden.java.kudago.utils.DateParser;
import arden.java.kudago.utils.PriceParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService<List<EventResponse>, CompletableFuture<List<Event>>> {
    private final EventRestTemplate eventRestTemplate;
    private final CurrencyRestTemplate currencyRestTemplate;

    @Qualifier("fixedThreadPool")
    private final ExecutorService fixedThreadPool;

    @Override
    public List<EventResponse> getEvents(LocalDate dateFrom, LocalDate dateTo) {
        long start = DateParser.toEpochSeconds(Objects.requireNonNullElseGet(dateFrom, LocalDate::now));
        long end = DateParser.toEpochSeconds(Objects.requireNonNullElseGet(dateTo, () -> LocalDate.now().plusWeeks(1)));

        Optional<List<EventResponse>> events = eventRestTemplate.getEvents(start, end);

        if (events.isPresent()) {
            return events.get();
        }

        throw new GeneralException("No events found");
    }

    @Override
    public CompletableFuture<List<Event>> getSuitableEvents(Double budget, String currency, LocalDate dateFrom, LocalDate dateTo) {
        CompletableFuture<List<EventResponse>> eventsRequest = CompletableFuture.supplyAsync(() -> getEvents(dateFrom, dateTo), fixedThreadPool);
        CompletableFuture<Double> convertedPrice = CompletableFuture.supplyAsync(() -> currencyRestTemplate.convertPrice(CurrencyConvertRequest.builder()
                .fromCurrency(currency)
                .toCurrency("RUB")
                .amount(budget)
                .build()).orElseThrow(() -> new GeneralException("Сервер не доступен")), fixedThreadPool);

        return eventsRequest.thenCombine(convertedPrice, (events, price) ->
                events.stream()
                        .filter(event -> PriceParser.parseEventPrice(event.price()) <= price)
                        .sorted(Comparator.comparing(EventResponse::favoritesCount).reversed())
                        .map(event -> Event.builder()
                                .id(event.id())
                                .title(event.title())
                                .siteUrl(event.siteUrl())
                                .description(event.description().replace("<p>", "").replace("</p>", ""))
                                .favoritesCount(event.favoritesCount())
                                .price(!PriceParser.parseEventPrice(event.price()).equals(0D) ? PriceParser.parseEventPrice(event.price()).toString() : "бесплатно")
                                .dates(event.dates().stream()
                                        .map(date -> Event.ConvertedDates.builder()
                                                .start(DateParser.toLocalDate(date.start()))
                                                .end(DateParser.toLocalDate(date.end())).build())
                                        .filter(convertedDates -> {
                                            LocalDate from;
                                            LocalDate to;
                                            from = Objects.requireNonNullElseGet(dateFrom, LocalDate::now);
                                            to = Objects.requireNonNullElseGet(dateTo, () -> LocalDate.now().plusWeeks(1));

                                            return convertedDates.start().isAfter(from) && convertedDates.end().isBefore(to);
                                        })
                                        .toList())
                                .build())
                        .toList());
    }

}
