package arden.java.kudago.controller;

import arden.java.kudago.dto.response.event.EventDto;
import arden.java.kudago.dto.response.event.SuitableEvent;
import arden.java.kudago.dto.response.event.EventResponse;
import arden.java.kudago.service.EventService;
import arden.java.kudago.service.SuitableEventService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {
    private final SuitableEventService<Mono<List<EventResponse>>, Mono<List<SuitableEvent>>> suitableEventService;
    private final EventService eventService;
    
    @Validated
    @GetMapping("/suitable")
    public ResponseEntity<List<SuitableEvent>> getSuitableEvents(@RequestParam @Min(value = 0, message = "Ваш бюджет должен быть неотрицательным числом")
                                                         Double budget,

                                                                 @RequestParam @NotNull(message = "Укажите валюту для конвертации")
                                                         String currency,

                                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                         LocalDate dateFrom,

                                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                         LocalDate dateTo) {
        return ResponseEntity.ok(suitableEventService.getSuitableEvents(budget, currency, dateFrom, dateTo).block());
    }

    @GetMapping("/list")
    public ResponseEntity<Page<EventDto>> getAllEvents(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(eventService.getAllEvents(PageRequest.of(page, size)));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<EventDto>> getAllEventsByFilter(@RequestParam(required = false) String name,
                                                               @RequestParam(required = false) String location,
                                                               @RequestParam(required = false) OffsetDateTime fromDate,
                                                               @RequestParam(required = false) OffsetDateTime toDate) {
        return ResponseEntity.ok(eventService.getEventsByFilter(name, location, fromDate, toDate));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PostMapping
    public ResponseEntity<EventDto> createEvent(@RequestBody EventDto EventDto) {
        return ResponseEntity.ok(eventService.createEvent(EventDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDto> updateCategory(@PathVariable Long id, @RequestBody EventDto EventDto) {
        return ResponseEntity.ok(eventService.updateEvent(id, EventDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok(true);
    }
}
