package arden.java.kudago.service;

import arden.java.kudago.dto.response.places.CategoryResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryById(Long id);

    CategoryResponse createCategory(CategoryResponse categoryResponse);

    CategoryResponse updateCategory(Long id, CategoryResponse categoryResponse);

    boolean deleteCategory(Long id);
}
