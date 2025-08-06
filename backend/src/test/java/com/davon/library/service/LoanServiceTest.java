package com.davon.library.service;

import com.davon.library.exception.BusinessException;
import com.davon.library.model.*;
import com.davon.library.model.enums.CopyStatus;
import com.davon.library.model.enums.LoanStatus;
import com.davon.library.repository.*;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class LoanServiceTest {

    @Inject
    LoanService loanService;

    @Inject
    LoanRepository loanRepository;

    @Inject
    MemberRepository memberRepository;

    @Inject
    BookRepository bookRepository;

    @Inject
    BookCopyRepository bookCopyRepository;

    @Inject
    ReservationRepository reservationRepository;

    private Member member;
    private Book book;
    private BookCopy bookCopy;

    @BeforeEach
    @Transactional
    void setUp() {
        loanRepository.deleteAll();
        memberRepository.deleteAll();
        reservationRepository.deleteAll();
        bookCopyRepository.deleteAll();
        bookRepository.deleteAll();

        member = new Member();
        member.setFineBalance(BigDecimal.ZERO);
        memberRepository.persist(member);

        book = new Book();
        book.setTitle("Test Book");
        book.setIsbn("1234567890123");
        bookRepository.persist(book);

        bookCopy = new BookCopy();
        bookCopy.setBook(book);
        bookCopy.setStatus(CopyStatus.AVAILABLE);
        bookCopyRepository.persist(bookCopy);
    }

    @Test
    @Transactional
    void testCheckoutBook() throws BusinessException {
        Loan loan = loanService.checkoutBook(book.getId(), member.getId());

        assertNotNull(loan);
        assertNotNull(loan.getId());
        assertEquals(member.getId(), loan.getMember().getId());
        assertEquals(CopyStatus.CHECKED_OUT, loan.getBookCopy().getStatus());
    }

    @Test
    @Transactional
    void testReturnBook() throws BusinessException {
        Loan loan = new Loan();
        loan.setMember(member);
        loan.setBookCopy(bookCopy);
        loan.setCheckoutDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(14));
        loan.setStatus(LoanStatus.ACTIVE);
        loanRepository.persist(loan);

        loanService.returnBook(loan.getId());

        Loan returnedLoan = loanRepository.findById(loan.getId());
        assertEquals(LoanStatus.RETURNED, returnedLoan.getStatus());
        assertEquals(CopyStatus.AVAILABLE, returnedLoan.getBookCopy().getStatus());
    }
}
