package com.davon.library.mapper;

import com.davon.library.dto.ReceiptResponseDTO;
import com.davon.library.model.Receipt;

public class ReceiptMapper {

    public static ReceiptResponseDTO toResponseDTO(Receipt receipt) {
        if (receipt == null) {
            return null;
        }
        return new ReceiptResponseDTO(
                receipt.getId(),
                TransactionMapper.toResponseDTO(receipt.getTransaction()),
                receipt.getIssueDate(),
                receipt.getItems(),
                receipt.getTotal(),
                receipt.getCreatedAt(),
                receipt.getUpdatedAt());
    }
}
