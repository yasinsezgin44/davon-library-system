package com.davon.library.service;

import com.davon.library.model.Category;
import com.davon.library.repository.CategoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class CategoryService {

    @Inject
    CategoryRepository categoryRepository;

    @Transactional
    public Category createCategory(Category category) {
        categoryRepository.persist(category);
        return category;
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.listAll();
    }
}
