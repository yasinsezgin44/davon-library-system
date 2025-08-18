package com.davon.library.dto;

import java.time.LocalDate;

public record BookResponseDTO(
                Long id,
                String title,
                String authorName,
                String isbn,
                String publisher,
                LocalDate publicationDate,
                String genre,
                String language,
                String coverImageUrl) {
}
