package arden.java.kudago.service.impl;

import arden.java.kudago.client.CurrencyRestTemplate;
import arden.java.kudago.client.EventRestTemplate;
import arden.java.kudago.dto.request.CurrencyConvertRequest;
import arden.java.kudago.dto.response.event.Event;
import arden.java.kudago.dto.response.event.EventResponse;
import arden.java.kudago.exception.GeneralException;
import arden.java.kudago.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRestTemplate eventRestTemplate;
    private final CurrencyRestTemplate currencyRestTemplate;

    @Qualifier("fixedThreadPool")
    private final ExecutorService fixedThreadPool;

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
                        .filter(event -> parseEventPrice(event.price()) <= price)
                        .sorted(Comparator.comparing(EventResponse::favoritesCount).reversed())
                        .map(event -> Event.builder()
                                .id(event.id())
                                .title(event.title())
                                .siteUrl(event.siteUrl())
                                .description(event.description().replace("<p>", "").replace("</p>", ""))
                                .favoritesCount(event.favoritesCount())
                                .price(!parseEventPrice(event.price()).equals(0D) ? parseEventPrice(event.price()).toString() : "бесплатно")
                                .dates(event.dates().stream()
                                        .map(date -> Event.ConvertedDates.builder()
                                                .start(toLocalDate(date.start()))
                                                .end(toLocalDate(date.end())).build())
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

    @Override
    public List<EventResponse> getEvents(LocalDate dateFrom, LocalDate dateTo) {
        long start, end;
        if (dateFrom == null || dateTo == null) {
            start = toEpochSeconds(LocalDate.now());
            end = toEpochSeconds(LocalDate.now().plusWeeks(1));
        } else {
            start = toEpochSeconds(dateFrom);
            end = toEpochSeconds(dateTo);
        }

        Optional<List<EventResponse>> events = eventRestTemplate.getEvents(start, end);

        if (events.isPresent()) {
            return events.get();
        }

        throw new GeneralException("No events found");
    }

    public Double parseEventPrice(String event) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(event);

        return matcher.find() ? Double.parseDouble(matcher.group()) : 0D;
    }

    public LocalDate toLocalDate(Long date) {
        return Instant.ofEpochSecond(date).atZone(ZoneOffset.UTC).toLocalDate();
    }

    public long toEpochSeconds(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
    }
}
