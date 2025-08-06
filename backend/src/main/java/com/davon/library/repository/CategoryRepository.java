package com.davon.library.repository;

import com.davon.library.model.Category;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

public interface CategoryRepository extends PanacheRepository<Category> {
}
