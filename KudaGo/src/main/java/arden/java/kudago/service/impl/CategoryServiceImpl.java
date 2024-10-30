package arden.java.kudago.service.impl;

import arden.java.kudago.dto.response.places.CategoryDto;
import arden.java.kudago.exception.IdNotFoundException;
import arden.java.kudago.model.Category;
import arden.java.kudago.repository.CategoryRepository;
import arden.java.kudago.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::createResponseFromCategory)
                .toList();
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        return createResponseFromCategory(categoryRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Category with id = " + id + " not found")));
    }

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        return createResponseFromCategory(categoryRepository.save(createCategoryFromResponse(categoryDto)));
    }

    @Override
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Category with id = " + id + " not found"));

        existingCategory.setName(categoryDto.name());
        existingCategory.setSlug(categoryDto.slug());

        return createResponseFromCategory(categoryRepository.save(existingCategory));
    }

    @Override
    public void deleteCategory(Long id) {
        if (categoryRepository.findById(id).isEmpty()) {
            throw new IdNotFoundException("Category with id = " + id + " not found");
        }

        categoryRepository.deleteById(id);
    }

    public CategoryDto createResponseFromCategory(Category category) {
        return new CategoryDto(category.getId(), category.getSlug(), category.getName());
    }

    public Category createCategoryFromResponse(CategoryDto categoryDto) {
        Category category = new Category();
        category.setId(category.getId());
        category.setSlug(category.getSlug());
        category.setName(category.getName());

        return category;
    }
}
