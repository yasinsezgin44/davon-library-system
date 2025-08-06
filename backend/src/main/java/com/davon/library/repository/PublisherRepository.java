package com.davon.library.repository;

import com.davon.library.model.Publisher;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface PublisherRepository extends PanacheRepository<Publisher> {
}
