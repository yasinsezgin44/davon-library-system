package com.davon.library.integration;

import com.davon.library.dao.*;
import com.davon.library.model.*;
import com.davon.library.service.*;
import com.davon.library.exception.BusinessException;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Checkout and Return Integration Tests")
class CheckoutReturnIntegrationTest {

    @Inject
    BookDAO bookDAO;

    @Inject
    BookCopyDAO bookCopyDAO;

    @Inject
    UserService userService;

    @Inject
    BookService bookService;

    @Inject
    LoanService loanService;

    @Inject
    LibrarianService librarianService;

    @Inject
    LoanDAO loanDAO;

    @Inject
    FineDAO fineDAO;

    private Member testMember;
    private Book testBook;
    private BookCopy testBookCopy;

    @BeforeEach
    @Transactional
    void setUp() throws Exception {
        // Clear data before each test
        try {
            loanDAO.clearAll();
            fineDAO.clearAll();
            bookCopyDAO.clearAll();
            bookDAO.clearAll();
        } catch (DAOException e) {
            // Ignore if clear methods are not available
        }

        setupTestData();
    }

    @Transactional
    void setupTestData() throws Exception {
        // Create test book
        testBook = Book.builder()
                .title("Integration Test Book")
                .ISBN("1234567890123")
                .publicationYear(2023)
                .description("A book for testing checkout and return")
                .build();
        testBook = bookService.createBook(testBook);

        // Create test book copy
        testBookCopy = BookCopy.builder()
                .book(testBook)
                .status(BookCopy.CopyStatus.AVAILABLE)
                .condition("Good")
                .location("Test-A1")
                .acquisitionDate(LocalDate.now().minusMonths(1))
                .build();
        testBookCopy = bookCopyDAO.save(testBookCopy);

        // Create test member
        testMember = Member.builder()
                .username("integrationtest")
                .passwordHash("hashedpassword123")
                .email("integration.test@library.com")
                .fullName("Integration Test")
                .membershipStartDate(LocalDate.now().minusMonths(1))
                .membershipEndDate(LocalDate.now().plusMonths(11))
                .fineBalance(0.0)
                .active(true)
                .build();
        testMember = (Member) userService.createUser(testMember);
    }

    @Test
    @DisplayName("Complete checkout and return flow - happy path")
    @org.junit.jupiter.api.Order(1)
    void testCompleteCheckoutReturnFlow() throws Exception {
        // Step 1: Checkout book
        Loan loan = loanService.checkoutBook(testBook.getId(), testMember.getId());

        assertNotNull(loan);
        assertNotNull(loan.getId());
        assertEquals(testMember.getId(), loan.getMember().getId());
        assertEquals(testBookCopy.getId(), loan.getBookCopy().getId());
        assertEquals(Loan.LoanStatus.ACTIVE, loan.getStatus());
        assertEquals(LocalDate.now(), loan.getCheckoutDate());
        assertEquals(LocalDate.now().plusDays(14), loan.getDueDate());
        assertEquals(0, loan.getRenewalCount());

        // Verify book copy status changed
        BookCopy updatedCopy = bookCopyDAO.findById(testBookCopy.getId()).orElse(null);
        assertNotNull(updatedCopy);
        assertEquals(BookCopy.CopyStatus.CHECKED_OUT, updatedCopy.getStatus());

        // Step 2: Verify loan appears in member's active loans
        List<Loan> memberLoans = loanService.getMemberActiveLoans(testMember.getId());
        assertEquals(1, memberLoans.size());
        assertEquals(loan.getId(), memberLoans.get(0).getId());

        // Step 3: Return book on time (no fine)
        Receipt receipt = loanService.returnBook(loan.getId());

        assertNotNull(receipt);
        assertEquals(loan.getId(), receipt.getTransactionId());
        assertEquals(0.0, receipt.getTotal()); // No fine

        // Verify loan status changed
        Loan returnedLoan = loanDAO.findById(loan.getId()).orElse(null);
        assertNotNull(returnedLoan);
        assertEquals(Loan.LoanStatus.RETURNED, returnedLoan.getStatus());
        assertEquals(LocalDate.now(), returnedLoan.getReturnDate());

        // Verify book copy is available again
        BookCopy availableCopy = bookCopyDAO.findById(testBookCopy.getId()).orElse(null);
        assertNotNull(availableCopy);
        assertEquals(BookCopy.CopyStatus.AVAILABLE, availableCopy.getStatus());

        // Verify no active loans for member
        List<Loan> activeLoans = loanService.getMemberActiveLoans(testMember.getId());
        assertEquals(0, activeLoans.size());
    }

    @Test
    @DisplayName("Checkout and late return with fine")
    @org.junit.jupiter.api.Order(2)
    void testCheckoutAndLateReturnWithFine() throws Exception {
        // Step 1: Checkout book
        Loan loan = loanService.checkoutBook(testBook.getId(), testMember.getId());
        assertNotNull(loan);

        // Step 2: Simulate overdue by setting due date in the past
        loan.setDueDate(LocalDate.now().minusDays(3));
        loanDAO.update(loan);

        // Step 3: Return overdue book
        Receipt receipt = loanService.returnBook(loan.getId());

        assertNotNull(receipt);
        assertTrue(receipt.getTotal() > 0); // Should have fine
        assertEquals(0.75, receipt.getTotal(), 0.01); // 3 days * $0.25

        // Verify fine was created
        List<Fine> memberFines = fineDAO.findByMember(testMember);
        assertEquals(1, memberFines.size());

        Fine fine = memberFines.get(0);
        assertEquals(Fine.FineReason.OVERDUE, fine.getReason());
        assertEquals(0.75, fine.getAmount(), 0.01);
        assertEquals(Fine.FineStatus.PENDING, fine.getStatus());

        // Verify member's fine balance was updated
        Member updatedMember = (Member) userService.findById(testMember.getId());
        assertEquals(0.75, updatedMember.getFineBalance(), 0.01);
    }

    @Test
    @DisplayName("Should prevent checkout when member has outstanding fines")
    @org.junit.jupiter.api.Order(3)
    void testCheckoutPreventedByOutstandingFines() throws Exception {
        // Step 1: Add fine to member
        testMember.addFine(5.0);
        userService.updateUser(testMember.getId(), testMember);

        // Step 2: Attempt to checkout book
        BusinessException exception = assertThrows(BusinessException.class,
                () -> loanService.checkoutBook(testBook.getId(), testMember.getId()));

        assertTrue(exception.getMessage().contains("outstanding fines"));
    }

    @Test
    @DisplayName("Should prevent checkout when no available copies")
    @org.junit.jupiter.api.Order(4)
    void testCheckoutPreventedByNoAvailableCopies() throws Exception {
        // Step 1: Check out the only copy
        Loan loan = loanService.checkoutBook(testBook.getId(), testMember.getId());
        assertNotNull(loan);

        // Step 2: Create another member
        Member anotherMember = Member.builder()
                .username("anothermember")
                .passwordHash("hashedpassword456")
                .email("another@library.com")
                .fullName("Another Member")
                .membershipStartDate(LocalDate.now().minusMonths(1))
                .membershipEndDate(LocalDate.now().plusMonths(11))
                .fineBalance(0.0)
                .active(true)
                .build();
        anotherMember = (Member) userService.createUser(anotherMember);
        final Long anotherMemberId = anotherMember.getId();

        // Step 3: Attempt to checkout same book
        BusinessException exception = assertThrows(BusinessException.class,
                () -> loanService.checkoutBook(testBook.getId(), anotherMemberId));

        assertTrue(exception.getMessage().contains("No available copies"));
    }

    @Test
    @DisplayName("Should successfully renew loan when conditions are met")
    @org.junit.jupiter.api.Order(5)
    void testLoanRenewal() throws Exception {
        // Step 1: Checkout book
        Loan loan = loanService.checkoutBook(testBook.getId(), testMember.getId());
        assertNotNull(loan);
        LocalDate originalDueDate = loan.getDueDate();

        // Step 2: Renew loan
        Loan renewedLoan = loanService.renewLoan(loan.getId());

        assertNotNull(renewedLoan);
        assertEquals(1, renewedLoan.getRenewalCount());
        assertEquals(originalDueDate.plusDays(14), renewedLoan.getDueDate());
        assertEquals(Loan.LoanStatus.ACTIVE, renewedLoan.getStatus());
    }

    @Test
    @DisplayName("Should prevent renewal when member has outstanding fines")
    @org.junit.jupiter.api.Order(6)
    void testRenewalPreventedByOutstandingFines() throws Exception {
        // Step 1: Checkout book
        Loan loan = loanService.checkoutBook(testBook.getId(), testMember.getId());
        assertNotNull(loan);

        // Step 2: Add fine to member
        testMember.addFine(2.0);
        userService.updateUser(testMember.getId(), testMember);

        // Step 3: Attempt to renew loan
        BusinessException exception = assertThrows(BusinessException.class,
                () -> loanService.renewLoan(loan.getId()));

        assertTrue(exception.getMessage().contains("outstanding fines"));
    }

    @Test
    @DisplayName("Should prevent renewal after maximum renewals reached")
    @org.junit.jupiter.api.Order(7)
    void testRenewalPreventedByMaxRenewals() throws Exception {
        // Step 1: Checkout book
        Loan loan = loanService.checkoutBook(testBook.getId(), testMember.getId());
        assertNotNull(loan);

        // Step 2: Renew twice (maximum)
        loan = loanService.renewLoan(loan.getId());
        assertEquals(1, loan.getRenewalCount());

        loan = loanService.renewLoan(loan.getId());
        assertEquals(2, loan.getRenewalCount());
        final Long loanId = loan.getId();

        // Step 3: Attempt third renewal
        BusinessException exception = assertThrows(BusinessException.class,
                () -> loanService.renewLoan(loanId));

        assertTrue(exception.getMessage().contains("Maximum renewals reached"));
    }

    @Test
    @DisplayName("LibrarianService integration - checkout and return")
    @org.junit.jupiter.api.Order(8)
    void testLibrarianServiceIntegration() throws Exception {
        // Step 1: Librarian checkout
        Loan loan = librarianService.checkoutBook(testBook.getId(), testMember.getId());

        assertNotNull(loan);
        assertEquals(Loan.LoanStatus.ACTIVE, loan.getStatus());

        // Step 2: Librarian return
        Receipt receipt = librarianService.returnBook(loan.getId());

        assertNotNull(receipt);
        assertEquals(loan.getId(), receipt.getTransactionId());
    }

    @Test
    @DisplayName("Should find overdue loans correctly")
    @org.junit.jupiter.api.Order(9)
    void testOverdueLoansDetection() throws Exception {
        // Step 1: Checkout book
        Loan loan = loanService.checkoutBook(testBook.getId(), testMember.getId());
        assertNotNull(loan);

        // Step 2: Make loan overdue
        loan.setDueDate(LocalDate.now().minusDays(1));
        loanDAO.update(loan);

        // Step 3: Check overdue loans
        List<Loan> overdueLoans = loanService.getOverdueLoans();
        assertEquals(1, overdueLoans.size());
        assertEquals(loan.getId(), overdueLoans.get(0).getId());

        // Step 4: Librarian can also get overdue loans
        List<Loan> librarianOverdueLoans = librarianService.getOverdueLoans();
        assertEquals(1, librarianOverdueLoans.size());
    }
}