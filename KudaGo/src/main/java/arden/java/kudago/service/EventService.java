package arden.java.kudago.service;

import arden.java.kudago.dto.response.event.EventDto;

import java.time.OffsetDateTime;
import java.util.List;

public interface EventService {
    List<EventDto> getAllEvents();
    List<EventDto> getEventsByFilter(String name, String location, OffsetDateTime fromDate, OffsetDateTime toDate);

    EventDto getEventById(Long id);

    EventDto createEvent(EventDto EventDto);

    EventDto updateEvent(Long id, EventDto EventDto);
    void deleteEvent(Long id);
}
