package com.davon.library.dto;

import java.time.LocalDateTime;
import java.util.List;

public record BookResponseDTO(
                Long id,
                String title,
                String isbn,
                String description,
                Integer publicationYear,
                Integer pages,
                String coverImageUrl,
                PublisherDTO publisher,
                CategoryDTO category,
                List<AuthorDTO> authors,
                List<BookCopyResponseDTO> copies,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
}
