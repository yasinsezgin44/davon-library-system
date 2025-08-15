package com.davon.library.dto;

import com.davon.library.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionResponseDTO(
        Long id,
        MemberResponseDTO member,
        FineResponseDTO fine,
        LocalDate date,
        BigDecimal amount,
        TransactionType type,
        String description,
        String paymentMethod,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
