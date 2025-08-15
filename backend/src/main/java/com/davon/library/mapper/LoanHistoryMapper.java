package com.davon.library.mapper;

import com.davon.library.dto.LoanHistoryResponseDTO;
import com.davon.library.model.LoanHistory;

public class LoanHistoryMapper {

    public static LoanHistoryResponseDTO toResponseDTO(LoanHistory loanHistory) {
        if (loanHistory == null) {
            return null;
        }
        return new LoanHistoryResponseDTO(
                loanHistory.getId(),
                MemberMapper.toResponseDTO(loanHistory.getMember()),
                LoanMapper.toResponseDTO(loanHistory.getLoan()),
                BookMapper.toResponseDTO(loanHistory.getBook()),
                loanHistory.getAction(),
                loanHistory.getActionDate(),
                loanHistory.getCreatedAt(),
                loanHistory.getUpdatedAt());
    }
}
