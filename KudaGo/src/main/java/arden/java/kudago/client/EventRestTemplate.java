package arden.java.kudago.client;

import arden.java.kudago.config.UrlConfig;
import arden.java.kudago.dto.response.event.AllEvents;
import arden.java.kudago.dto.response.event.EventResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class EventRestTemplate {
    private final RestTemplate restTemplate;
    private final UrlConfig urlConfig;

    public Optional<List<EventResponse>> getEvents(long start, long end) {
        String url = UriComponentsBuilder.fromHttpUrl(urlConfig.kudaGoUrl() + "/events")
                .queryParam("expand", "locationDto")
                .queryParam("actual_since", start)
                .queryParam("actual_until", end)
                .queryParam("fields", "id,dates,title,slug,description,site_url,price,favorites_count,locationDto")
                .toUriString();

        ResponseEntity<AllEvents> response = restTemplate
                .exchange(url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        });

        return Optional.ofNullable(response.getBody().eventResponses());
    }
}
