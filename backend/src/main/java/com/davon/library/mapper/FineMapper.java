package com.davon.library.mapper;

import com.davon.library.dto.FineRequestDTO;
import com.davon.library.dto.FineResponseDTO;
import com.davon.library.model.Fine;
import com.davon.library.model.Loan;
import com.davon.library.model.Member;

public class FineMapper {

    public static Fine toEntity(FineRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Fine fine = new Fine();
        fine.setAmount(dto.amount());
        fine.setReason(dto.reason());
        fine.setIssueDate(dto.issueDate());
        fine.setDueDate(dto.dueDate());
        fine.setStatus(dto.status());

        if (dto.memberId() != null) {
            Member member = new Member();
            member.setId(dto.memberId());
            fine.setMember(member);
        }

        if (dto.loanId() != null) {
            Loan loan = new Loan();
            loan.setId(dto.loanId());
            fine.setLoan(loan);
        }

        return fine;
    }

    public static FineResponseDTO toResponseDTO(Fine fine) {
        if (fine == null) {
            return null;
        }
        return new FineResponseDTO(
                fine.getId(),
                MemberMapper.toResponseDTO(fine.getMember()),
                LoanMapper.toResponseDTO(fine.getLoan()),
                fine.getAmount(),
                fine.getReason(),
                fine.getIssueDate(),
                fine.getDueDate(),
                fine.getStatus(),
                fine.getCreatedAt(),
                fine.getUpdatedAt());
    }
}
