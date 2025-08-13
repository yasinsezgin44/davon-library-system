package com.davon.library.service;

import com.davon.library.model.BookCopy;
import com.davon.library.model.Fine;
import com.davon.library.model.Loan;
import com.davon.library.model.Member;
import com.davon.library.model.enums.CopyStatus;
import com.davon.library.model.enums.FineReason;
import com.davon.library.model.enums.FineStatus;
import com.davon.library.model.enums.LoanStatus;
import com.davon.library.repository.BookCopyRepository;
import com.davon.library.repository.FineRepository;
import com.davon.library.repository.LoanRepository;
import com.davon.library.repository.MemberRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
public class LoanServiceTest {

    @Inject
    LoanService loanService;

    @InjectMock
    LoanRepository loanRepository;

    @InjectMock
    BookCopyRepository bookCopyRepository;

    @InjectMock
    FineRepository fineRepository;

    @InjectMock
    MemberRepository memberRepository;

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
        loan.setDueDate(LocalDate.now().plusDays(14));
        loan.setStatus(LoanStatus.ACTIVE);
    }

    @Test
    void checkoutBook_Success() {
        when(memberRepository.findByIdOptional(anyLong())).thenReturn(Optional.of(member));
        when(loanRepository.countActiveLoansByMember(any(Member.class))).thenReturn(0L);
        when(bookCopyRepository.findAvailableByBookId(anyLong())).thenReturn(Optional.of(bookCopy));

        Loan result = loanService.checkoutBook(1L, 1L);

        assertNotNull(result);
        assertEquals(member, result.getMember());
        assertEquals(bookCopy, result.getBookCopy());
        assertEquals(LoanStatus.ACTIVE, result.getStatus());
        assertEquals(CopyStatus.CHECKED_OUT, bookCopy.getStatus());

        ArgumentCaptor<Loan> loanCaptor = ArgumentCaptor.forClass(Loan.class);
        verify(loanRepository).persist(loanCaptor.capture());
        assertEquals(member, loanCaptor.getValue().getMember());
    }

    @Test
    void checkoutBook_MemberNotFound() {
        when(memberRepository.findByIdOptional(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> loanService.checkoutBook(1L, 1L));
    }

    @Test
    void checkoutBook_MaxLoansReached() {
        when(memberRepository.findByIdOptional(anyLong())).thenReturn(Optional.of(member));
        when(loanRepository.countActiveLoansByMember(any(Member.class))).thenReturn(5L);
        assertThrows(BadRequestException.class, () -> loanService.checkoutBook(1L, 1L));
    }

    @Test
    void checkoutBook_HasOutstandingFines() {
        member.setFineBalance(new BigDecimal("10.00"));
        when(memberRepository.findByIdOptional(anyLong())).thenReturn(Optional.of(member));
        assertThrows(BadRequestException.class, () -> loanService.checkoutBook(1L, 1L));
    }

    @Test
    void checkoutBook_NoAvailableCopies() {
        when(memberRepository.findByIdOptional(anyLong())).thenReturn(Optional.of(member));
        when(loanRepository.countActiveLoansByMember(any(Member.class))).thenReturn(0L);
        when(bookCopyRepository.findAvailableByBookId(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> loanService.checkoutBook(1L, 1L));
    }

    @Test
    void returnBook_Success() {
        when(loanRepository.findByIdOptional(anyLong())).thenReturn(Optional.of(loan));
        loanService.returnBook(1L);

        assertEquals(LoanStatus.RETURNED, loan.getStatus());
        assertEquals(CopyStatus.AVAILABLE, bookCopy.getStatus());
    }

    @Test
    void returnBook_LoanNotFound() {
        when(loanRepository.findByIdOptional(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> loanService.returnBook(1L));
    }

    @Test
    void returnBook_LoanNotActive() {
        loan.setStatus(LoanStatus.RETURNED);
        when(loanRepository.findByIdOptional(anyLong())).thenReturn(Optional.of(loan));
        assertThrows(BadRequestException.class, () -> loanService.returnBook(1L));
    }

    @Test
    void returnBook_Overdue() {
        loan.setDueDate(LocalDate.now().minusDays(1));
        when(loanRepository.findByIdOptional(anyLong())).thenReturn(Optional.of(loan));

        loanService.returnBook(1L);

        assertEquals(LoanStatus.RETURNED, loan.getStatus());
        assertEquals(CopyStatus.AVAILABLE, bookCopy.getStatus());

        ArgumentCaptor<Fine> fineCaptor = ArgumentCaptor.forClass(Fine.class);
        verify(fineRepository).persist(fineCaptor.capture());

        Fine fine = fineCaptor.getValue();
        assertEquals(member, fine.getMember());
        assertEquals(loan, fine.getLoan());
        assertEquals(new BigDecimal("0.25"), fine.getAmount());
        assertEquals(FineReason.OVERDUE, fine.getReason());
        assertEquals(FineStatus.PENDING, fine.getStatus());
    }
}

