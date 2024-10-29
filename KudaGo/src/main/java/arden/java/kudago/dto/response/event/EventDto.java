package arden.java.kudago.dto.response.event;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record EventDto(
        String name,
        OffsetDateTime date,
        Long locationId
) {
}
