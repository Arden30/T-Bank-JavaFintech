package arden.java.kudago.service;

import org.springframework.scheduling.annotation.Async;

import java.time.LocalDate;

public interface SuitableEventService<T, U> {
    T getEvents(LocalDate dateFrom, LocalDate dateTo);

    @Async
    U getSuitableEvents(Double budget, String currency, LocalDate dateFrom, LocalDate dateTo);
}
