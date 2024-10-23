package arden.java.kudago.config;

import arden.java.kudago.dto.response.places.CategoryResponse;
import arden.java.kudago.dto.response.places.LocationResponse;
import arden.java.kudago.repository.StorageRepository;
import arden.java.kudago.repository.impl.StorageRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfiguration {
    @Bean
    public StorageRepository<Long, CategoryResponse> categoryRepository() {
        return new StorageRepositoryImpl<>();
    }
}
