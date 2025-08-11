package com.davon.library.dto;

import com.davon.library.model.enums.LoanStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record LoanRequestDTO(
        @NotNull(message = "Member ID is required")
        Long memberId,

        @NotNull(message = "Book Copy ID is required")
        Long bookCopyId,

        LocalDate checkoutDate,

        LocalDate dueDate,

        LocalDate returnDate,

        LoanStatus status,

        Integer renewalCount
) {}
