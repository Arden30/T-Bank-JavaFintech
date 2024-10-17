package arden.java.kudago.service;

import arden.java.kudago.dto.response.event.EventResponse;
import arden.java.kudago.dto.response.event.Event;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface EventService {
    List<EventResponse> getEvents(LocalDate dateFrom, LocalDate dateTo);

    @Async
    CompletableFuture<List<Event>> getSuitableEvents(Double budget, String currency, LocalDate dateFrom, LocalDate dateTo);
}
