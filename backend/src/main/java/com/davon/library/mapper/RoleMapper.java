package com.davon.library.mapper;

import com.davon.library.dto.RoleResponseDTO;
import com.davon.library.model.Role;

public class RoleMapper {

    public static RoleResponseDTO toResponseDTO(Role role) {
        if (role == null) {
            return null;
        }
        return new RoleResponseDTO(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
    }
}
