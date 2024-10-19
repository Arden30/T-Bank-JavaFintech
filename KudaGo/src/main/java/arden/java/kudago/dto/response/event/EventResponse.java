package arden.java.kudago.dto.response.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record EventResponse(Long id,
                            List<Dates> dates,
                            String title,
                            String description,
                            @JsonProperty("site_url")
                           String siteUrl,
                            String price,
                            @JsonProperty("favorites_count")
                           Long favoritesCount) {

    public record Dates(
            Long start,
            Long end
    ) {
    }
}
