package com.davon.library.dao.impl;

import com.davon.library.dao.LoanDAO;
import com.davon.library.dao.DAOException;
import com.davon.library.model.Loan;
import com.davon.library.model.Member;
import com.davon.library.model.BookCopy;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MSSQLLoanDAOImplTest {

    @Inject
    LoanDAO loanDAO;

    private Loan testLoan;
    private Member testMember;
    private BookCopy testBookCopy;

    @BeforeEach
    void setUp() {
        // Create test member (simplified - in real test, you'd create via MemberDAO)
        testMember = new Member();
        testMember.setId(1L);

        // Create test book copy (simplified - in real test, you'd create via
        // BookCopyDAO)
        testBookCopy = new BookCopy();
        testBookCopy.setId(1L);

        testLoan = new Loan();
        testLoan.setMemberId(1L);
        testLoan.setBookCopyId(1L);
        testLoan.setLoanDate(LocalDate.now());
        testLoan.setDueDate(LocalDate.now().plusDays(14));
        testLoan.setStatus(Loan.LoanStatus.ACTIVE);
    }

    @Test
    @Order(1)
    @Transactional
    void testSaveLoan() throws DAOException {
        // Given
        assertNull(testLoan.getId());

        // When
        Loan savedLoan = loanDAO.save(testLoan);

        // Then
        assertNotNull(savedLoan.getId());
        assertNotNull(savedLoan.getCreatedAt());
        assertNotNull(savedLoan.getUpdatedAt());
        assertEquals(testLoan.getMemberId(), savedLoan.getMemberId());
        assertEquals(testLoan.getBookCopyId(), savedLoan.getBookCopyId());
        assertEquals(testLoan.getLoanDate(), savedLoan.getLoanDate());
        assertEquals(testLoan.getDueDate(), savedLoan.getDueDate());
        assertEquals(testLoan.getStatus(), savedLoan.getStatus());
        assertNull(savedLoan.getReturnDate());
    }

    @Test
    @Order(2)
    @Transactional
    void testFindById() throws DAOException {
        // Given
        Loan savedLoan = loanDAO.save(testLoan);

        // When
        Optional<Loan> foundLoan = loanDAO.findById(savedLoan.getId());

        // Then
        assertTrue(foundLoan.isPresent());
        assertEquals(savedLoan.getId(), foundLoan.get().getId());
        assertEquals(savedLoan.getMemberId(), foundLoan.get().getMemberId());
        assertEquals(savedLoan.getBookCopyId(), foundLoan.get().getBookCopyId());
        assertEquals(savedLoan.getStatus(), foundLoan.get().getStatus());
    }

    @Test
    @Order(3)
    void testFindByIdNotFound() {
        // When
        Optional<Loan> foundLoan = loanDAO.findById(999999L);

        // Then
        assertFalse(foundLoan.isPresent());
    }

    @Test
    @Order(4)
    @Transactional
    void testUpdateLoan() throws DAOException {
        // Given
        Loan savedLoan = loanDAO.save(testLoan);
        Long originalId = savedLoan.getId();
        LocalDateTime originalCreatedAt = savedLoan.getCreatedAt();

        // When
        savedLoan.setReturnDate(LocalDate.now());
        savedLoan.setStatus(Loan.LoanStatus.RETURNED);

        Loan updatedLoan = loanDAO.update(savedLoan);

        // Then
        assertEquals(originalId, updatedLoan.getId());
        assertEquals(originalCreatedAt, updatedLoan.getCreatedAt());
        assertTrue(updatedLoan.getUpdatedAt().isAfter(originalCreatedAt));
        assertEquals(LocalDate.now(), updatedLoan.getReturnDate());
        assertEquals(Loan.LoanStatus.RETURNED, updatedLoan.getStatus());
    }

    @Test
    @Order(5)
    @Transactional
    void testExistsById() throws DAOException {
        // Given
        Loan savedLoan = loanDAO.save(testLoan);

        // When & Then
        assertTrue(loanDAO.existsById(savedLoan.getId()));
        assertFalse(loanDAO.existsById(999999L));
    }

    @Test
    @Order(6)
    @Transactional
    void testCount() throws DAOException {
        // Given
        long initialCount = loanDAO.count();

        loanDAO.save(testLoan);

        Loan loan2 = new Loan();
        loan2.setMemberId(2L);
        loan2.setBookCopyId(2L);
        loan2.setLoanDate(LocalDate.now());
        loan2.setDueDate(LocalDate.now().plusDays(14));
        loan2.setStatus(Loan.LoanStatus.ACTIVE);
        loanDAO.save(loan2);

        // When
        long newCount = loanDAO.count();

        // Then
        assertEquals(initialCount + 2, newCount);
    }

    @Test
    @Order(7)
    @Transactional
    void testFindByMember() throws DAOException {
        // Given
        loanDAO.save(testLoan);

        Loan loan2 = new Loan();
        loan2.setMemberId(1L); // Same member
        loan2.setBookCopyId(2L);
        loan2.setLoanDate(LocalDate.now());
        loan2.setDueDate(LocalDate.now().plusDays(14));
        loan2.setStatus(Loan.LoanStatus.ACTIVE);
        loanDAO.save(loan2);

        Loan loan3 = new Loan();
        loan3.setMemberId(3L); // Different member
        loan3.setBookCopyId(3L);
        loan3.setLoanDate(LocalDate.now());
        loan3.setDueDate(LocalDate.now().plusDays(14));
        loan3.setStatus(Loan.LoanStatus.ACTIVE);
        loanDAO.save(loan3);

        // When
        List<Loan> memberLoans = loanDAO.findByMember(testMember);

        // Then
        assertEquals(2, memberLoans.size());
        assertTrue(memberLoans.stream().allMatch(loan -> loan.getMemberId().equals(1L)));
    }

    @Test
    @Order(8)
    @Transactional
    void testFindActiveLoansByMember() throws DAOException {
        // Given
        loanDAO.save(testLoan); // ACTIVE

        Loan returnedLoan = new Loan();
        returnedLoan.setMemberId(1L);
        returnedLoan.setBookCopyId(2L);
        returnedLoan.setLoanDate(LocalDate.now().minusDays(7));
        returnedLoan.setDueDate(LocalDate.now().minusDays(1));
        returnedLoan.setReturnDate(LocalDate.now());
        returnedLoan.setStatus(Loan.LoanStatus.RETURNED);
        loanDAO.save(returnedLoan);

        Loan overdueLoan = new Loan();
        overdueLoan.setMemberId(1L);
        overdueLoan.setBookCopyId(3L);
        overdueLoan.setLoanDate(LocalDate.now().minusDays(20));
        overdueLoan.setDueDate(LocalDate.now().minusDays(6));
        overdueLoan.setStatus(Loan.LoanStatus.OVERDUE);
        loanDAO.save(overdueLoan);

        // When
        List<Loan> activeLoans = loanDAO.findActiveLoansByMember(testMember);

        // Then
        assertEquals(2, activeLoans.size()); // ACTIVE and OVERDUE
        assertTrue(activeLoans.stream().anyMatch(loan -> loan.getStatus() == Loan.LoanStatus.ACTIVE));
        assertTrue(activeLoans.stream().anyMatch(loan -> loan.getStatus() == Loan.LoanStatus.OVERDUE));
        assertFalse(activeLoans.stream().anyMatch(loan -> loan.getStatus() == Loan.LoanStatus.RETURNED));
    }

    @Test
    @Order(9)
    @Transactional
    void testCountActiveLoansByMember() throws DAOException {
        // Given
        loanDAO.save(testLoan); // ACTIVE

        Loan returnedLoan = new Loan();
        returnedLoan.setMemberId(1L);
        returnedLoan.setBookCopyId(2L);
        returnedLoan.setLoanDate(LocalDate.now().minusDays(7));
        returnedLoan.setDueDate(LocalDate.now().minusDays(1));
        returnedLoan.setReturnDate(LocalDate.now());
        returnedLoan.setStatus(Loan.LoanStatus.RETURNED);
        loanDAO.save(returnedLoan);

        // When
        long activeCount = loanDAO.countActiveLoansByMember(testMember);

        // Then
        assertEquals(1, activeCount);
    }

    @Test
    @Order(10)
    @Transactional
    void testFindByBookCopyAndMember() throws DAOException {
        // Given
        loanDAO.save(testLoan);

        // When
        Optional<Loan> foundLoan = loanDAO.findByBookCopyAndMember(testBookCopy, testMember);

        // Then
        assertTrue(foundLoan.isPresent());
        assertEquals(testLoan.getMemberId(), foundLoan.get().getMemberId());
        assertEquals(testLoan.getBookCopyId(), foundLoan.get().getBookCopyId());
    }

    @Test
    @Order(11)
    @Transactional
    void testFindActiveByBookCopy() throws DAOException {
        // Given
        loanDAO.save(testLoan); // ACTIVE

        Loan returnedLoan = new Loan();
        returnedLoan.setMemberId(2L);
        returnedLoan.setBookCopyId(1L); // Same book copy
        returnedLoan.setLoanDate(LocalDate.now().minusDays(7));
        returnedLoan.setDueDate(LocalDate.now().minusDays(1));
        returnedLoan.setReturnDate(LocalDate.now());
        returnedLoan.setStatus(Loan.LoanStatus.RETURNED);
        loanDAO.save(returnedLoan);

        // When
        Optional<Loan> activeLoan = loanDAO.findActiveByBookCopy(testBookCopy);

        // Then
        assertTrue(activeLoan.isPresent());
        assertEquals(Loan.LoanStatus.ACTIVE, activeLoan.get().getStatus());
        assertEquals(1L, activeLoan.get().getMemberId());
    }

    @Test
    @Order(12)
    @Transactional
    void testFindOverdueLoans() throws DAOException {
        // Given
        Loan overdueLoan1 = new Loan();
        overdueLoan1.setMemberId(1L);
        overdueLoan1.setBookCopyId(1L);
        overdueLoan1.setLoanDate(LocalDate.now().minusDays(20));
        overdueLoan1.setDueDate(LocalDate.now().minusDays(5)); // Overdue
        overdueLoan1.setStatus(Loan.LoanStatus.ACTIVE);
        loanDAO.save(overdueLoan1);

        Loan currentLoan = new Loan();
        currentLoan.setMemberId(2L);
        currentLoan.setBookCopyId(2L);
        currentLoan.setLoanDate(LocalDate.now());
        currentLoan.setDueDate(LocalDate.now().plusDays(7)); // Not overdue
        currentLoan.setStatus(Loan.LoanStatus.ACTIVE);
        loanDAO.save(currentLoan);

        // When
        List<Loan> overdueLoans = loanDAO.findOverdueLoans(LocalDate.now());

        // Then
        assertEquals(1, overdueLoans.size());
        assertTrue(overdueLoans.get(0).getDueDate().isBefore(LocalDate.now()));
        assertEquals(Loan.LoanStatus.ACTIVE, overdueLoans.get(0).getStatus());
    }

    @Test
    @Order(13)
    @Transactional
    void testFindLoansDueOn() throws DAOException {
        // Given
        LocalDate targetDate = LocalDate.now().plusDays(3);

        Loan dueTodayLoan = new Loan();
        dueTodayLoan.setMemberId(1L);
        dueTodayLoan.setBookCopyId(1L);
        dueTodayLoan.setLoanDate(LocalDate.now().minusDays(11));
        dueTodayLoan.setDueDate(targetDate);
        dueTodayLoan.setStatus(Loan.LoanStatus.ACTIVE);
        loanDAO.save(dueTodayLoan);

        Loan dueLaterLoan = new Loan();
        dueLaterLoan.setMemberId(2L);
        dueLaterLoan.setBookCopyId(2L);
        dueLaterLoan.setLoanDate(LocalDate.now());
        dueLaterLoan.setDueDate(LocalDate.now().plusDays(7));
        dueLaterLoan.setStatus(Loan.LoanStatus.ACTIVE);
        loanDAO.save(dueLaterLoan);

        // When
        List<Loan> loansDueOn = loanDAO.findLoansDueOn(targetDate);

        // Then
        assertEquals(1, loansDueOn.size());
        assertEquals(targetDate, loansDueOn.get(0).getDueDate());
        assertEquals(Loan.LoanStatus.ACTIVE, loansDueOn.get(0).getStatus());
    }

    @Test
    @Order(14)
    @Transactional
    void testFindByStatus() throws DAOException {
        // Given
        loanDAO.save(testLoan); // ACTIVE

        Loan returnedLoan = new Loan();
        returnedLoan.setMemberId(2L);
        returnedLoan.setBookCopyId(2L);
        returnedLoan.setLoanDate(LocalDate.now().minusDays(7));
        returnedLoan.setDueDate(LocalDate.now().minusDays(1));
        returnedLoan.setReturnDate(LocalDate.now());
        returnedLoan.setStatus(Loan.LoanStatus.RETURNED);
        loanDAO.save(returnedLoan);

        Loan overdueLoan = new Loan();
        overdueLoan.setMemberId(3L);
        overdueLoan.setBookCopyId(3L);
        overdueLoan.setLoanDate(LocalDate.now().minusDays(20));
        overdueLoan.setDueDate(LocalDate.now().minusDays(6));
        overdueLoan.setStatus(Loan.LoanStatus.OVERDUE);
        loanDAO.save(overdueLoan);

        // When
        List<Loan> activeLoans = loanDAO.findByStatus(Loan.LoanStatus.ACTIVE);
        List<Loan> returnedLoans = loanDAO.findByStatus(Loan.LoanStatus.RETURNED);
        List<Loan> overdueLoans = loanDAO.findByStatus(Loan.LoanStatus.OVERDUE);

        // Then
        assertEquals(1, activeLoans.size());
        assertEquals(1, returnedLoans.size());
        assertEquals(1, overdueLoans.size());

        assertEquals(Loan.LoanStatus.ACTIVE, activeLoans.get(0).getStatus());
        assertEquals(Loan.LoanStatus.RETURNED, returnedLoans.get(0).getStatus());
        assertEquals(Loan.LoanStatus.OVERDUE, overdueLoans.get(0).getStatus());
    }

    @Test
    @Order(15)
    @Transactional
    void testDeleteById() throws DAOException {
        // Given
        Loan savedLoan = loanDAO.save(testLoan);
        Long loanId = savedLoan.getId();

        assertTrue(loanDAO.existsById(loanId));

        // When
        loanDAO.deleteById(loanId);

        // Then
        assertFalse(loanDAO.existsById(loanId));
        assertFalse(loanDAO.findById(loanId).isPresent());
    }

    @Test
    @Order(16)
    @Transactional
    void testDelete() throws DAOException {
        // Given
        Loan savedLoan = loanDAO.save(testLoan);
        Long loanId = savedLoan.getId();

        assertTrue(loanDAO.existsById(loanId));

        // When
        loanDAO.delete(savedLoan);

        // Then
        assertFalse(loanDAO.existsById(loanId));
        assertFalse(loanDAO.findById(loanId).isPresent());
    }

    @Test
    @Order(17)
    void testDeleteNonExistentLoan() {
        // When & Then
        assertThrows(DAOException.class, () -> {
            loanDAO.deleteById(999999L);
        });
    }

    @Test
    @Order(18)
    void testUpdateNonExistentLoan() {
        // Given
        Loan nonExistentLoan = new Loan();
        nonExistentLoan.setId(999999L);
        nonExistentLoan.setMemberId(1L);
        nonExistentLoan.setBookCopyId(1L);
        nonExistentLoan.setLoanDate(LocalDate.now());
        nonExistentLoan.setDueDate(LocalDate.now().plusDays(14));
        nonExistentLoan.setStatus(Loan.LoanStatus.ACTIVE);

        // When & Then
        assertThrows(DAOException.class, () -> {
            loanDAO.update(nonExistentLoan);
        });
    }

    @Test
    @Order(19)
    @Transactional
    void testFindAll() throws DAOException {
        // Given
        loanDAO.save(testLoan);

        Loan loan2 = new Loan();
        loan2.setMemberId(2L);
        loan2.setBookCopyId(2L);
        loan2.setLoanDate(LocalDate.now());
        loan2.setDueDate(LocalDate.now().plusDays(14));
        loan2.setStatus(Loan.LoanStatus.ACTIVE);
        loanDAO.save(loan2);

        // When
        List<Loan> allLoans = loanDAO.findAll();

        // Then
        assertTrue(allLoans.size() >= 2);
        assertTrue(allLoans.stream().anyMatch(l -> l.getMemberId().equals(1L)));
        assertTrue(allLoans.stream().anyMatch(l -> l.getMemberId().equals(2L)));
    }

    @Test
    @Order(20)
    @Transactional
    void testClearAll() throws DAOException {
        // Given
        loanDAO.save(testLoan);

        Loan loan2 = new Loan();
        loan2.setMemberId(2L);
        loan2.setBookCopyId(2L);
        loan2.setLoanDate(LocalDate.now());
        loan2.setDueDate(LocalDate.now().plusDays(14));
        loan2.setStatus(Loan.LoanStatus.ACTIVE);
        loanDAO.save(loan2);

        assertTrue(loanDAO.count() >= 2);

        // When
        loanDAO.clearAll();

        // Then
        assertEquals(0, loanDAO.count());
        assertEquals(0, loanDAO.findAll().size());
    }
}