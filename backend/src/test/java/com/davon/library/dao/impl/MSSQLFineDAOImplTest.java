package com.davon.library.dao.impl;

import com.davon.library.dao.FineDAO;
import com.davon.library.dao.DAOException;
import com.davon.library.database.DatabaseConnectionManager;
import com.davon.library.model.Fine;
import com.davon.library.model.Member;
import com.davon.library.model.Transaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MSSQLFineDAOImplTest {

    @Inject
    FineDAO fineDAO;

    @Inject
    DatabaseConnectionManager connectionManager;

    private Member testMember;
    private Fine testFine;

    @BeforeEach
    void setUp() throws SQLException {
        // Clean up test data
        cleanUpTestData();

        // Set up test member (assuming it exists in DB with id=1)
        testMember = Member.builder()
                .id(1L)
                .membershipStartDate(LocalDate.now().minusDays(30))
                .membershipEndDate(LocalDate.now().plusDays(335))
                .build();

        // Set up test fine
        testFine = Fine.builder()
                .member(testMember)
                .amount(15.00)
                .reason(Fine.FineReason.OVERDUE)
                .issueDate(LocalDate.now().minusDays(7))
                .dueDate(LocalDate.now().plusDays(30))
                .status(Fine.FineStatus.PENDING)
                .build();
    }

    private void cleanUpTestData() throws SQLException {
        try (Connection conn = connectionManager.getConnection()) {
            // Clean up fines table for testing
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM fines WHERE member_id = 1")) {
                stmt.executeUpdate();
            }
        }
    }

    @Test
    void testSaveFine() throws DAOException {
        // Act
        Fine savedFine = fineDAO.save(testFine);

        // Assert
        assertNotNull(savedFine);
        assertNotNull(savedFine.getId());
        assertEquals(testFine.getMember().getId(), savedFine.getMember().getId());
        assertEquals(testFine.getAmount(), savedFine.getAmount(), 0.01);
        assertEquals(testFine.getReason(), savedFine.getReason());
        assertEquals(testFine.getIssueDate(), savedFine.getIssueDate());
        assertEquals(testFine.getDueDate(), savedFine.getDueDate());
        assertEquals(testFine.getStatus(), savedFine.getStatus());
        assertNotNull(savedFine.getCreatedAt());
        assertNotNull(savedFine.getUpdatedAt());
    }

    @Test
    void testFindById() throws DAOException {
        // Arrange
        Fine savedFine = fineDAO.save(testFine);

        // Act
        Optional<Fine> foundFine = fineDAO.findById(savedFine.getId());

        // Assert
        assertTrue(foundFine.isPresent());
        Fine fine = foundFine.get();
        assertEquals(savedFine.getId(), fine.getId());
        assertEquals(savedFine.getMember().getId(), fine.getMember().getId());
        assertEquals(savedFine.getAmount(), fine.getAmount(), 0.01);
        assertEquals(savedFine.getReason(), fine.getReason());
        assertEquals(savedFine.getIssueDate(), fine.getIssueDate());
        assertEquals(savedFine.getDueDate(), fine.getDueDate());
        assertEquals(savedFine.getStatus(), fine.getStatus());
    }

    @Test
    void testFindByIdNotFound() {
        // Act
        Optional<Fine> foundFine = fineDAO.findById(999999L);

        // Assert
        assertFalse(foundFine.isPresent());
    }

    @Test
    void testUpdate() throws DAOException {
        // Arrange
        Fine savedFine = fineDAO.save(testFine);
        double newAmount = 25.50;
        Fine.FineStatus newStatus = Fine.FineStatus.PAID;
        Fine.FineReason newReason = Fine.FineReason.DAMAGED_ITEM;

        // Act
        savedFine.setAmount(newAmount);
        savedFine.setStatus(newStatus);
        savedFine.setReason(newReason);
        Fine updatedFine = fineDAO.update(savedFine);

        // Assert
        assertNotNull(updatedFine);
        assertEquals(savedFine.getId(), updatedFine.getId());
        assertEquals(newAmount, updatedFine.getAmount(), 0.01);
        assertEquals(newStatus, updatedFine.getStatus());
        assertEquals(newReason, updatedFine.getReason());

        // Verify in database
        Optional<Fine> foundFine = fineDAO.findById(savedFine.getId());
        assertTrue(foundFine.isPresent());
        assertEquals(newAmount, foundFine.get().getAmount(), 0.01);
        assertEquals(newStatus, foundFine.get().getStatus());
        assertEquals(newReason, foundFine.get().getReason());
    }

    @Test
    void testDelete() throws DAOException {
        // Arrange
        Fine savedFine = fineDAO.save(testFine);
        Long fineId = savedFine.getId();

        // Verify it exists
        assertTrue(fineDAO.existsById(fineId));

        // Act
        fineDAO.delete(savedFine);

        // Assert
        assertFalse(fineDAO.existsById(fineId));
        Optional<Fine> foundFine = fineDAO.findById(fineId);
        assertFalse(foundFine.isPresent());
    }

    @Test
    void testDeleteById() throws DAOException {
        // Arrange
        Fine savedFine = fineDAO.save(testFine);
        Long fineId = savedFine.getId();

        // Verify it exists
        assertTrue(fineDAO.existsById(fineId));

        // Act
        fineDAO.deleteById(fineId);

        // Assert
        assertFalse(fineDAO.existsById(fineId));
        Optional<Fine> foundFine = fineDAO.findById(fineId);
        assertFalse(foundFine.isPresent());
    }

    @Test
    void testExistsById() throws DAOException {
        // Arrange
        Fine savedFine = fineDAO.save(testFine);

        // Act & Assert
        assertTrue(fineDAO.existsById(savedFine.getId()));
        assertFalse(fineDAO.existsById(999999L));
    }

    @Test
    void testFindAll() throws DAOException {
        // Arrange
        Fine fine1 = fineDAO.save(testFine);

        Fine testFine2 = Fine.builder()
                .member(testMember)
                .amount(10.00)
                .reason(Fine.FineReason.LOST_ITEM)
                .issueDate(LocalDate.now().minusDays(5))
                .dueDate(LocalDate.now().plusDays(25))
                .status(Fine.FineStatus.DISPUTED)
                .build();
        Fine fine2 = fineDAO.save(testFine2);

        // Act
        List<Fine> allFines = fineDAO.findAll();

        // Assert
        assertNotNull(allFines);
        assertTrue(allFines.size() >= 2);
        assertTrue(allFines.stream().anyMatch(f -> f.getId().equals(fine1.getId())));
        assertTrue(allFines.stream().anyMatch(f -> f.getId().equals(fine2.getId())));
    }

    @Test
    void testCount() throws DAOException {
        // Arrange
        long initialCount = fineDAO.count();
        fineDAO.save(testFine);

        // Act
        long newCount = fineDAO.count();

        // Assert
        assertEquals(initialCount + 1, newCount);
    }

    @Test
    void testFindByMember() throws DAOException {
        // Arrange
        Fine fine1 = fineDAO.save(testFine);

        Fine testFine2 = Fine.builder()
                .member(testMember)
                .amount(5.00)
                .reason(Fine.FineReason.ADMINISTRATIVE)
                .issueDate(LocalDate.now().minusDays(3))
                .dueDate(LocalDate.now().plusDays(27))
                .status(Fine.FineStatus.PENDING)
                .build();
        Fine fine2 = fineDAO.save(testFine2);

        // Act
        List<Fine> memberFines = fineDAO.findByMember(testMember);

        // Assert
        assertNotNull(memberFines);
        assertTrue(memberFines.size() >= 2);
        assertTrue(memberFines.stream().anyMatch(f -> f.getId().equals(fine1.getId())));
        assertTrue(memberFines.stream().anyMatch(f -> f.getId().equals(fine2.getId())));
        assertTrue(memberFines.stream().allMatch(f -> f.getMember().getId().equals(testMember.getId())));
    }

    @Test
    void testFindUnpaidFinesByMember() throws DAOException {
        // Arrange
        Fine pendingFine = fineDAO.save(testFine);

        Fine paidFine = Fine.builder()
                .member(testMember)
                .amount(20.00)
                .reason(Fine.FineReason.DAMAGED_ITEM)
                .issueDate(LocalDate.now().minusDays(10))
                .dueDate(LocalDate.now().plusDays(20))
                .status(Fine.FineStatus.PAID)
                .build();
        fineDAO.save(paidFine);

        // Act
        List<Fine> unpaidFines = fineDAO.findUnpaidFinesByMember(testMember);

        // Assert
        assertNotNull(unpaidFines);
        assertTrue(unpaidFines.size() >= 1);
        assertTrue(unpaidFines.stream().anyMatch(f -> f.getId().equals(pendingFine.getId())));
        assertTrue(unpaidFines.stream().allMatch(f -> f.getStatus() == Fine.FineStatus.PENDING));
        assertTrue(unpaidFines.stream().allMatch(f -> f.getMember().getId().equals(testMember.getId())));
    }

    @Test
    void testGetTotalUnpaidAmount() throws DAOException {
        // Arrange
        double amount1 = 15.00;
        double amount2 = 10.00;

        testFine.setAmount(amount1);
        fineDAO.save(testFine);

        Fine testFine2 = Fine.builder()
                .member(testMember)
                .amount(amount2)
                .reason(Fine.FineReason.LOST_ITEM)
                .issueDate(LocalDate.now().minusDays(5))
                .dueDate(LocalDate.now().plusDays(25))
                .status(Fine.FineStatus.PENDING)
                .build();
        fineDAO.save(testFine2);

        // Add a paid fine that shouldn't be counted
        Fine paidFine = Fine.builder()
                .member(testMember)
                .amount(20.00)
                .reason(Fine.FineReason.DAMAGED_ITEM)
                .issueDate(LocalDate.now().minusDays(10))
                .dueDate(LocalDate.now().plusDays(20))
                .status(Fine.FineStatus.PAID)
                .build();
        fineDAO.save(paidFine);

        // Act
        double totalUnpaid = fineDAO.getTotalUnpaidAmount(testMember);

        // Assert
        assertEquals(amount1 + amount2, totalUnpaid, 0.01);
    }

    @Test
    void testFindByReason() throws DAOException {
        // Arrange
        Fine overdueFine = fineDAO.save(testFine);

        Fine damagedFine = Fine.builder()
                .member(testMember)
                .amount(50.00)
                .reason(Fine.FineReason.DAMAGED_ITEM)
                .issueDate(LocalDate.now().minusDays(5))
                .dueDate(LocalDate.now().plusDays(25))
                .status(Fine.FineStatus.PENDING)
                .build();
        fineDAO.save(damagedFine);

        // Act
        List<Fine> overdueFines = fineDAO.findByReason(Fine.FineReason.OVERDUE);
        List<Fine> damagedFines = fineDAO.findByReason(Fine.FineReason.DAMAGED_ITEM);

        // Assert
        assertNotNull(overdueFines);
        assertNotNull(damagedFines);
        assertTrue(overdueFines.stream().anyMatch(f -> f.getId().equals(overdueFine.getId())));
        assertTrue(damagedFines.stream().anyMatch(f -> f.getId().equals(damagedFine.getId())));
        assertTrue(overdueFines.stream().allMatch(f -> f.getReason() == Fine.FineReason.OVERDUE));
        assertTrue(damagedFines.stream().allMatch(f -> f.getReason() == Fine.FineReason.DAMAGED_ITEM));
    }

    @Test
    void testFindByStatus() throws DAOException {
        // Arrange
        Fine pendingFine = fineDAO.save(testFine);

        Fine paidFine = Fine.builder()
                .member(testMember)
                .amount(20.00)
                .reason(Fine.FineReason.DAMAGED_ITEM)
                .issueDate(LocalDate.now().minusDays(10))
                .dueDate(LocalDate.now().plusDays(20))
                .status(Fine.FineStatus.PAID)
                .build();
        fineDAO.save(paidFine);

        // Act
        List<Fine> pendingFines = fineDAO.findByStatus(Fine.FineStatus.PENDING);
        List<Fine> paidFines = fineDAO.findByStatus(Fine.FineStatus.PAID);

        // Assert
        assertNotNull(pendingFines);
        assertNotNull(paidFines);
        assertTrue(pendingFines.stream().anyMatch(f -> f.getId().equals(pendingFine.getId())));
        assertTrue(paidFines.stream().anyMatch(f -> f.getId().equals(paidFine.getId())));
        assertTrue(pendingFines.stream().allMatch(f -> f.getStatus() == Fine.FineStatus.PENDING));
        assertTrue(paidFines.stream().allMatch(f -> f.getStatus() == Fine.FineStatus.PAID));
    }

    @Test
    void testFindOverdueFines() throws DAOException {
        // Arrange
        LocalDate pastDueDate = LocalDate.now().minusDays(5);

        Fine overdueFine = Fine.builder()
                .member(testMember)
                .amount(15.00)
                .reason(Fine.FineReason.OVERDUE)
                .issueDate(LocalDate.now().minusDays(20))
                .dueDate(pastDueDate)
                .status(Fine.FineStatus.PENDING)
                .build();
        fineDAO.save(overdueFine);

        Fine currentFine = Fine.builder()
                .member(testMember)
                .amount(10.00)
                .reason(Fine.FineReason.OVERDUE)
                .issueDate(LocalDate.now().minusDays(5))
                .dueDate(LocalDate.now().plusDays(25))
                .status(Fine.FineStatus.PENDING)
                .build();
        fineDAO.save(currentFine);

        // Act
        List<Fine> overdueFines = fineDAO.findOverdueFines(LocalDate.now());

        // Assert
        assertNotNull(overdueFines);
        assertTrue(overdueFines.stream().anyMatch(f -> f.getId().equals(overdueFine.getId())));
        // The current fine should not be in overdue list
        assertFalse(overdueFines.stream().anyMatch(f -> f.getId().equals(currentFine.getId())));
    }

    @Test
    void testPayFine() throws DAOException {
        // Arrange
        testFine.setStatus(Fine.FineStatus.PENDING);

        // Act
        Transaction transaction = testFine.pay();

        // Assert
        assertEquals(Fine.FineStatus.PAID, testFine.getStatus());
        assertNotNull(transaction);
        assertEquals(testFine.getAmount(), transaction.getAmount(), 0.01);
        assertEquals(Transaction.TransactionType.FINE_PAYMENT, transaction.getType());
        assertEquals(LocalDate.now(), transaction.getDate());
        assertTrue(transaction.getDescription().contains("fine ID"));
    }

    @Test
    void testWaiveFine() {
        // Arrange
        testFine.setStatus(Fine.FineStatus.PENDING);

        // Act
        testFine.waive();

        // Assert
        assertEquals(Fine.FineStatus.WAIVED, testFine.getStatus());
    }

    @Test
    void testAdjustAmount() {
        // Arrange
        double originalAmount = testFine.getAmount();
        double newAmount = 30.00;

        // Act
        testFine.adjustAmount(newAmount);

        // Assert
        assertEquals(newAmount, testFine.getAmount(), 0.01);
        assertNotEquals(originalAmount, testFine.getAmount());
    }

    @Test
    void testDisputeFine() {
        // Arrange
        testFine.setStatus(Fine.FineStatus.PENDING);

        // Act
        boolean result = testFine.disputeFine("Item was returned on time");

        // Assert
        assertTrue(result);
        assertEquals(Fine.FineStatus.DISPUTED, testFine.getStatus());
    }

    @Test
    void testSaveWithNullMember() {
        // Arrange
        testFine.setMember(null);

        // Act & Assert
        assertThrows(DAOException.class, () -> fineDAO.save(testFine));
    }

    @Test
    void testUpdateNonExistentFine() {
        // Arrange
        testFine.setId(999999L);

        // Act & Assert
        assertThrows(DAOException.class, () -> fineDAO.update(testFine));
    }

    @Test
    void testDeleteNonExistentFine() {
        // Act & Assert
        assertThrows(DAOException.class, () -> fineDAO.deleteById(999999L));
    }
}