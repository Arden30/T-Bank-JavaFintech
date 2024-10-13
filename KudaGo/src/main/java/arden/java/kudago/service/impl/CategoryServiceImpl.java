package arden.java.kudago.service.impl;

import arden.java.kudago.dto.Category;
import arden.java.kudago.exception.CreationObjectException;
import arden.java.kudago.exception.IdNotFoundException;
import arden.java.kudago.repository.StorageRepository;
import arden.java.kudago.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final StorageRepository<Long, Category> categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.readAll();
    }

    @Override
    public Category getCategoryById(Long id) {
        if (categoryRepository.read(id) == null) {
            throw new IdNotFoundException("Category with id = " + id + " not found");
        }

        return categoryRepository.read(id);
    }

    @Override
    public Category createCategory(Category category) {
        if (categoryRepository.create(category.id(), category) == null) {
            throw new CreationObjectException("Could not create category, because your input format is wrong, check again");
        }

        return categoryRepository.create(category.id(), category);
    }

    @Override
    public Category updateCategory(Long id, Category category) {
        if (categoryRepository.read(id) == null) {
            throw new IdNotFoundException("Category with id = " + id + " not found");
        }

        if (categoryRepository.update(id, category) == null) {
            throw new CreationObjectException("Could not update category, because your input format is wrong, check again");
        }

        return categoryRepository.update(id, category);
    }

    @Override
    public boolean deleteCategory(Long id) {
        if (categoryRepository.read(id) == null || !categoryRepository.delete(id)) {
            throw new IdNotFoundException("Category with id = " + id + " not found");
        }

        return categoryRepository.delete(id);
    }
}
