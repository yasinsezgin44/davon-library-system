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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ReportServiceTest {

    @Inject
    ReportService reportService;

    @Inject
    LoanRepository loanRepository;

    @Inject
    FineRepository fineRepository;

    @Inject
    BookRepository bookRepository;

    @Inject
    BookCopyRepository bookCopyRepository;

    @Inject
    MemberRepository memberRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    AuthorRepository authorRepository;

    @Inject
    PublisherRepository publisherRepository;

    @Inject
    CategoryRepository categoryRepository;

    private User user1;
    private Member member1;
    private Book book1;
    private BookCopy bookCopy1;

    @BeforeEach
    @Transactional
    void setUp() {
        fineRepository.deleteAll();
        loanRepository.deleteAll();
        bookCopyRepository.deleteAll();
        bookRepository.deleteAll();
        memberRepository.deleteAll();
        userRepository.deleteAll();
        authorRepository.deleteAll();
        publisherRepository.deleteAll();
        categoryRepository.deleteAll();

        user1 = new User();
        user1.setUsername("testuser1");
        user1.setPasswordHash("password");
        user1.setFullName("Test User 1");
        user1.setEmail("test1@example.com");
        userRepository.persist(user1);

        member1 = new Member();
        member1.setUser(user1);
        member1.setFineBalance(BigDecimal.ZERO);
        memberRepository.persist(member1);

        Author author = new Author();
        author.setName("Test Author");
        authorRepository.persist(author);

        Publisher publisher = new Publisher();
        publisher.setName("Test Publisher");
        publisherRepository.persist(publisher);

        Category category = new Category();
        category.setName("Test Category");
        categoryRepository.persist(category);

        Set<Author> authors = new HashSet<>();
        authors.add(author);

        book1 = new Book();
        book1.setTitle("Java Programming");
        book1.setIsbn("1234567890");
        book1.setPublicationYear(2022);
        book1.setAuthors(authors);
        book1.setPublisher(publisher);
        book1.setCategory(category);
        bookRepository.persist(book1);

        bookCopy1 = new BookCopy();
        bookCopy1.setBook(book1);
        bookCopy1.setStatus("AVAILABLE");
        bookCopyRepository.persist(bookCopy1);
    }

    @Test
    @Transactional
    void testGenerateMonthlyReport() {
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();

        Loan loan = new Loan();
        loan.setMember(member1);
        loan.setBookCopy(bookCopy1);
        loan.setCheckoutDate(LocalDate.now().minusDays(10));
        loan.setDueDate(LocalDate.now().plusDays(4));
        loan.setStatus("ACTIVE");
        loanRepository.persist(loan);

        ReportService.MonthlyReport report = reportService.generateMonthlyReport(startDate, endDate);
        assertNotNull(report);
        assertEquals(1, report.getTotalLoans());
    }
}
