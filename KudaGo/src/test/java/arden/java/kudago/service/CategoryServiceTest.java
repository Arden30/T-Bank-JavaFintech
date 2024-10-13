package arden.java.kudago.service;

import arden.java.kudago.dto.Category;
import arden.java.kudago.exception.CreationObjectException;
import arden.java.kudago.exception.IdNotFoundException;
import arden.java.kudago.repository.StorageRepository;
import arden.java.kudago.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private StorageRepository<Long, Category> storage;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    private final List<Category> categoriesList = List.of(
            new Category(1L, "shop", "Магазин здорового питания"),
            new Category(2L, "cafe", "Кафе быстрого питания")
    );

    @Test
    @DisplayName("Getting all categories: success test")
    public void getAllCategories_successTest() {
        //Arrange
        when(storage.readAll()).thenReturn(categoriesList);

        //Act
        List<Category> categories = categoryService.getAllCategories();

        //Assert
        assertThat(categories).isEqualTo(categoriesList);
    }

    @Test
    @DisplayName("Getting all categories: fail test")
    public void getAllCategories_failTest() {
        when(storage.readAll()).thenReturn(Collections.emptyList());

        List<Category> categories = categoryService.getAllCategories();

        assertThat(Collections.emptyList()).isEqualTo(categories);
    }

    @Test
    @DisplayName("Getting category by id: success test")
    public void getCategoryById_successTest() {
        when(storage.read(1L)).thenReturn(categoriesList.getFirst());

        Category category = categoryService.getCategoryById(1L);

        assertThat(category).isEqualTo(categoriesList.getFirst());
    }

    @Test
    @DisplayName("Getting category by id: fail test")
    public void getCategoryById_failTest() {
        when(storage.read(1L)).thenReturn(null);

        assertThrows(IdNotFoundException.class, () -> categoryService.getCategoryById(1L));
    }

    @Test
    @DisplayName("Create new category: success test")
    public void createCategory_successTest() {
        when(storage.create(1L, categoriesList.getFirst())).thenReturn(categoriesList.getFirst());

        Category category = categoryService.createCategory(categoriesList.getFirst());

        assertThat(category).isEqualTo(categoriesList.getFirst());
    }

    @Test
    @DisplayName("Create new category: fail test")
    public void createCategory_failTest() {
        when(storage.create(1L, categoriesList.getFirst())).thenReturn(null);

        assertThrows(CreationObjectException.class, () -> categoryService.createCategory(categoriesList.getFirst()));
    }

    @Test
    @DisplayName("Update category: success test")
    public void updateCategory_successTest() {
        when(storage.update(1L, categoriesList.getLast())).thenReturn(categoriesList.getLast());
        when(storage.read(1L)).thenReturn(categoriesList.getFirst());

        Category category = categoryService.updateCategory(1L, categoriesList.getLast());

        assertThat(category).isEqualTo(categoriesList.getLast());
    }

    @Test
    @DisplayName("Update category: fail test")
    public void updateCategory_failTest() {
        assertThrows(IdNotFoundException.class, () -> categoryService.updateCategory(1L, categoriesList.getLast()));
    }

    @Test
    @DisplayName("Delete category: success test")
    public void deleteCategory_successTest() {
        when(storage.read(1L)).thenReturn(categoriesList.getFirst());
        when(storage.delete(1L)).thenReturn(true);

        boolean isDeleted = categoryService.deleteCategory(1L);

        assertThat(isDeleted).isTrue();
    }

    @Test
    @DisplayName("Delete category: fail test")
    public void deleteCategory_failTest() {
        assertThrows(IdNotFoundException.class, () -> categoryService.deleteCategory(categoriesList.getFirst().id()));
    }
}
