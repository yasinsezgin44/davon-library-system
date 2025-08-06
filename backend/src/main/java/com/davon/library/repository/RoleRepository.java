package com.davon.library.repository;

import com.davon.library.model.Role;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public interface RoleRepository extends PanacheRepository<Role> {
}
