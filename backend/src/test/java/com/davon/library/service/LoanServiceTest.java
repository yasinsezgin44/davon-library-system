package com.davon.library.service;

import com.davon.library.model.BookCopy;
import com.davon.library.model.Fine;
import com.davon.library.model.Loan;
import com.davon.library.model.Member;
import com.davon.library.model.enums.CopyStatus;
import com.davon.library.model.enums.LoanStatus;
import com.davon.library.repository.BookCopyRepository;
import com.davon.library.repository.FineRepository;
import com.davon.library.repository.LoanRepository;
import com.davon.library.repository.MemberRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@QuarkusTest
class LoanServiceTest {

    @Inject
    LoanService loanService;

    @InjectMock
    LoanRepository loanRepository;

    @InjectMock
    MemberRepository memberRepository;

    @InjectMock
    BookCopyRepository bookCopyRepository;

    @InjectMock
    FineRepository fineRepository;

    private Member member;
    private BookCopy bookCopy;
    private Loan loan;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(1L);
        member.setFineBalance(BigDecimal.ZERO);

        bookCopy = new BookCopy();
        bookCopy.setId(1L);
        bookCopy.setStatus(CopyStatus.AVAILABLE);

        loan = new Loan();
        loan.setId(1L);
        loan.setMember(member);
        loan.setBookCopy(bookCopy);
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setDueDate(LocalDate.now().plusDays(14));
    }

    @Test
    void testCheckoutBook() {
        when(memberRepository.findByIdOptional(1L)).thenReturn(Optional.of(member));
        when(loanRepository.countActiveLoansByMember(member)).thenReturn(0L);
        when(bookCopyRepository.findAvailableByBookId(anyLong())).thenReturn(Optional.of(bookCopy));

        Loan createdLoan = loanService.checkoutBook(1L, 1L);

        assertNotNull(createdLoan);
        assertEquals(CopyStatus.CHECKED_OUT, createdLoan.getBookCopy().getStatus());
        Mockito.verify(loanRepository).persist(any(Loan.class));
    }

    @Test
    void testCheckoutBook_memberNotFound() {
        when(memberRepository.findByIdOptional(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> loanService.checkoutBook(1L, 1L));
    }

    @Test
    void testCheckoutBook_maxLoansReached() {
        when(memberRepository.findByIdOptional(1L)).thenReturn(Optional.of(member));
        when(loanRepository.countActiveLoansByMember(member)).thenReturn(5L);
        assertThrows(BadRequestException.class, () -> loanService.checkoutBook(1L, 1L));
    }

    @Test
    void testReturnBook() {
        when(loanRepository.findByIdOptional(1L)).thenReturn(Optional.of(loan));
        loanService.returnBook(1L);
        assertEquals(LoanStatus.RETURNED, loan.getStatus());
        assertEquals(CopyStatus.AVAILABLE, loan.getBookCopy().getStatus());
    }

    @Test
    void testReturnBook_overdue() {
        loan.setDueDate(LocalDate.now().minusDays(1));
        when(loanRepository.findByIdOptional(1L)).thenReturn(Optional.of(loan));
        loanService.returnBook(1L);
        Mockito.verify(fineRepository).persist(any(Fine.class));
    }
}
