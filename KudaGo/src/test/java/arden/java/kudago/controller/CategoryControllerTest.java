package arden.java.kudago.controller;

import arden.java.kudago.dto.Category;
import arden.java.kudago.exception.CreationObjectException;
import arden.java.kudago.exception.IdNotFoundException;
import arden.java.kudago.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetCategoriesSuccess() throws Exception {
        List<Category> categories = List.of(
                new Category(1L, "cafe", "Кафе быстрого питания"),
                new Category(2L, "shop", "Магазин одежды")
        );

        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/v1/places/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].slug").value("cafe"));
    }

    @Test
    void testGetCategoryByIdSuccess() throws Exception {
        Category category = new Category(1L, "cafe", "Кафе быстрого питания");
        when(categoryService.getCategoryById(1L)).thenReturn(category);

        mockMvc.perform(get("/api/v1/places/categories/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.slug").value("cafe"));
    }

    @Test
    void testGetCategoryByIdNotFound() throws Exception {
        when(categoryService.getCategoryById(anyLong())).thenThrow(new IdNotFoundException("Category not found"));

        mockMvc.perform(get("/api/v1/places/categories/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateCategorySuccess() throws Exception {
        Category newCategory = new Category(null, "museum", "Музей воды");
        Category createdCategory = new Category(1L, "museum", "Музей воды");

        when(categoryService.createCategory(any(Category.class))).thenReturn(createdCategory);

        mockMvc.perform(post("/api/v1/places/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.slug").value("museum"));
    }

    @Test
    void testCreateCategoryInvalidData() throws Exception {
        Category invalidCategory = new Category(null, "", "");
        when(categoryService.createCategory(any(Category.class))).thenThrow(new CreationObjectException("Can't create an object"));

        mockMvc.perform(post("/api/v1/places/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCategory)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateCategorySuccess() throws Exception {
        Category updatedCategory = new Category(1L, "restaurant", "Restaurant");

        when(categoryService.updateCategory(anyLong(), any(Category.class))).thenReturn(updatedCategory);

        mockMvc.perform(put("/api/v1/places/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCategory)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.slug").value("restaurant"));
    }

    @Test
    void testUpdateCategoryNotFound() throws Exception {
        when(categoryService.updateCategory(anyLong(), any(Category.class)))
                .thenThrow(new IdNotFoundException("Category not found"));

        mockMvc.perform(put("/api/v1/places/categories/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Category(999L, "unknown", "Unknown"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteCategorySuccess() throws Exception {
        when(categoryService.deleteCategory(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/places/categories/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));
    }

    @Test
    void testDeleteCategoryNotFound() throws Exception {
        when(categoryService.deleteCategory(anyLong())).thenThrow(new IdNotFoundException("Category not found"));

        mockMvc.perform(delete("/api/v1/places/categories/999"))
                .andExpect(status().isNotFound());
    }
}
