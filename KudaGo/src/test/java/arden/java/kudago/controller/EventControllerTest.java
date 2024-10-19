package arden.java.kudago.controller;

import arden.java.kudago.dto.response.event.Event;
import arden.java.kudago.dto.response.event.EventResponse;
import arden.java.kudago.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({EventController.class})
public class EventControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private EventService<Mono<List<EventResponse>>, Mono<List<Event>>> eventService;

    @Test
    void testGetEventsSuccess() throws Exception {
        Mono<List<Event>> events = Mono.just(
                List.of(
                        new Event(1L,
                                List.of(new Event.ConvertedDates(LocalDate.now(), LocalDate.now().plusWeeks(1))),
                                "Фестиваль осени",
                                "Какой-то классный фестиваль",
                                "https://festival.ru",
                                "400.0",
                                30L)
                )
        );

        when(eventService.getSuitableEvents(100D, "USD", null, null)).thenReturn(events);

        mvc.perform(get("/api/v1/events")
                        .param("budget", "100")
                        .param("currency", "USD")
                        .param("amount", "400.0"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Фестиваль осени"));
    }
}
