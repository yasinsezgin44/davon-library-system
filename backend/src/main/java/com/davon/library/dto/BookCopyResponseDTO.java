package com.davon.library.dto;

import com.davon.library.model.enums.CopyStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookCopyResponseDTO(
        Long id,
        ShallowBookResponseDTO book,
        LocalDate acquisitionDate,
        String condition,
        CopyStatus status,
        String location,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
