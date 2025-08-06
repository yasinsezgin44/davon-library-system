package com.davon.library.service;

import com.davon.library.model.Category;
import com.davon.library.repository.CategoryRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class CategoryServiceTest {

    @Inject
    CategoryService categoryService;

    @Inject
    CategoryRepository categoryRepository;

    @BeforeEach
    @Transactional
    void setUp() {
        categoryRepository.deleteAll();
    }

    @Test
    @Transactional
    void testCreateCategory() {
        Category category = new Category();
        category.setName("Test Category");
        categoryService.createCategory(category);
        assertNotNull(category.getId());
        assertEquals(1, categoryRepository.count());
    }

    @Test
    @Transactional
    void testGetCategoryById() {
        Category category = new Category();
        category.setName("Test Category");
        categoryRepository.persist(category);

        Category found = categoryService.getCategoryById(category.getId());
        assertNotNull(found);
        assertEquals("Test Category", found.getName());
    }
}
