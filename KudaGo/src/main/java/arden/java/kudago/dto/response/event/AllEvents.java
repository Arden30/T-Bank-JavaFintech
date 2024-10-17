package arden.java.kudago.dto.response.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AllEvents(
        @JsonProperty("results")
        List<EventResponse> eventResponses
) {
}
