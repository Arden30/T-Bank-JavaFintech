package arden.java.kudago.service;

import arden.java.kudago.dto.response.places.CategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllCategories();

    CategoryDto getCategoryById(Long id);

    CategoryDto createCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(Long id, CategoryDto categoryDto);

    boolean deleteCategory(Long id);
}
