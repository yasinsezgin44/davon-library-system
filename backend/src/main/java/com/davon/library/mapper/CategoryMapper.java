package com.davon.library.mapper;

import com.davon.library.dto.CategoryResponseDTO;
import com.davon.library.model.Category;

public class CategoryMapper {

    public static CategoryResponseDTO toResponseDTO(Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
