package com.davon.library.service;

import com.davon.library.model.*;
import com.davon.library.repository.*;
import com.davon.library.exception.BusinessException;
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
    BookRepository bookRepository;

    @Inject
    BookCopyRepository bookCopyRepository;

    @Inject
    MemberRepository memberRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    FineRepository fineRepository;

    private User user;
    private Member member;
    private Book book;
    private BookCopy bookCopy;

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
    }

    @Test
    @Transactional
    void testCheckoutBook() throws BusinessException {
        Loan loan = loanService.checkoutBook(book.getId(), member.getId());
        assertNotNull(loan.getId());
        assertEquals("CHECKED_OUT", bookCopyRepository.findById(bookCopy.getId()).getStatus());
    }

    @Test
    @Transactional
    void testReturnBook() throws BusinessException {
        Loan loan = new Loan();
        loan.setMember(member);
        loan.setBookCopy(bookCopy);
        loan.setCheckoutDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(14));
        loan.setStatus("ACTIVE");
        loanRepository.persist(loan);

        bookCopy.setStatus("CHECKED_OUT");
        bookCopyRepository.persist(bookCopy);

        loanService.returnBook(loan.getId());
        assertEquals("AVAILABLE", bookCopyRepository.findById(bookCopy.getId()).getStatus());
        assertEquals("RETURNED", loanRepository.findById(loan.getId()).getStatus());
    }
}
