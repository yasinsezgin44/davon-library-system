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
import java.time.LocalDateTime;
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

    @Inject
    UserDAO userDAO;

    private Member testMember;
    private Book testBook;
    private BookCopy testBookCopy;
    private String testTimestamp;

    @BeforeEach
    @Transactional
    void setUp() throws Exception {
        // Generate unique timestamp for this test run
        testTimestamp = String.valueOf(System.nanoTime());

        // Clear data in proper order to handle foreign key constraints
        try {
            // Clear in order of dependencies: fines -> loans -> book_copies -> books ->
            // users
            clearDataWithConstraints();
        } catch (Exception e) {
            System.out.println("Warning: Could not clear all data: " + e.getMessage());
        }

        // Setup fresh test data for each test
        setupTestData();
    }

    private void clearDataWithConstraints() throws Exception {
        // Clear fines first (references members)
        try {
            fineDAO.clearAll();
        } catch (Exception e) {
            System.out.println("Warning: Could not clear fines: " + e.getMessage());
        }

        // Clear loans next (references members and book copies)
        try {
            loanDAO.clearAll();
        } catch (Exception e) {
            System.out.println("Warning: Could not clear loans: " + e.getMessage());
        }

        // Clear book copies (references books)
        try {
            bookCopyDAO.clearAll();
        } catch (Exception e) {
            System.out.println("Warning: Could not clear book copies: " + e.getMessage());
        }

        // Clear books
        try {
            bookDAO.clearAll();
        } catch (Exception e) {
            System.out.println("Warning: Could not clear books: " + e.getMessage());
        }

        // Clear users last (no dependencies)
        try {
            userDAO.clearAll();
        } catch (Exception e) {
            System.out.println("Warning: Could not clear users: " + e.getMessage());
        }
    }

    @Transactional
    void setupTestData() throws Exception {
        // Create test book with unique ISBN (use timestamp for valid ISBN-13)
        String validISBN = String.format("978%010d", Long.parseLong(testTimestamp) % 1000000000L);
        testBook = Book.builder()
                .title("Integration Test Book " + testTimestamp)
                .ISBN(validISBN)
                .publicationYear(2023)
                .description("A book for testing checkout and return")
                .pages(300)
                .build();
        testBook = bookService.createBook(testBook);

        // Create test book copy
        testBookCopy = BookCopy.builder()
                .book(testBook)
                .status(BookCopy.CopyStatus.AVAILABLE)
                .condition("Good")
                .location("Test-A1-" + testTimestamp)
                .acquisitionDate(LocalDate.now().minusMonths(1))
                .build();
        testBookCopy = bookCopyDAO.save(testBookCopy);

        // Create test member with unique username and email
        testMember = Member.builder()
                .username("testuser" + testTimestamp)
                .passwordHash("hashedpassword123")
                .email("test" + testTimestamp + "@library.com")
                .fullName("Integration Test User")
                .membershipStartDate(LocalDate.now().minusMonths(1))
                .membershipEndDate(LocalDate.now().plusMonths(11))
                .fineBalance(0.0)
                .active(true)
                .status(UserStatus.ACTIVE)
                .build();

        // Validate that email and username don't already exist
        if (userDAO.existsByEmail(testMember.getEmail())) {
            testMember.setEmail("test" + testTimestamp + "_" + System.currentTimeMillis() + "@library.com");
        }
        if (userDAO.existsByUsername(testMember.getUsername())) {
            testMember.setUsername("testuser" + testTimestamp + "_" + System.currentTimeMillis());
        }

        testMember = (Member) userService.createUser(testMember);
    }

    @Test
    @DisplayName("Complete checkout and return flow - happy path")
    @org.junit.jupiter.api.Order(1)
    void testCompleteCheckoutReturnFlow() throws Exception {
        // Debug: Verify test data is set up correctly
        System.out.println("🔍 Debug: testMember ID: " + (testMember != null ? testMember.getId() : "NULL"));
        System.out.println("🔍 Debug: testBook ID: " + (testBook != null ? testBook.getId() : "NULL"));
        System.out.println("🔍 Debug: testBookCopy ID: " + (testBookCopy != null ? testBookCopy.getId() : "NULL"));

        assertNotNull(testMember, "Test member should not be null");
        assertNotNull(testBook, "Test book should not be null");
        assertNotNull(testBookCopy, "Test book copy should not be null");

        // Step 1: Checkout book
        try {
            System.out.println("🔍 Debug: Attempting checkout for book ID " + testBook.getId() + " by member ID "
                    + testMember.getId());
            Loan loan = loanService.checkoutBook(testBook.getId(), testMember.getId());

            System.out.println(
                    "🔍 Debug: Checkout result: " + (loan != null ? "SUCCESS (ID: " + loan.getId() + ")" : "NULL"));

            if (loan == null) {
                throw new AssertionError("Checkout returned null loan");
            }

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

        } catch (Exception e) {
            System.err.println("🚨 Debug: Exception during checkout test: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    @DisplayName("Checkout and late return with fine")
    @org.junit.jupiter.api.Order(2)
    void testCheckoutAndLateReturnWithFine() throws Exception {
        // Step 1: Checkout book
        Loan loan = loanService.checkoutBook(testBook.getId(), testMember.getId());
        assertNotNull(loan);

        // Step 2: Simulate overdue by setting due date in the past
        LocalDate overdueDueDate = LocalDate.now().minusDays(5); // 5 days ago to be safe
        loan.setDueDate(overdueDueDate);
        loan = loanDAO.update(loan);

        // Verify the loan was updated correctly
        Loan reloadedLoan = loanDAO.findById(loan.getId()).orElse(null);
        assertNotNull(reloadedLoan);
        assertEquals(overdueDueDate, reloadedLoan.getDueDate());

        // Step 3: Return overdue book
        Receipt receipt = loanService.returnBook(loan.getId());

        assertNotNull(receipt);
        System.out.println("🔍 DEBUG: Test received receipt with total: " + receipt.getTotal());
        assertTrue(receipt.getTotal() > 0); // Should have fine
        assertEquals(1.25, receipt.getTotal(), 0.01); // 5 days * $0.25

        // Verify fine was created
        List<Fine> memberFines = fineDAO.findByMember(testMember);
        System.out.println("🔍 DEBUG: Found " + memberFines.size() + " fines for member " + testMember.getId());
        assertEquals(1, memberFines.size());

        Fine fine = memberFines.get(0);
        System.out.println("🔍 DEBUG: Fine reason: " + fine.getReason() + ", amount: " + fine.getAmount() + ", status: "
                + fine.getStatus());
        assertEquals(Fine.FineReason.OVERDUE, fine.getReason());
        assertEquals(1.25, fine.getAmount(), 0.01);
        assertEquals(Fine.FineStatus.PENDING, fine.getStatus());

        // Verify member's fine balance was updated
        Member updatedMember = (Member) userService.findById(testMember.getId());
        System.out.println("🔍 DEBUG: Member fine balance: " + updatedMember.getFineBalance());
        assertEquals(1.25, updatedMember.getFineBalance(), 0.01);
    }

    @Test
    @DisplayName("Should prevent checkout when member has outstanding fines")
    @org.junit.jupiter.api.Order(3)
    void testCheckoutPreventedByOutstandingFines() throws Exception {
        // Step 1: Create and persist a fine
        Fine fine = Fine.builder()
                .member(testMember)
                .amount(5.0)
                .reason(Fine.FineReason.OVERDUE)
                .status(Fine.FineStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .issueDate(LocalDate.now())
                .build();
        fineDAO.save(fine);

        // Verify member's fine balance is updated
        Member updatedMember = (Member) userService.findById(testMember.getId());
        assertEquals(5.0, updatedMember.getFineBalance(), 0.01);

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
                .username("anothermember" + testTimestamp)
                .passwordHash("hashedpassword456")
                .email("another" + testTimestamp + "@library.com")
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