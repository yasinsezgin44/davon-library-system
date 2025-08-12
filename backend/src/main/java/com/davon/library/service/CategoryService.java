package com.davon.library.service;

import com.davon.library.model.Category;
import com.davon.library.repository.CategoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class CategoryService {

    @Inject
    CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.listAll();
    }
}
