package arden.java.kudago.service.impl;

import arden.java.kudago.client.CurrencyRestTemplate;
import arden.java.kudago.client.EventRestTemplate;
import arden.java.kudago.dto.request.CurrencyConvertRequest;
import arden.java.kudago.dto.response.event.SuitableEvent;
import arden.java.kudago.dto.response.event.EventResponse;
import arden.java.kudago.exception.GeneralException;
import arden.java.kudago.service.SuitableEventService;
import arden.java.kudago.utils.DateParser;
import arden.java.kudago.utils.PriceParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SuitableEventServiceReactiveImpl implements SuitableEventService<Mono<List<EventResponse>>, Mono<List<SuitableEvent>>> {
    private final EventRestTemplate eventRestTemplate;
    private final CurrencyRestTemplate currencyRestTemplate;

    @Override
    public Mono<List<EventResponse>> getEvents(LocalDate dateFrom, LocalDate dateTo) {
        long start = DateParser.toEpochSeconds(Objects.requireNonNullElseGet(dateFrom, LocalDate::now));
        long end = DateParser.toEpochSeconds(Objects.requireNonNullElseGet(dateTo, () -> LocalDate.now().plusWeeks(1)));

        Optional<List<EventResponse>> events = eventRestTemplate.getEvents(start, end);

        if (events.isPresent()) {
            return Mono.just(events.get());
        }

        throw new GeneralException("No events found");
    }

    @Override
    public Mono<List<SuitableEvent>> getSuitableEvents(Double budget, String currency, LocalDate dateFrom, LocalDate dateTo) {
        Mono<List<EventResponse>> eventsMono = getEvents(dateFrom, dateTo);
        Mono<Double> convertedPriceMono = Mono.just(currencyRestTemplate.convertPrice(CurrencyConvertRequest.builder()
                        .fromCurrency(currency)
                        .toCurrency("RUB")
                        .amount(budget)
                        .build())
                .orElseThrow(() -> new GeneralException("Сервер не доступен")));

        return Mono.zip(eventsMono, convertedPriceMono)
                .flatMap(tuple -> {
                    List<EventResponse> events = tuple.getT1();
                    Double price = tuple.getT2();

                    return Flux.fromIterable(events)
                            .filter(event -> PriceParser.parseEventPrice(event.price()) <= price)
                            .sort(Comparator.comparing(EventResponse::favoritesCount).reversed())
                            .map(event -> SuitableEvent.builder()
                                    .id(event.id())
                                    .title(event.title())
                                    .siteUrl(event.siteUrl())
                                    .description(event.description().replace("<p>", "").replace("</p>", ""))
                                    .favoritesCount(event.favoritesCount())
                                    .price(!PriceParser.parseEventPrice(event.price()).equals(0D) ? PriceParser.parseEventPrice(event.price()).toString() : "бесплатно")
                                    .dates(event.dates().stream()
                                            .map(date -> SuitableEvent.ConvertedDates.builder()
                                                    .start(DateParser.toLocalDate(date.start()))
                                                    .end(DateParser.toLocalDate(date.end())).build())
                                            .filter(convertedDates -> {
                                                LocalDate from = Objects.requireNonNullElseGet(dateFrom, LocalDate::now);
                                                LocalDate to = Objects.requireNonNullElseGet(dateTo, () -> LocalDate.now().plusWeeks(1));
                                                return convertedDates.start().isAfter(from) && convertedDates.end().isBefore(to);
                                            })
                                            .toList())
                                    .build())
                            .collectList();
                });
    }
}
