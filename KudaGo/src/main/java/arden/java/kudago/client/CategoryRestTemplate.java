package arden.java.kudago.client;

import arden.java.kudago.config.UrlConfig;
import arden.java.kudago.dto.response.places.CategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CategoryRestTemplate {
    private final RestTemplate restTemplate;
    private final UrlConfig urlConfig;

    public Optional<List<CategoryDto>> getAllCategories() {
        ResponseEntity<List<CategoryDto>> response = restTemplate
                .exchange(urlConfig.kudaGoUrl() + "/place-categories",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        });

        return Optional.ofNullable(response.getBody());
    }
}
