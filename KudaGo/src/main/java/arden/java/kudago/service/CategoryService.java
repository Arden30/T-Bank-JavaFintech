package arden.java.kudago.service;

import arden.java.kudago.dto.response.places.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();

    Category getCategoryById(Long id);

    Category createCategory(Category category);

    Category updateCategory(Long id, Category category);

    boolean deleteCategory(Long id);
}
