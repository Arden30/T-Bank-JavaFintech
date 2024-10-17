package arden.java.kudago.dto.response.event;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record Event(
        Long id,
        List<ConvertedDates> dates,
        String title,
        String description,
        String siteUrl,
        String price,
        Long favoritesCount
) {
    @Builder
    public record ConvertedDates(
            LocalDate start,
            LocalDate end
    ) {
    }
}
