package arden.java.kudago.service.impl;

import arden.java.kudago.dto.response.event.EventDto;
import arden.java.kudago.exception.GeneralException;
import arden.java.kudago.exception.IdNotFoundException;
import arden.java.kudago.model.Event;
import arden.java.kudago.model.specification.EventSpecification;
import arden.java.kudago.repository.EventRepository;
import arden.java.kudago.repository.LocationRepository;
import arden.java.kudago.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final LocationServiceImpl locationService;
    private final LocationRepository locationRepository;

    @Override
    public Page<EventDto> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable)
                .map(this::createResponseFromEvent);
    }

    @Override
    public Page<EventDto> getEventsByFilter(EventSpecification specification, Pageable pageable) {
        return eventRepository.findAll(specification, pageable)
                .map(this::createResponseFromEvent);
    }

    @Override
    public EventDto getEventById(Long id) {
        return createResponseFromEvent(eventRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Event with id '" + id + "' not found")));
    }

    @Override
    public EventDto createEvent(EventDto eventDto) {
        Long locationId = eventDto.locationId();
        if (locationRepository.findByIdEager(locationId).isEmpty()) {
            throw new GeneralException("Location does not exist");
        }

        return createResponseFromEvent(eventRepository.save(createEventFromResponse(eventDto)));
    }

    @Override
    @Transactional
    public EventDto updateEvent(Long id, EventDto eventDto) {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Event with id '" + id + "' not found"));

        existingEvent.setName(eventDto.name());
        existingEvent.setDate(eventDto.date());
        existingEvent.setLocation(locationRepository.findByIdEager(eventDto.locationId()).orElseThrow(() -> new IdNotFoundException("Location with id '" + eventDto.locationId() + "' not found")));

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
                .locationId(event.getLocation().getId())
                .build();
    }

    private Event createEventFromResponse(EventDto eventDto) {
        Event event = new Event();
        event.setName(eventDto.name());
        event.setDate(eventDto.date());
        event.setLocation(locationRepository.findByIdEager(eventDto.locationId()).orElseThrow(() -> new IdNotFoundException("Location with id '" + eventDto.locationId() + "' not found")));

        return event;
    }
}
