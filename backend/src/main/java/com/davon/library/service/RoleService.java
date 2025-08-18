package com.davon.library.service;

import com.davon.library.model.Role;
import com.davon.library.repository.RoleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class RoleService {

    @Inject
    RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.listAll();
    }
}
