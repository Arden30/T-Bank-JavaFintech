package arden.java.kudago.service;

import arden.java.kudago.dto.response.event.EventDto;
import arden.java.kudago.model.specification.EventSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {
    Page<EventDto> getAllEvents(Pageable pageable);

    Page<EventDto> getEventsByFilter(EventSpecification specification, Pageable pageable);

    EventDto getEventById(Long id);

    EventDto createEvent(EventDto EventDto);

    EventDto updateEvent(Long id, EventDto EventDto);

    void deleteEvent(Long id);
}
