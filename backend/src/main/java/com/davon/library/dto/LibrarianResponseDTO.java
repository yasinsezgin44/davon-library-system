package com.davon.library.dto;

import java.time.LocalDate;

public record LibrarianResponseDTO(
        Long id,
        UserResponseDTO user,
        LocalDate employmentDate,
        String employeeId) {
}
