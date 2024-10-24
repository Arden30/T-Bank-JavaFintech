package arden.java.kudago.service.impl;

import arden.java.kudago.dto.response.event.EventDto;
import arden.java.kudago.exception.IdNotFoundException;
import arden.java.kudago.model.Event;
import arden.java.kudago.repository.EventRepository;
import arden.java.kudago.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    @Override
    public List<EventDto> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::createResponseFromEvent)
                .toList();
    }

    @Override
    public List<EventDto> getEventsByFilter(String name, String location, OffsetDateTime fromDate, OffsetDateTime toDate) {
        return eventRepository.findAll(EventRepository.buildSpecification(name, location, fromDate, toDate)).stream()
                .map(this::createResponseFromEvent)
                .toList();
    }

    @Override
    public EventDto getEventById(Long id) {
        return createResponseFromEvent(eventRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Event with id '" + id + "' not found")));
    }

    @Override
    public EventDto createEvent(EventDto eventDto) {
        return createResponseFromEvent(eventRepository.save(createEventFromResponse(eventDto)));
    }

    @Override
    public EventDto updateEvent(Long id, EventDto eventDto) {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Event with id '" + id + "' not found"));

        existingEvent.setName(eventDto.name());
        existingEvent.setDate(eventDto.date());
        existingEvent.setLocation(eventDto.location());

        return createResponseFromEvent(eventRepository.save(existingEvent));
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    private EventDto createResponseFromEvent(Event event) {
        return EventDto.builder()
                .name(event.getName())
                .date(event.getDate())
                .location(event.getLocation())
                .build();
    }

    private Event createEventFromResponse(EventDto eventDto) {
        Event event = new Event();
        event.setName(eventDto.name());
        event.setDate(eventDto.date());
        event.setLocation(eventDto.location());

        return event;
    }
}
