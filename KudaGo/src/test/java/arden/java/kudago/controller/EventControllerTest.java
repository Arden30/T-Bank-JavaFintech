package arden.java.kudago.controller;

import arden.java.kudago.dto.response.event.EventDto;
import arden.java.kudago.dto.response.event.EventResponse;
import arden.java.kudago.dto.response.event.SuitableEvent;
import arden.java.kudago.exception.CreationObjectException;
import arden.java.kudago.exception.IdNotFoundException;
import arden.java.kudago.service.EventService;
import arden.java.kudago.service.SuitableEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({EventController.class})
public class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SuitableEventService<Mono<List<EventResponse>>, Mono<List<SuitableEvent>>> suitableEventService;

    @MockBean
    private EventService eventService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testGetEventsSuccess() throws Exception {
        List<EventDto> events = List.of(
                new EventDto("Party", OffsetDateTime.now(), 1L),
                new EventDto("Concert", OffsetDateTime.now(), 1L)
        );

        Page<EventDto> eventPage = new PageImpl<>(events, PageRequest.of(0, events.size()), events.size());
        when(eventService.getAllEvents(ArgumentMatchers.any())).thenReturn(eventPage);

        mockMvc.perform(get("/api/v1/events/list")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.size()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Party"));
    }

    @Test
    void testGetEventByIdSuccess() throws Exception {
        EventDto eventDto = new EventDto("Party", OffsetDateTime.now(), 1L);
        when(eventService.getEventById(1L)).thenReturn(eventDto);

        mockMvc.perform(get("/api/v1/events/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Party"));
    }

    @Test
    void testGetEventBySlugNotFound() throws Exception {
        when(eventService.getEventById(anyLong())).thenThrow(new IdNotFoundException("Event not found"));

        mockMvc.perform(get("/api/v1/events/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateEventSuccess() throws Exception {
        EventDto newEvent = new EventDto("Party", OffsetDateTime.now(), 1L);

        when(eventService.createEvent(any(EventDto.class))).thenReturn(newEvent);

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEvent)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Party"));
    }

    @Test
    void testCreateEventInvalidData() throws Exception {
        EventDto invalidEventResponse = new EventDto(null, OffsetDateTime.now(), 1L);
        when(eventService.createEvent(any(EventDto.class))).thenThrow(new CreationObjectException("Can't create an object"));

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEventResponse)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateEventSuccess() throws Exception {
        EventDto updatedEvent = new EventDto("Party", OffsetDateTime.now(), 1L);

        when(eventService.updateEvent(anyLong(), any(EventDto.class))).thenReturn(updatedEvent);

        mockMvc.perform(put("/api/v1/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEvent)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Party"));
    }

    @Test
    void testUpdateEventNotFound() throws Exception {
        when(eventService.updateEvent(anyLong(), any(EventDto.class)))
                .thenThrow(new IdNotFoundException("Event not found"));

        mockMvc.perform(put("/api/v1/events/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new EventDto("Party", OffsetDateTime.now(), 1L))))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteEventSuccess() throws Exception {
        doNothing().when(eventService).deleteEvent(1L);

        mockMvc.perform(delete("/api/v1/events/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));
    }

    @Test
    void testDeleteEventNotFound() throws Exception {
        doThrow(new IdNotFoundException("Event not found")).when(eventService).deleteEvent(anyLong());

        mockMvc.perform(delete("/api/v1/events/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFilterByName() throws Exception {
        List<EventDto> events = List.of(
                new EventDto("Party", OffsetDateTime.now(), 1L),
                new EventDto("Concert", OffsetDateTime.now(), 1L)
        );

        Page<EventDto> eventPage = new PageImpl<>(List.of(events.getFirst()), PageRequest.of(0, 1), 1);
        when(eventService.getEventsByFilter(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(eventPage);

        mockMvc.perform(get("/api/v1/events/filter")
                        .param("name", "Party"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Party"));

    }

    @Test
    void testGetSuitableEventsSuccess() throws Exception {
        Mono<List<SuitableEvent>> events = Mono.just(
                List.of(
                        new SuitableEvent(1L,
                                List.of(new SuitableEvent.ConvertedDates(LocalDate.now(), LocalDate.now().plusWeeks(1))),
                                "Фестиваль осени",
                                "Какой-то классный фестиваль",
                                "https://festival.ru",
                                "400.0",
                                30L)
                )
        );

        when(suitableEventService.getSuitableEvents(100D, "USD", null, null)).thenReturn(events);

        mockMvc.perform(get("/api/v1/events/suitable")
                        .param("budget", "100")
                        .param("currency", "USD")
                        .param("amount", "400.0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Фестиваль осени"));
    }
}
