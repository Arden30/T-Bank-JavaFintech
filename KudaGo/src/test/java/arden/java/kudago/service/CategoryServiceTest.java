package arden.java.kudago.service;

import arden.java.kudago.dto.response.places.CategoryDto;
import arden.java.kudago.exception.IdNotFoundException;
import arden.java.kudago.model.Category;
import arden.java.kudago.repository.CategoryRepository;
import arden.java.kudago.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository storage;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    private final List<Optional<Category>> categories = List.of(
            Optional.of(new Category(1L, "shop", "Магазин здорового питания")),
            Optional.of(new Category(2L, "cafe", "Кафе быстрого питания"))
    );

    private final List<CategoryDto> categoriesList = List.of(
            new CategoryDto(1L, "Магазин здорового питания", "shop"),
            new CategoryDto(2L, "Кафе быстрого питания", "cafe")
    );

    @Test
    @DisplayName("Getting all categories: success test")
    public void getAllCategories_successTest() {
        //Arrange
        when(storage.findAll()).thenReturn(categories.stream().map(Optional::get).toList());

        //Act
        List<CategoryDto> categories = categoryService.getAllCategories();

        //Assert
        assertThat(categories).isEqualTo(categoriesList);
    }

    @Test
    @DisplayName("Getting all categories: fail test")
    public void getAllCategories_failTest() {
        when(storage.findAll()).thenReturn(Collections.emptyList());

        List<CategoryDto> categories = categoryService.getAllCategories();

        assertThat(Collections.emptyList()).isEqualTo(categories);
    }

    @Test
    @DisplayName("Getting category by id: success test")
    public void getCategoryById_successTest() {
        when(storage.findById(1L)).thenReturn(categories.getFirst());

        CategoryDto categoryDto = categoryService.getCategoryById(1L);

        assertThat(categoryDto).isEqualTo(categoriesList.getFirst());
    }

    @Test
    @DisplayName("Getting category by id: fail test")
    public void getCategoryById_failTest() {
        when(storage.findById(1L)).thenThrow(new IdNotFoundException("Id not found"));

        assertThrows(IdNotFoundException.class, () -> categoryService.getCategoryById(1L));
    }

    @Test
    @DisplayName("Create new category: success test")
    public void createCategory_successTest() {
        when(storage.save(any(Category.class))).thenReturn(categories.getFirst().get());

        CategoryDto categoryDto = categoryService.createCategory(categoriesList.getFirst());

        assertThat(categoryDto).isEqualTo(categoriesList.getFirst());
    }

    @Test
    @DisplayName("Update category: success test")
    public void updateCategory_successTest() {
        when(storage.save(any(Category.class))).thenReturn(categories.getLast().get());
        when(storage.findById(1L)).thenReturn(categories.getFirst());

        CategoryDto categoryDto = categoryService.updateCategory(1L, categoriesList.getLast());

        assertThat(categoryDto).isEqualTo(categoriesList.getLast());
    }

    @Test
    @DisplayName("Update category: fail test")
    public void updateCategory_failTest() {
        assertThrows(IdNotFoundException.class, () -> categoryService.updateCategory(1L, categoriesList.getLast()));
    }

    @Test
    @DisplayName("Delete category: success test")
    public void deleteCategory_successTest() {
        when(storage.findById(1L)).thenReturn(categories.getFirst());
        assertDoesNotThrow(() -> categoryService.deleteCategory(categories.getFirst().get().getId()));
    }
}
