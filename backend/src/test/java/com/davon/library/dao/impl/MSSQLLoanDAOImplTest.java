package com.davon.library.dao.impl;

import com.davon.library.dao.LoanDAO;
import com.davon.library.dao.DAOException;
import com.davon.library.model.*;
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
        testMember = new Member();
        testMember.setId(1L);
        testMember.setFullName("John Doe");
        testMember.setUsername("john.doe");

        testBookCopy = new BookCopy();
        testBookCopy.setId(1L);
        testBookCopy.setStatus(BookCopy.CopyStatus.AVAILABLE);

        testLoan = new Loan();
        testLoan.setMember(testMember);
        testLoan.setBookCopy(testBookCopy);
        testLoan.setCheckoutDate(LocalDate.now());
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
        assertEquals(testLoan.getMember().getId(), savedLoan.getMember().getId());
        assertEquals(testLoan.getBookCopy().getId(), savedLoan.getBookCopy().getId());
        assertEquals(testLoan.getCheckoutDate(), savedLoan.getCheckoutDate());
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
        assertEquals(savedLoan.getMember().getId(), foundLoan.get().getMember().getId());
        assertEquals(savedLoan.getBookCopy().getId(), foundLoan.get().getBookCopy().getId());
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

        Member member2 = new Member();
        member2.setId(2L);
        BookCopy bookCopy2 = new BookCopy();
        bookCopy2.setId(2L);

        Loan loan2 = new Loan();
        loan2.setMember(member2);
        loan2.setBookCopy(bookCopy2);
        loan2.setCheckoutDate(LocalDate.now());
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

        Member member2 = new Member();
        member2.setId(1L); // Same member
        BookCopy bookCopy2 = new BookCopy();
        bookCopy2.setId(2L);

        Loan loan2 = new Loan();
        loan2.setMember(member2);
        loan2.setBookCopy(bookCopy2);
        loan2.setCheckoutDate(LocalDate.now());
        loan2.setDueDate(LocalDate.now().plusDays(14));
        loan2.setStatus(Loan.LoanStatus.ACTIVE);
        loanDAO.save(loan2);

        Member member3 = new Member();
        member3.setId(3L); // Different member
        BookCopy bookCopy3 = new BookCopy();
        bookCopy3.setId(3L);

        Loan loan3 = new Loan();
        loan3.setMember(member3);
        loan3.setBookCopy(bookCopy3);
        loan3.setCheckoutDate(LocalDate.now());
        loan3.setDueDate(LocalDate.now().plusDays(14));
        loan3.setStatus(Loan.LoanStatus.ACTIVE);
        loanDAO.save(loan3);

        // When
        List<Loan> memberLoans = loanDAO.findByMember(testMember);

        // Then
        assertEquals(2, memberLoans.size());
        assertTrue(memberLoans.stream().allMatch(loan -> loan.getMember().getId().equals(1L)));
    }

    @Test
    @Order(8)
    @Transactional
    void testFindActiveLoansByMember() throws DAOException {
        // Given
        loanDAO.save(testLoan); // ACTIVE

        Member member1 = new Member();
        member1.setId(1L);
        BookCopy bookCopy2 = new BookCopy();
        bookCopy2.setId(2L);

        Loan returnedLoan = new Loan();
        returnedLoan.setMember(member1);
        returnedLoan.setBookCopy(bookCopy2);
        returnedLoan.setCheckoutDate(LocalDate.now().minusDays(7));
        returnedLoan.setDueDate(LocalDate.now().minusDays(1));
        returnedLoan.setReturnDate(LocalDate.now());
        returnedLoan.setStatus(Loan.LoanStatus.RETURNED);
        loanDAO.save(returnedLoan);

        BookCopy bookCopy3 = new BookCopy();
        bookCopy3.setId(3L);

        Loan overdueLoan = new Loan();
        overdueLoan.setMember(member1);
        overdueLoan.setBookCopy(bookCopy3);
        overdueLoan.setCheckoutDate(LocalDate.now().minusDays(20));
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

        Member member1 = new Member();
        member1.setId(1L);
        BookCopy bookCopy2 = new BookCopy();
        bookCopy2.setId(2L);

        Loan returnedLoan = new Loan();
        returnedLoan.setMember(member1);
        returnedLoan.setBookCopy(bookCopy2);
        returnedLoan.setCheckoutDate(LocalDate.now().minusDays(7));
        returnedLoan.setDueDate(LocalDate.now().minusDays(1));
        returnedLoan.setReturnDate(LocalDate.now());
        returnedLoan.setStatus(Loan.LoanStatus.RETURNED);
        loanDAO.save(returnedLoan);

        // When
        long activeCount = loanDAO.countActiveLoansByMember(testMember);

        // Then
        assertEquals(1, activeCount); // Only the ACTIVE loan
    }

    @Test
    @Order(10)
    @Transactional
    void testFindByBookCopyAndMember() throws DAOException {
        // Given
        Loan savedLoan = loanDAO.save(testLoan);

        Member member2 = new Member();
        member2.setId(2L);

        Loan returnedLoan = new Loan();
        returnedLoan.setMember(member2);
        returnedLoan.setBookCopy(testBookCopy); // Same book copy
        returnedLoan.setCheckoutDate(LocalDate.now().minusDays(7));
        returnedLoan.setDueDate(LocalDate.now().minusDays(1));
        returnedLoan.setReturnDate(LocalDate.now());
        returnedLoan.setStatus(Loan.LoanStatus.RETURNED);
        loanDAO.save(returnedLoan);

        // When
        Optional<Loan> foundLoan = loanDAO.findByBookCopyAndMember(testBookCopy, testMember);

        // Then
        assertTrue(foundLoan.isPresent());
        assertEquals(testLoan.getMember().getId(), foundLoan.get().getMember().getId());
        assertEquals(testLoan.getBookCopy().getId(), foundLoan.get().getBookCopy().getId());
    }

    @Test
    @Order(11)
    @Transactional
    void testFindActiveByBookCopy() throws DAOException {
        // Given
        loanDAO.save(testLoan); // ACTIVE

        BookCopy bookCopy1 = new BookCopy();
        bookCopy1.setId(1L);
        Member member2 = new Member();
        member2.setId(2L);

        Loan returnedLoan = new Loan();
        returnedLoan.setMember(member2);
        returnedLoan.setBookCopy(bookCopy1); // Same book copy
        returnedLoan.setCheckoutDate(LocalDate.now().minusDays(7));
        returnedLoan.setDueDate(LocalDate.now().minusDays(1));
        returnedLoan.setReturnDate(LocalDate.now());
        returnedLoan.setStatus(Loan.LoanStatus.RETURNED);
        loanDAO.save(returnedLoan);

        // When
        Optional<Loan> activeLoan = loanDAO.findActiveByBookCopy(testBookCopy);

        // Then
        assertTrue(activeLoan.isPresent());
        assertEquals(1L, activeLoan.get().getMember().getId());
    }

    @Test
    @Order(12)
    @Transactional
    void testFindOverdueLoans() throws DAOException {
        // Given
        Member member1 = new Member();
        member1.setId(1L);
        BookCopy bookCopy1 = new BookCopy();
        bookCopy1.setId(1L);

        Loan overdueLoan1 = new Loan();
        overdueLoan1.setMember(member1);
        overdueLoan1.setBookCopy(bookCopy1);
        overdueLoan1.setCheckoutDate(LocalDate.now().minusDays(20));
        overdueLoan1.setDueDate(LocalDate.now().minusDays(6));
        overdueLoan1.setStatus(Loan.LoanStatus.ACTIVE);
        loanDAO.save(overdueLoan1);

        Member member2 = new Member();
        member2.setId(2L);
        BookCopy bookCopy2 = new BookCopy();
        bookCopy2.setId(2L);

        Loan currentLoan = new Loan();
        currentLoan.setMember(member2);
        currentLoan.setBookCopy(bookCopy2);
        currentLoan.setCheckoutDate(LocalDate.now());
        currentLoan.setDueDate(LocalDate.now().plusDays(14));
        currentLoan.setStatus(Loan.LoanStatus.ACTIVE);
        loanDAO.save(currentLoan);

        // When
        List<Loan> overdueLoans = loanDAO.findOverdueLoans(LocalDate.now());

        // Then
        assertEquals(1, overdueLoans.size());
        assertTrue(overdueLoans.get(0).getDueDate().isBefore(LocalDate.now()));
    }

    @Test
    @Order(13)
    @Transactional
    void testFindLoansDueOn() throws DAOException {
        // Given
        LocalDate today = LocalDate.now();
        LocalDate targetDueDate = today.plusDays(1);

        Member member1 = new Member();
        member1.setId(1L);
        BookCopy bookCopy1 = new BookCopy();
        bookCopy1.setId(1L);

        Loan dueTodayLoan = new Loan();
        dueTodayLoan.setMember(member1);
        dueTodayLoan.setBookCopy(bookCopy1);
        dueTodayLoan.setCheckoutDate(LocalDate.now().minusDays(11));
        dueTodayLoan.setDueDate(targetDueDate);
        dueTodayLoan.setStatus(Loan.LoanStatus.ACTIVE);
        loanDAO.save(dueTodayLoan);

        Member member2 = new Member();
        member2.setId(2L);
        BookCopy bookCopy2 = new BookCopy();
        bookCopy2.setId(2L);

        Loan dueLaterLoan = new Loan();
        dueLaterLoan.setMember(member2);
        dueLaterLoan.setBookCopy(bookCopy2);
        dueLaterLoan.setCheckoutDate(LocalDate.now());
        dueLaterLoan.setDueDate(LocalDate.now().plusDays(14));
        dueLaterLoan.setStatus(Loan.LoanStatus.ACTIVE);
        loanDAO.save(dueLaterLoan);

        // When
        List<Loan> loansDueOn = loanDAO.findLoansDueOn(targetDueDate);

        // Then
        assertEquals(1, loansDueOn.size());
        assertEquals(targetDueDate, loansDueOn.get(0).getDueDate());
    }

    @Test
    @Order(14)
    @Transactional
    void testFindByStatus() throws DAOException {
        // Given
        loanDAO.save(testLoan); // ACTIVE

        Member member2 = new Member();
        member2.setId(2L);
        BookCopy bookCopy2 = new BookCopy();
        bookCopy2.setId(2L);

        Loan returnedLoan = new Loan();
        returnedLoan.setMember(member2);
        returnedLoan.setBookCopy(bookCopy2);
        returnedLoan.setCheckoutDate(LocalDate.now().minusDays(7));
        returnedLoan.setDueDate(LocalDate.now().minusDays(1));
        returnedLoan.setReturnDate(LocalDate.now());
        returnedLoan.setStatus(Loan.LoanStatus.RETURNED);
        loanDAO.save(returnedLoan);

        Member member3 = new Member();
        member3.setId(3L);
        BookCopy bookCopy3 = new BookCopy();
        bookCopy3.setId(3L);

        Loan overdueLoan = new Loan();
        overdueLoan.setMember(member3);
        overdueLoan.setBookCopy(bookCopy3);
        overdueLoan.setCheckoutDate(LocalDate.now().minusDays(20));
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

        // Verify it exists
        assertTrue(loanDAO.existsById(loanId));

        // When
        loanDAO.deleteById(loanId);

        // Then
        assertFalse(loanDAO.existsById(loanId));
    }

    @Test
    @Order(16)
    @Transactional
    void testDelete() throws DAOException {
        // Given
        Loan savedLoan = loanDAO.save(testLoan);
        Long loanId = savedLoan.getId();

        // Verify it exists
        assertTrue(loanDAO.existsById(loanId));

        // When
        loanDAO.delete(savedLoan);

        // Then
        assertFalse(loanDAO.existsById(loanId));
    }

    @Test
    @Order(17)
    void testDeleteNonExistentLoan() {
        // Given
        Member member = new Member();
        member.setId(1L);
        BookCopy bookCopy = new BookCopy();
        bookCopy.setId(1L);

        Loan nonExistentLoan = new Loan();
        nonExistentLoan.setMember(member);
        nonExistentLoan.setBookCopy(bookCopy);
        nonExistentLoan.setCheckoutDate(LocalDate.now());
        nonExistentLoan.setDueDate(LocalDate.now().plusDays(14));
        nonExistentLoan.setStatus(Loan.LoanStatus.ACTIVE);
        nonExistentLoan.setId(999999L); // Non-existent ID

        // When & Then
        assertThrows(DAOException.class, () -> loanDAO.delete(nonExistentLoan));
    }

    @Test
    @Order(18)
    void testUpdateNonExistentLoan() {
        // Given
        Member member = new Member();
        member.setId(1L);
        BookCopy bookCopy = new BookCopy();
        bookCopy.setId(1L);

        Loan nonExistentLoan = new Loan();
        nonExistentLoan.setMember(member);
        nonExistentLoan.setBookCopy(bookCopy);
        nonExistentLoan.setCheckoutDate(LocalDate.now());
        nonExistentLoan.setDueDate(LocalDate.now().plusDays(14));
        nonExistentLoan.setStatus(Loan.LoanStatus.ACTIVE);
        nonExistentLoan.setId(999999L); // Non-existent ID

        // When & Then
        assertThrows(DAOException.class, () -> loanDAO.update(nonExistentLoan));
    }

    @Test
    @Order(19)
    @Transactional
    void testFindAll() throws DAOException {
        // Given
        loanDAO.save(testLoan);

        Member member2 = new Member();
        member2.setId(2L);
        BookCopy bookCopy2 = new BookCopy();
        bookCopy2.setId(2L);

        Loan loan2 = new Loan();
        loan2.setMember(member2);
        loan2.setBookCopy(bookCopy2);
        loan2.setCheckoutDate(LocalDate.now());
        loan2.setDueDate(LocalDate.now().plusDays(14));
        loan2.setStatus(Loan.LoanStatus.ACTIVE);
        loanDAO.save(loan2);

        // When
        List<Loan> allLoans = loanDAO.findAll();

        // Then
        assertTrue(allLoans.size() >= 2);
        assertTrue(allLoans.stream().anyMatch(l -> l.getMember().getId().equals(1L)));
        assertTrue(allLoans.stream().anyMatch(l -> l.getMember().getId().equals(2L)));
    }

    @Test
    @Order(20)
    @Transactional
    void testClearAll() throws DAOException {
        // Given
        loanDAO.save(testLoan);

        Member member2 = new Member();
        member2.setId(2L);
        BookCopy bookCopy2 = new BookCopy();
        bookCopy2.setId(2L);

        Loan loan2 = new Loan();
        loan2.setMember(member2);
        loan2.setBookCopy(bookCopy2);
        loan2.setCheckoutDate(LocalDate.now());
        loan2.setDueDate(LocalDate.now().plusDays(14));
        loan2.setStatus(Loan.LoanStatus.ACTIVE);
        loanDAO.save(loan2);

        // Verify we have data
        assertTrue(loanDAO.count() >= 2);

        // When
        loanDAO.clearAll();

        // Then
        assertEquals(0, loanDAO.count());
    }
}