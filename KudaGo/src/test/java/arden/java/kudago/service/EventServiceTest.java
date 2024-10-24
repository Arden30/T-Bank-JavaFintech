package arden.java.kudago.service;

import arden.java.kudago.dto.response.event.EventDto;
import arden.java.kudago.exception.IdNotFoundException;
import arden.java.kudago.model.Event;
import arden.java.kudago.repository.EventRepository;
import arden.java.kudago.service.impl.EventServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @InjectMocks
    private EventServiceImpl eventService;

    private final List<Optional<Event>> events = List.of(
            Optional.of(new Event(1L, "Party", OffsetDateTime.parse("2024-10-23T15:30:00+01:00"), null)),
            Optional.of(new Event(2L, "Concert", OffsetDateTime.parse("2024-10-21T15:30:00+01:00"), null))
    );

    private final List<EventDto> eventsList = List.of(
            new EventDto("Party", OffsetDateTime.parse("2024-10-23T15:30:00+01:00"), null),
            new EventDto("Concert", OffsetDateTime.parse("2024-10-21T15:30:00+01:00"), null)
    );

    @Test
    @DisplayName("Getting all Events: success test")
    public void getAllEvents_successTest() {
        //Arrange
        when(eventRepository.findAll()).thenReturn(events.stream().map(Optional::get).toList());

        //Act
        List<EventDto> EventDto = eventService.getAllEvents();

        //Assert
        assertThat(EventDto).isEqualTo(eventsList);
    }

    @Test
    @DisplayName("Getting all Events: fail test")
    public void getAllEvents_failTest() {
        when(eventRepository.findAll()).thenReturn(Collections.emptyList());

        List<EventDto> EventDto = eventService.getAllEvents();

        assertThat(Collections.emptyList()).isEqualTo(EventDto);
    }

    @Test
    @DisplayName("Getting Event by id: success test")
    public void getEventBySlug_successTest() {
        when(eventRepository.findById(1L)).thenReturn(events.getFirst());

        EventDto EventDto = eventService.getEventById(1L);

        assertThat(EventDto).isEqualTo(eventsList.getFirst());
    }

    @Test
    @DisplayName("Getting Event by id: fail test")
    public void getEventBySlug_failTest() {
        when(eventRepository.findById(1L)).thenThrow(new IdNotFoundException("id not found"));

        assertThrows(IdNotFoundException.class, () -> eventService.getEventById(1L));
    }

    @Test
    @DisplayName("Create new Event: success test")
    public void createEvent_successTest() {
        when(eventRepository.save(any(Event.class))).thenReturn(events.getFirst().get());

        EventDto EventDto = eventService.createEvent(eventsList.getFirst());

        assertThat(EventDto).isEqualTo(eventsList.getFirst());
    }

    @Test
    @DisplayName("Update Event: success test")
    public void updateEvent_successTest() {
        when(eventRepository.save(any(Event.class))).thenReturn(events.getLast().get());
        when(eventRepository.findById(1L)).thenReturn(events.getFirst());

        EventDto EventDto = eventService.updateEvent(1L, eventsList.getLast());

        assertThat(EventDto).isEqualTo(eventsList.getLast());
    }

    @Test
    @DisplayName("Update Event: fail test")
    public void updateEvent_failTest() {
        assertThrows(IdNotFoundException.class, () -> eventService.updateEvent(2L, eventsList.getLast()));
    }

    @Test
    @DisplayName("Delete Event: success test")
    public void deleteEvent_successTest() {
        assertDoesNotThrow(() -> eventService.deleteEvent(events.getFirst().get().getId()));
    }
}
