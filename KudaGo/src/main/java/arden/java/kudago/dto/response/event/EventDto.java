package arden.java.kudago.dto.response.event;

import arden.java.kudago.model.Location;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record EventDto(
        String name,
        OffsetDateTime date,
        Location location
) {
}
