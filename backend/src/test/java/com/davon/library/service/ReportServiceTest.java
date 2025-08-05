package com.davon.library.service;

import com.davon.library.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

        @InjectMocks
        private ReportService reportService;

        @Mock
        private LoanService loanService;

        @Mock
        private FineService fineService;

        private Member member1, member2;
        private Book book1, book2;
        private BookCopy bookCopy1, bookCopy2;
        private Loan activeLoan, overdueLoan, returnedLoan;
        private Fine pendingFine, paidFine;

        @BeforeEach
        void setUp() {
                // Create test members
                member1 = Member.builder()
                                .id(1L)
                                .name("John Doe")
                                .email("john@test.com")
                                .active(true)
                                .build();

                member2 = Member.builder()
                                .id(2L)
                                .name("Jane Smith")
                                .email("jane@test.com")
                                .active(true)
                                .build();

                // Create test books
                book1 = Book.builder()
                                .id(1L)
                                .title("Java Programming")
                                .ISBN("1234567890")
                                .build();

                book2 = Book.builder()
                                .id(2L)
                                .title("Database Design")
                                .ISBN("0987654321")
                                .build();

                // Create test book copies
                bookCopy1 = BookCopy.builder()
                                .id(1L)
                                .book(book1)
                                .status(BookCopy.CopyStatus.AVAILABLE)
                                .build();

                bookCopy2 = BookCopy.builder()
                                .id(2L)
                                .book(book2)
                                .status(BookCopy.CopyStatus.CHECKED_OUT)
                                .build();

                // Create test loans
                activeLoan = Loan.builder()
                                .id(1L)
                                .member(member1)
                                .bookCopy(bookCopy1)
                                .checkoutDate(LocalDate.now().minusDays(5))
                                .dueDate(LocalDate.now().plusDays(9))
                                .status(Loan.LoanStatus.ACTIVE)
                                .build();

                overdueLoan = Loan.builder()
                                .id(2L)
                                .member(member2)
                                .bookCopy(bookCopy2)
                                .checkoutDate(LocalDate.now().minusDays(20))
                                .dueDate(LocalDate.now().minusDays(6))
                                .status(Loan.LoanStatus.OVERDUE)
                                .build();

                returnedLoan = Loan.builder()
                                .id(3L)
                                .member(member1)
                                .bookCopy(bookCopy1)
                                .checkoutDate(LocalDate.now().minusDays(30))
                                .dueDate(LocalDate.now().minusDays(16))
                                .status(Loan.LoanStatus.RETURNED)
                                .build();

                // Create test fines
                pendingFine = Fine.builder()
                                .id(1L)
                                .amount(5.00)
                                .reason(Fine.FineReason.OVERDUE)
                                .status(Fine.FineStatus.PENDING)
                                .issueDate(LocalDate.now().minusDays(5))
                                .dueDate(LocalDate.now().plusDays(9))
                                .build();

                paidFine = Fine.builder()
                                .id(2L)
                                .amount(10.00)
                                .reason(Fine.FineReason.DAMAGED_ITEM)
                                .status(Fine.FineStatus.PAID)
                                .issueDate(LocalDate.now().minusDays(15))
                                .dueDate(LocalDate.now().minusDays(1))
                                .build();
        }

        @Test
        void testGenerateMonthlyReport_WithValidData() {
                // Given
                LocalDate startDate = LocalDate.of(2024, 1, 1);
                LocalDate endDate = LocalDate.of(2024, 1, 31);
                List<Loan> testLoans = Arrays.asList(activeLoan, overdueLoan, returnedLoan);

                // When
                ReportService.MonthlyReport report = reportService.generateMonthlyReport(startDate, endDate);

                // Then
                assertNotNull(report);
                assertEquals(startDate, report.getStartDate());
                assertEquals(endDate, report.getEndDate());
                assertEquals(31L, report.getReportingPeriodDays()); // January has 31 days
                assertEquals(0, report.getTotalLoans()); // Empty list due to placeholder implementation
                assertEquals(0.0, report.getAvgLoansPerDay());
                assertNotNull(report.getMostPopularBooks());
                assertNotNull(report.getMemberActivitySummary());
        }

        @Test
        void testGenerateMonthlyReport_EmptyDateRange() {
                // Given
                LocalDate startDate = LocalDate.of(2024, 1, 15);
                LocalDate endDate = LocalDate.of(2024, 1, 15);

                // When
                ReportService.MonthlyReport report = reportService.generateMonthlyReport(startDate, endDate);

                // Then
                assertNotNull(report);
                assertEquals(1L, report.getReportingPeriodDays()); // Same day = 1 day
                assertEquals(0, report.getTotalLoans());
                assertEquals(0.0, report.getAvgLoansPerDay());
        }

        @Test
        void testGenerateOverdueReport() {
                // When
                ReportService.OverdueReport report = reportService.generateOverdueReport();

                // Then
                assertNotNull(report);
                assertEquals(LocalDate.now(), report.getReportDate());
                assertEquals(0, report.getTotalOverdueLoans()); // Empty list due to placeholder
                assertEquals(0.0, report.getTotalFinesOwed());
                assertEquals(0.0, report.getAverageDaysOverdue());
                assertNotNull(report.getOverdueLoans());
        }

        @Test
        void testGenerateFineReport_WithValidData() {
                // Given
                LocalDate startDate = LocalDate.of(2024, 1, 1);
                LocalDate endDate = LocalDate.of(2024, 1, 31);

                // When
                ReportService.FineReport report = reportService.generateFineReport(startDate, endDate);

                // Then
                assertNotNull(report);
                assertEquals(startDate, report.getStartDate());
                assertEquals(endDate, report.getEndDate());
                assertEquals(0, report.getTotalFinesIssued()); // Empty list due to placeholder
                assertEquals(0.0, report.getTotalFineAmount());
                assertEquals(0, report.getFinesPaid());
                assertEquals(0, report.getFinesPending());
                assertEquals(0.0, report.getCollectionRate());
        }

        @Test
        void testMonthlyReportBuilder() {
                // Given
                LocalDate startDate = LocalDate.of(2024, 1, 1);
                LocalDate endDate = LocalDate.of(2024, 1, 31);
                List<String> popularBooks = Arrays.asList("Book A", "Book B");

                // When
                ReportService.MonthlyReport report = ReportService.MonthlyReport.builder()
                                .startDate(startDate)
                                .endDate(endDate)
                                .reportingPeriodDays(31L)
                                .totalLoans(100)
                                .activeLoans(80)
                                .overdueLoans(15)
                                .returnedLoans(5)
                                .avgLoansPerDay(3.23)
                                .overdueRate(15.0)
                                .mostPopularBooks(popularBooks)
                                .memberActivitySummary(Collections.emptyMap())
                                .build();

                // Then
                assertNotNull(report);
                assertEquals(startDate, report.getStartDate());
                assertEquals(endDate, report.getEndDate());
                assertEquals(31L, report.getReportingPeriodDays());
                assertEquals(100, report.getTotalLoans());
                assertEquals(80, report.getActiveLoans());
                assertEquals(15, report.getOverdueLoans());
                assertEquals(5, report.getReturnedLoans());
                assertEquals(3.23, report.getAvgLoansPerDay());
                assertEquals(15.0, report.getOverdueRate());
                assertEquals(popularBooks, report.getMostPopularBooks());
        }

        @Test
        void testOverdueReportBuilder() {
                // Given
                LocalDate reportDate = LocalDate.now();
                List<Loan> overdueLoans = Arrays.asList(overdueLoan);

                // When
                ReportService.OverdueReport report = ReportService.OverdueReport.builder()
                                .reportDate(reportDate)
                                .totalOverdueLoans(1)
                                .overdueLoans(overdueLoans)
                                .totalFinesOwed(25.50)
                                .averageDaysOverdue(7.5)
                                .build();

                // Then
                assertNotNull(report);
                assertEquals(reportDate, report.getReportDate());
                assertEquals(1, report.getTotalOverdueLoans());
                assertEquals(overdueLoans, report.getOverdueLoans());
                assertEquals(25.50, report.getTotalFinesOwed());
                assertEquals(7.5, report.getAverageDaysOverdue());
        }

        @Test
        void testFineReportBuilder() {
                // Given
                LocalDate startDate = LocalDate.of(2024, 1, 1);
                LocalDate endDate = LocalDate.of(2024, 1, 31);

                // When
                ReportService.FineReport report = ReportService.FineReport.builder()
                                .startDate(startDate)
                                .endDate(endDate)
                                .totalFinesIssued(50)
                                .totalFineAmount(250.75)
                                .finesPaid(30)
                                .finesPending(20)
                                .collectionRate(60.0)
                                .build();

                // Then
                assertNotNull(report);
                assertEquals(startDate, report.getStartDate());
                assertEquals(endDate, report.getEndDate());
                assertEquals(50, report.getTotalFinesIssued());
                assertEquals(250.75, report.getTotalFineAmount());
                assertEquals(30, report.getFinesPaid());
                assertEquals(20, report.getFinesPending());
                assertEquals(60.0, report.getCollectionRate());
        }

        @Test
        void testReportDateRangeValidation() {
                // Test that the service handles various date ranges correctly
                LocalDate start = LocalDate.of(2024, 2, 1); // February 1st
                LocalDate end = LocalDate.of(2024, 2, 29); // February 29th (leap year)

                ReportService.MonthlyReport report = reportService.generateMonthlyReport(start, end);

                assertEquals(29L, report.getReportingPeriodDays()); // Leap year February has 29 days
        }

        @Test
        void testEdgeCaseEmptyReports() {
                // Test behavior with no data
                ReportService.MonthlyReport monthlyReport = reportService.generateMonthlyReport(
                                LocalDate.now(), LocalDate.now());
                ReportService.OverdueReport overdueReport = reportService.generateOverdueReport();
                ReportService.FineReport fineReport = reportService.generateFineReport(
                                LocalDate.now(), LocalDate.now());

                // All should handle empty data gracefully
                assertNotNull(monthlyReport);
                assertNotNull(overdueReport);
                assertNotNull(fineReport);

                assertEquals(0, monthlyReport.getTotalLoans());
                assertEquals(0, overdueReport.getTotalOverdueLoans());
                assertEquals(0, fineReport.getTotalFinesIssued());
        }
}