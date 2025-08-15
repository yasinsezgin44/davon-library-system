package com.davon.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record BookRequestDTO(
                @NotBlank(message = "Title is required") @Size(max = 255, message = "Title must not exceed 255 characters") String title,

                @NotBlank(message = "ISBN is required") @Size(min = 13, max = 13, message = "ISBN must be 13 characters") String isbn,

                Integer publicationYear,

                String description,

                String coverImage,

                Integer pages,

                @NotNull(message = "Publisher ID is required") Long publisherId,

                @NotNull(message = "Category ID is required") Long categoryId,

                Set<Long> authorIds) {
}
