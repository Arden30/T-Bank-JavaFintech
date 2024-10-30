package arden.java.kudago.service.impl;

import arden.java.kudago.dto.response.places.CategoryDto;
import arden.java.kudago.exception.IdNotFoundException;
import arden.java.kudago.model.Category;
import arden.java.kudago.repository.CategoryRepository;
import arden.java.kudago.repository.HistoryCaretaker;
import arden.java.kudago.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final Map<Long, HistoryCaretaker<Category.CategoryMemento>> categoryHistory = new HashMap<>();

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
        Category category = createCategoryFromResponse(categoryDto);
        saveCategorySnapshot(category);

        return createResponseFromCategory(categoryRepository.save(category));
    }

    @Override
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new IdNotFoundException("Category with id = " + id + " not found"));
        saveCategorySnapshot(existingCategory);

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
        category.setId(categoryDto.id());
        category.setSlug(categoryDto.slug());
        category.setName(categoryDto.name());

        return category;
    }

    private void saveCategorySnapshot(Category category) {
        var memento = category.save();

        categoryHistory
                .computeIfAbsent(category.getId(), k -> new HistoryCaretaker<>())
                .save(memento);
    }

    public Category restoreCategorySnapshot(Long categoryId, int snapshotIndex) {
        var history = categoryHistory.get(categoryId);
        if (history == null) {
            throw new IllegalArgumentException("History wasn't found");
        }

        var memento = history.undo(snapshotIndex);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IdNotFoundException("Category with id = " + categoryId + " not found"));
        category.restore(memento);

        return categoryRepository.save(category);
    }
}
