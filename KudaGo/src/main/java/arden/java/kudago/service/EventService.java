package arden.java.kudago.service;

import arden.java.kudago.dto.response.event.EventDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;

public interface EventService {
    Page<EventDto> getAllEvents(Pageable pageable);

    List<EventDto> getEventsByFilter(String name, String location, OffsetDateTime fromDate, OffsetDateTime toDate);

    EventDto getEventById(Long id);

    EventDto createEvent(EventDto EventDto);

    EventDto updateEvent(Long id, EventDto EventDto);

    void deleteEvent(Long id);
}
