package com.davon.library.service;

import com.davon.library.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@ApplicationScoped
public class ReportService {

    public Map<String, Object> generateMonthlyReport(List<Loan> loans, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();

        long reportingPeriodDays = ChronoUnit.DAYS.between(startDate, endDate);

        int totalLoans = 0;
        int overdueLoans = 0;
        int returnedLoans = 0;

        for (Loan loan : loans) {
            // Check if loan falls within the reporting period
            if (isLoanInPeriod(loan, startDate, endDate)) {
                totalLoans++;

                if (loan.getStatus() == Loan.LoanStatus.OVERDUE) {
                    overdueLoans++;
                }

                if (loan.getStatus() == Loan.LoanStatus.RETURNED) {
                    returnedLoans++;
                }
            }
        }

        double avgLoansPerDay = reportingPeriodDays > 0 ? (double) totalLoans / reportingPeriodDays : 0;
        double overdueRate = totalLoans > 0 ? (double) overdueLoans / totalLoans * 100 : 0;

        report.put("reportingPeriodDays", reportingPeriodDays);
        report.put("totalLoans", totalLoans);
        report.put("overdueLoans", overdueLoans);
        report.put("returnedLoans", returnedLoans);
        report.put("avgLoansPerDay", avgLoansPerDay);
        report.put("overdueRate", overdueRate);

        return report;
    }

    private boolean isLoanInPeriod(Loan loan, LocalDate startDate, LocalDate endDate) {
        LocalDate checkoutDate = loan.getCheckoutDate();
        return !checkoutDate.isBefore(startDate) && !checkoutDate.isAfter(endDate);
    }
}