package com.davon.library.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record BookResponseDTO(
        Long id,
        String title,
        String isbn,
        Integer publicationYear,
        String description,
        String coverImageUrl,
        Integer pages,
        PublisherResponseDTO publisher,
        CategoryResponseDTO category,
        Set<AuthorResponseDTO> authors,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
