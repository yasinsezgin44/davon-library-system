package com.davon.library.mapper;

import com.davon.library.dto.TransactionResponseDTO;
import com.davon.library.model.Transaction;

public class TransactionMapper {

    public static TransactionResponseDTO toResponseDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        return new TransactionResponseDTO(
                transaction.getId(),
                MemberMapper.toResponseDTO(transaction.getMember()),
                FineMapper.toResponseDTO(transaction.getFine()),
                transaction.getDate(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getDescription(),
                transaction.getPaymentMethod(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt());
    }
}
