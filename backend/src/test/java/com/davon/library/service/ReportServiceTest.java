package com.davon.library.service;

import com.davon.library.model.*;
import com.davon.library.repository.*;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class ReportServiceTest {

    @Inject
    ReportService reportService;

    @Inject
    LoanRepository loanRepository;

    @Inject
    FineRepository fineRepository;

    @Inject
    MemberRepository memberRepository;

    @Inject
    BookRepository bookRepository;

    @Inject
    BookCopyRepository bookCopyRepository;

    @Inject
    ReservationRepository reservationRepository;

    @BeforeEach
    @Transactional
    void setUp() {
        fineRepository.deleteAll();
        loanRepository.deleteAll();
        memberRepository.deleteAll();
        reservationRepository.deleteAll();
        bookCopyRepository.deleteAll();
        bookRepository.deleteAll();

        Member member = new Member();
        member.setFineBalance(BigDecimal.ZERO);
        memberRepository.persist(member);

        Book book = new Book();
        book.setTitle("Test Book");
        book.setIsbn("1234567890123");
        bookRepository.persist(book);

        BookCopy bookCopy = new BookCopy();
        bookCopy.setBook(book);
        bookCopy.setStatus("AVAILABLE");
        bookCopyRepository.persist(bookCopy);

        Loan loan = new Loan();
        loan.setMember(member);
        loan.setBookCopy(bookCopy);
        loan.setCheckoutDate(LocalDate.now().minusDays(30));
        loan.setDueDate(LocalDate.now().minusDays(15));
        loan.setStatus("ACTIVE");
        loanRepository.persist(loan);

        Fine fine = new Fine();
        fine.setLoan(loan);
        fine.setAmount(BigDecimal.TEN);
        fine.setStatus("OUTSTANDING");
        fineRepository.persist(fine);
    }

    @Test
    @Transactional
    void testGenerateMonthlyReport() {
        ReportService.MonthlyReport report = reportService.generateMonthlyReport(LocalDate.now().minusMonths(1), LocalDate.now());

        assertNotNull(report);
    }
}
