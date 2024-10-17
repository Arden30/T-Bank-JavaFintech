package arden.java.kudago.controller;

import arden.java.kudago.dto.response.event.Event;
import arden.java.kudago.service.EventService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @Validated
    @GetMapping("/events")
    public ResponseEntity<List<Event>> getSuitableEvents(@RequestParam @Min(value = 0, message = "Ваш бюджет должен быть неотрицательным числом")
                                                         Double budget,

                                                         @RequestParam @NotNull(message = "Укажите валюту для конвертации")
                                                         String currency,

                                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                         LocalDate dateFrom,

                                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                         LocalDate dateTo) {
        return ResponseEntity.ok(eventService.getSuitableEvents(budget, currency, dateFrom, dateTo).join());
    }
}
