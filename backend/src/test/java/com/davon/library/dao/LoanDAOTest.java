package com.davon.library.dao;

import com.davon.library.dao.impl.InMemoryLoanDAOImpl;
import com.davon.library.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoanDAO Tests")
class LoanDAOTest {

    private LoanDAO loanDAO;
    private Member testMember;
    private BookCopy testBookCopy;
    private Book testBook;

    @BeforeEach
    void setUp() {
        loanDAO = new InMemoryLoanDAOImpl();

        // Create test book
        testBook = Book.builder()
                .id(1L)
                .title("Test Book")
                .ISBN("1234567890")
                .publicationYear(2023)
                .build();

        // Create test book copy
        testBookCopy = BookCopy.builder()
                .id(1L)
                .book(testBook)
                .status(BookCopy.CopyStatus.AVAILABLE)
                .condition("Good")
                .location("A1-B2")
                .acquisitionDate(LocalDate.now().minusMonths(1))
                .build();

        // Create test member
        testMember = Member.builder()
                .id(1L)
                .email("test@library.com")
                .firstName("John")
                .lastName("Doe")
                .membershipStartDate(LocalDate.now().minusMonths(1))
                .membershipEndDate(LocalDate.now().plusMonths(11))
                .fineBalance(0.0)
                .build();
    }

    @Test
    @DisplayName("Should save and retrieve loan")
    void testSaveAndFindById() throws DAOException {
        Loan loan = createTestLoan();

        Loan savedLoan = loanDAO.save(loan);

        assertNotNull(savedLoan.getId());
        assertEquals(testMember.getId(), savedLoan.getMember().getId());
        assertEquals(testBookCopy.getId(), savedLoan.getBookCopy().getId());
        assertEquals(Loan.LoanStatus.ACTIVE, savedLoan.getStatus());

        Optional<Loan> foundLoan = loanDAO.findById(savedLoan.getId());
        assertTrue(foundLoan.isPresent());
        assertEquals(savedLoan.getId(), foundLoan.get().getId());
    }

    @Test
    @DisplayName("Should find loans by member")
    void testFindByMember() throws DAOException {
        Loan loan1 = createTestLoan();
        Loan loan2 = createTestLoan();
        loan2.setCheckoutDate(LocalDate.now().minusDays(7));

        loanDAO.save(loan1);
        loanDAO.save(loan2);

        List<Loan> memberLoans = loanDAO.findByMember(testMember);

        assertEquals(2, memberLoans.size());
    }

    @Test
    @DisplayName("Should find active loans by member")
    void testFindActiveLoansByMember() throws DAOException {
        Loan activeLoan = createTestLoan();
        Loan returnedLoan = createTestLoan();
        returnedLoan.setStatus(Loan.LoanStatus.RETURNED);
        returnedLoan.setReturnDate(LocalDate.now());

        loanDAO.save(activeLoan);
        loanDAO.save(returnedLoan);

        List<Loan> activeLoans = loanDAO.findActiveLoansByMember(testMember);

        assertEquals(1, activeLoans.size());
        assertEquals(Loan.LoanStatus.ACTIVE, activeLoans.get(0).getStatus());
    }

    @Test
    @DisplayName("Should find loan by book copy and member")
    void testFindByBookCopyAndMember() throws DAOException {
        Loan loan = createTestLoan();
        loanDAO.save(loan);

        Optional<Loan> foundLoan = loanDAO.findByBookCopyAndMember(testBookCopy, testMember);

        assertTrue(foundLoan.isPresent());
        assertEquals(loan.getMember().getId(), foundLoan.get().getMember().getId());
        assertEquals(loan.getBookCopy().getId(), foundLoan.get().getBookCopy().getId());
    }

    @Test
    @DisplayName("Should find active loan by book copy")
    void testFindActiveByBookCopy() throws DAOException {
        Loan activeLoan = createTestLoan();
        loanDAO.save(activeLoan);

        Optional<Loan> foundLoan = loanDAO.findActiveByBookCopy(testBookCopy);

        assertTrue(foundLoan.isPresent());
        assertEquals(Loan.LoanStatus.ACTIVE, foundLoan.get().getStatus());
        assertEquals(testBookCopy.getId(), foundLoan.get().getBookCopy().getId());
    }

    @Test
    @DisplayName("Should find overdue loans")
    void testFindOverdueLoans() throws DAOException {
        Loan overdueLoan = createTestLoan();
        overdueLoan.setDueDate(LocalDate.now().minusDays(1));

        Loan currentLoan = createTestLoan();
        currentLoan.setDueDate(LocalDate.now().plusDays(7));

        loanDAO.save(overdueLoan);
        loanDAO.save(currentLoan);

        List<Loan> overdueLoans = loanDAO.findOverdueLoans(LocalDate.now());

        assertEquals(1, overdueLoans.size());
        assertTrue(overdueLoans.get(0).getDueDate().isBefore(LocalDate.now()));
    }

    @Test
    @DisplayName("Should find loans due on specific date")
    void testFindLoansDueOn() throws DAOException {
        LocalDate targetDate = LocalDate.now().plusDays(3);

        Loan loanDueOnTarget = createTestLoan();
        loanDueOnTarget.setDueDate(targetDate);

        Loan loanDueOther = createTestLoan();
        loanDueOther.setDueDate(LocalDate.now().plusDays(7));

        loanDAO.save(loanDueOnTarget);
        loanDAO.save(loanDueOther);

        List<Loan> loansDue = loanDAO.findLoansDueOn(targetDate);

        assertEquals(1, loansDue.size());
        assertEquals(targetDate, loansDue.get(0).getDueDate());
    }

    @Test
    @DisplayName("Should find loans by status")
    void testFindByStatus() throws DAOException {
        Loan activeLoan = createTestLoan();
        Loan returnedLoan = createTestLoan();
        returnedLoan.setStatus(Loan.LoanStatus.RETURNED);

        loanDAO.save(activeLoan);
        loanDAO.save(returnedLoan);

        List<Loan> activeLoans = loanDAO.findByStatus(Loan.LoanStatus.ACTIVE);
        List<Loan> returnedLoans = loanDAO.findByStatus(Loan.LoanStatus.RETURNED);

        assertEquals(1, activeLoans.size());
        assertEquals(1, returnedLoans.size());
        assertEquals(Loan.LoanStatus.ACTIVE, activeLoans.get(0).getStatus());
        assertEquals(Loan.LoanStatus.RETURNED, returnedLoans.get(0).getStatus());
    }

    @Test
    @DisplayName("Should count active loans by member")
    void testCountActiveLoansByMember() throws DAOException {
        Loan activeLoan1 = createTestLoan();
        Loan activeLoan2 = createTestLoan();
        Loan returnedLoan = createTestLoan();
        returnedLoan.setStatus(Loan.LoanStatus.RETURNED);

        loanDAO.save(activeLoan1);
        loanDAO.save(activeLoan2);
        loanDAO.save(returnedLoan);

        long activeCount = loanDAO.countActiveLoansByMember(testMember);

        assertEquals(2, activeCount);
    }

    @Test
    @DisplayName("Should update loan status")
    void testUpdateLoan() throws DAOException {
        Loan loan = createTestLoan();
        Loan savedLoan = loanDAO.save(loan);

        savedLoan.setStatus(Loan.LoanStatus.RETURNED);
        savedLoan.setReturnDate(LocalDate.now());

        Loan updatedLoan = loanDAO.update(savedLoan);

        assertEquals(Loan.LoanStatus.RETURNED, updatedLoan.getStatus());
        assertNotNull(updatedLoan.getReturnDate());
    }

    @Test
    @DisplayName("Should delete loan")
    void testDeleteLoan() throws DAOException {
        Loan loan = createTestLoan();
        Loan savedLoan = loanDAO.save(loan);

        loanDAO.deleteById(savedLoan.getId());

        Optional<Loan> deletedLoan = loanDAO.findById(savedLoan.getId());
        assertFalse(deletedLoan.isPresent());
    }

    @Test
    @DisplayName("Should validate loan entity before saving")
    void testValidation() {
        Loan invalidLoan = Loan.builder()
                .checkoutDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status(Loan.LoanStatus.ACTIVE)
                .build(); // Missing member and book copy

        assertThrows(DAOException.class, () -> loanDAO.save(invalidLoan));
    }

    @Test
    @DisplayName("Should handle empty results gracefully")
    void testEmptyResults() {
        List<Loan> loans = loanDAO.findByMember(testMember);
        List<Loan> overdueLoans = loanDAO.findOverdueLoans(LocalDate.now());
        long count = loanDAO.countActiveLoansByMember(testMember);

        assertTrue(loans.isEmpty());
        assertTrue(overdueLoans.isEmpty());
        assertEquals(0, count);
    }

    private Loan createTestLoan() {
        return Loan.builder()
                .member(testMember)
                .bookCopy(testBookCopy)
                .checkoutDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status(Loan.LoanStatus.ACTIVE)
                .renewalCount(0)
                .build();
    }
}