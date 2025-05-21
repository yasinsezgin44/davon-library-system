package com.davon.library.service;

import com.davon.library.model.Book;
import com.davon.library.model.Member;
import com.davon.library.model.Report;
import com.davon.library.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private UserService userService;

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reportService = new ReportService(loanRepository, inventoryService, userService);
    }

    @Test
    void testGenerateDailyCirculation() {
        // Act
        Report report = reportService.generateDailyCirculation();

        // Assert
        assertNotNull(report);
        assertEquals("Daily Circulation Report", report.getTitle());
        assertEquals(LocalDate.now(), report.getDateGenerated());
        assertTrue(report.getContent().containsKey("Date"));
        assertTrue(report.getContent().containsKey("Total Checkouts"));
        assertTrue(report.getContent().containsKey("Total Returns"));
    }

    @Test
    void testGenerateInventoryStatusReport() {
        // Arrange
        int totalBooks = 100;
        List<Book> availableBooks = new ArrayList<>();
        for (int i = 0; i < 75; i++) {
            availableBooks.add(new Book());
        }

        when(inventoryService.getTotalBooks()).thenReturn(totalBooks);
        when(inventoryService.getAvailableBooks()).thenReturn(availableBooks);

        // Act
        Report report = reportService.generateInventoryStatusReport();

        // Assert
        assertNotNull(report);
        assertEquals("Inventory Status Report", report.getTitle());
        assertEquals(LocalDate.now(), report.getDateGenerated());
        assertEquals("100", report.getContent().get("Total Books"));
        assertEquals("75", report.getContent().get("Available Books"));
    }

    @Test
    void testGenerateMembershipReport() {
        // Arrange
        Set<User> users = new HashSet<>();

        // Create 5 active members
        for (int i = 0; i < 5; i++) {
            Member member = mock(Member.class);
            when(member.isActive()).thenReturn(true);
            users.add(member);
        }

        // Create 2 inactive members
        for (int i = 0; i < 2; i++) {
            Member member = mock(Member.class);
            when(member.isActive()).thenReturn(false);
            users.add(member);
        }

        // Create 3 active non-member users (librarians, admins, etc.)
        for (int i = 0; i < 3; i++) {
            User user = mock(User.class);
            when(user.isActive()).thenReturn(true);
            users.add(user);
        }

        when(userService.getUsers()).thenReturn(users);

        // Act
        Report report = reportService.generateMembershipReport();

        // Assert
        assertNotNull(report);
        assertEquals("Membership Report", report.getTitle());
        assertEquals(LocalDate.now(), report.getDateGenerated());
        assertEquals("5", report.getContent().get("Total Active Members"));
        // We can't reliably test "New Members This Month" since it returns 0 in the
        // placeholder implementation
    }

    @Test
    void testGenerateOverdueItemsReport() {
        // Act
        Report report = reportService.generateOverdueItemsReport();

        // Assert
        assertNotNull(report);
        assertEquals("Overdue Items Report", report.getTitle());
        assertEquals(LocalDate.now(), report.getDateGenerated());
    }
}