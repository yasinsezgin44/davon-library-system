package com.davon.library.service;

import com.davon.library.model.Category;
import com.davon.library.repository.CategoryRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@QuarkusTest
class CategoryServiceTest {

    @Inject
    CategoryService categoryService;

    @InjectMock
    CategoryRepository categoryRepository;

    @Test
    void getAllCategories_shouldReturnListOfCategories() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        when(categoryRepository.listAll()).thenReturn(Collections.singletonList(category));

        List<Category> categories = categoryService.getAllCategories();

        assertEquals(1, categories.size());
        assertEquals("Test Category", categories.get(0).getName());
    }
}
