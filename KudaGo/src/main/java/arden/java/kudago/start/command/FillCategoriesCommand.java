package arden.java.kudago.start.command;

import arden.java.kudago.dto.response.places.CategoryDto;
import arden.java.kudago.repository.StorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FillCategoriesCommand implements Command<CategoryDto> {
    private final StorageRepository<Long, CategoryDto> categoryRepository;

    @Override
    public void execute(List<CategoryDto> categories) {
        categories.forEach(categoryDto -> categoryRepository.create(categoryDto.id(), categoryDto));
    }
}
