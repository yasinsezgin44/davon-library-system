package com.davon.library.service;

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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
class FineServiceTest {

    @Inject
    FineService fineService;

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

    private Member member;
    private Book book;
    private BookCopy bookCopy;

    @BeforeEach
    @Transactional
    void setUp() {
        fineRepository.deleteAll();
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
    void testCreateOverdueFine() {
        Loan loan = new Loan();
        loan.setMember(member);
        loan.setBookCopy(bookCopy);
        loan.setCheckoutDate(LocalDate.now().minusDays(30));
        loan.setDueDate(LocalDate.now().minusDays(15));
        loan.setStatus(LoanStatus.ACTIVE);
        loanRepository.persist(loan);

        assertNotNull(loan.getId());

        fineService.createOverdueFine(loan);

        Fine fine = fineRepository.findByLoan(loan);
        assertNotNull(fine);
    }
}
