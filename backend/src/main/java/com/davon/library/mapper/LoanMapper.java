package com.davon.library.mapper;

import com.davon.library.dto.LoanRequestDTO;
import com.davon.library.dto.LoanResponseDTO;
import com.davon.library.model.BookCopy;
import com.davon.library.model.Loan;
import com.davon.library.model.Member;

public class LoanMapper {

    public static Loan toEntity(LoanRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Loan loan = new Loan();
        loan.setCheckoutDate(dto.checkoutDate());
        loan.setDueDate(dto.dueDate());
        loan.setReturnDate(dto.returnDate());
        loan.setStatus(dto.status());
        loan.setRenewalCount(dto.renewalCount());

        if (dto.memberId() != null) {
            Member member = new Member();
            member.setId(dto.memberId());
            loan.setMember(member);
        }

        if (dto.bookCopyId() != null) {
            BookCopy bookCopy = new BookCopy();
            bookCopy.setId(dto.bookCopyId());
            loan.setBookCopy(bookCopy);
        }

        return loan;
    }

    public static LoanResponseDTO toResponseDTO(Loan loan) {
        if (loan == null) {
            return null;
        }
        return new LoanResponseDTO(
                loan.getId(),
                MemberMapper.toResponseDTO(loan.getMember()),
                BookMapper.toResponseDTO(loan.getBookCopy().getBook(), false),
                loan.getCheckoutDate(),
                loan.getDueDate(),
                loan.getReturnDate(),
                loan.getStatus(),
                loan.getRenewalCount(),
                loan.getCreatedAt(),
                loan.getUpdatedAt());
    }
}
