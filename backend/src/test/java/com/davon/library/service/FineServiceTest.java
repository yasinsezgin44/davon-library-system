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

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class FineServiceTest {

    @Inject
    FineService fineService;

    @Inject
    FineRepository fineRepository;

    @Inject
    LoanRepository loanRepository;

    @Inject
    BookRepository bookRepository;

    @Inject
    BookCopyRepository bookCopyRepository;

    @Inject
    MemberRepository memberRepository;

    @Inject
    UserRepository userRepository;

    private User user;
    private Member member;
    private Book book;
    private BookCopy bookCopy;
    private Loan loan;

    @BeforeEach
    @Transactional
    void setUp() {
        fineRepository.deleteAll();
        loanRepository.deleteAll();
        bookCopyRepository.deleteAll();
        bookRepository.deleteAll();
        memberRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setUsername("testuser");
        user.setPasswordHash("password");
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        userRepository.persist(user);

        member = new Member();
        member.setUser(user);
        member.setFineBalance(BigDecimal.ZERO);
        memberRepository.persist(member);

        book = new Book();
        book.setTitle("Test Book");
        book.setIsbn("1234567890");
        bookRepository.persist(book);

        bookCopy = new BookCopy();
        bookCopy.setBook(book);
        bookCopy.setStatus("AVAILABLE");
        bookCopyRepository.persist(bookCopy);

        loan = new Loan();
        loan.setMember(member);
        loan.setBookCopy(bookCopy);
        loan.setCheckoutDate(LocalDate.now().minusDays(20));
        loan.setDueDate(LocalDate.now().minusDays(5));
        loan.setStatus("OVERDUE");
        loanRepository.persist(loan);
    }

    @Test
    @Transactional
    void testCreateOverdueFine() {
        Fine fine = fineService.createOverdueFine(loan);
        assertNotNull(fine.getId());
        assertEquals("OVERDUE", fine.getReason());
        assertTrue(fine.getAmount().compareTo(BigDecimal.ZERO) > 0);
    }
}
