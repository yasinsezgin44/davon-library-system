package com.davon.library.service;

import com.davon.library.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReportServiceTest {
    private ReportService reportService;
    private Member member;
    private Book book;
    private BookCopy bookCopy;

    @BeforeEach
    void setUp() {
        reportService = new ReportService();

        // Create test objects
        book = Book.builder()
                .id(1L)
                .title("Test Book")
                .ISBN("1234567890")
                .build();

        bookCopy = BookCopy.builder()
                .id(1L)
                .book(book)
                .status(BookCopy.CopyStatus.CHECKED_OUT)
                .build();

        member = Member.builder()
                .id(1L)
                .fullName("Test Member")
                .active(true)
                .build();
    }

    @Test
    void testMonthlyReportPeriodCalculation() {
        // Create test loans over a 30-day period
        LocalDate startDate = LocalDate.of(2024, 1, 1); // January 1st
        LocalDate endDate = LocalDate.of(2024, 1, 31); // January 31st

        // Create loans within this period
        Loan loan1 = Loan.builder()
                .id(1L)
                .member(member)
                .bookCopy(bookCopy)
                .checkoutDate(LocalDate.of(2024, 1, 5))
                .dueDate(LocalDate.of(2024, 1, 19))
                .status(Loan.LoanStatus.RETURNED)
                .build();

        Loan loan2 = Loan.builder()
                .id(2L)
                .member(member)
                .bookCopy(bookCopy)
                .checkoutDate(LocalDate.of(2024, 1, 15))
                .dueDate(LocalDate.of(2024, 1, 29))
                .status(Loan.LoanStatus.OVERDUE)
                .build();

        List<Loan> loans = List.of(loan1, loan2);

        // Generate report
        Map<String, Object> report = reportService.generateMonthlyReport(loans, startDate, endDate);

        assertEquals(30L, (Long) report.get("reportingPeriodDays"),
                "Reporting period should be 30 days, not months");

        double expectedAvgLoansPerDay = 2.0 / 30.0; // 2 loans over 30 days
        assertEquals(expectedAvgLoansPerDay, (Double) report.get("avgLoansPerDay"), 0.01,
                "Average loans per day should be calculated correctly");

        assertEquals(2, (Integer) report.get("totalLoans"));
        assertEquals(1, (Integer) report.get("overdueLoans"));
        assertEquals(1, (Integer) report.get("returnedLoans"));
        assertEquals(50.0, (Double) report.get("overdueRate"), 0.01);
    }
}