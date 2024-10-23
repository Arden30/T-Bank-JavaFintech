package arden.java.kudago.service.impl;

import arden.java.kudago.dto.response.places.CategoryResponse;
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
    private final StorageRepository<Long, CategoryResponse> categoryRepository;

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.readAll();
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        if (categoryRepository.read(id) == null) {
            throw new IdNotFoundException("Category with id = " + id + " not found");
        }

        return categoryRepository.read(id);
    }

    @Override
    public CategoryResponse createCategory(CategoryResponse categoryResponse) {
        if (categoryRepository.create(categoryResponse.id(), categoryResponse) == null) {
            throw new CreationObjectException("Could not create category, because your input format is wrong, check again");
        }

        return categoryRepository.create(categoryResponse.id(), categoryResponse);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryResponse categoryResponse) {
        if (categoryRepository.read(id) == null) {
            throw new IdNotFoundException("Category with id = " + id + " not found");
        }

        if (categoryRepository.update(id, categoryResponse) == null) {
            throw new CreationObjectException("Could not update category, because your input format is wrong, check again");
        }

        return categoryRepository.update(id, categoryResponse);
    }

    @Override
    public boolean deleteCategory(Long id) {
        if (categoryRepository.read(id) == null || !categoryRepository.delete(id)) {
            throw new IdNotFoundException("Category with id = " + id + " not found");
        }

        return categoryRepository.delete(id);
    }
}
