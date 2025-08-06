package com.davon.library.repository;

import com.davon.library.model.Author;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

public interface AuthorRepository extends PanacheRepository<Author> {
}
