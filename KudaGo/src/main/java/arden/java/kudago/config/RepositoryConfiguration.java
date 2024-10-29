package arden.java.kudago.config;

import arden.java.kudago.dto.response.places.CategoryDto;
import arden.java.kudago.repository.StorageRepository;
import arden.java.kudago.repository.impl.StorageRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfiguration {
    @Bean
    public StorageRepository<Long, CategoryDto> categoryRepository() {
        return new StorageRepositoryImpl<>();
    }
}
