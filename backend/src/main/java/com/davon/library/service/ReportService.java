package com.davon.library.service;

import com.davon.library.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class ReportService {

    @Inject
    LoanService loanService;

    @Inject
    FineService fineService;

    /**
     * Generates a comprehensive monthly report for library operations
     */
    public MonthlyReport generateMonthlyReport(LocalDate startDate, LocalDate endDate) {
        // Get all loans in the period (in a real app, this would be a database query)
        List<Loan> loansInPeriod = getLoansInPeriod(startDate, endDate);

        long reportingPeriodDays = ChronoUnit.DAYS.between(startDate, endDate) + 1; // +1 for inclusive end date

        return MonthlyReport.builder()
                .startDate(startDate)
                .endDate(endDate)
                .reportingPeriodDays(reportingPeriodDays)
                .totalLoans(loansInPeriod.size())
                .activeLoans(countLoansByStatus(loansInPeriod, Loan.LoanStatus.ACTIVE))
                .overdueLoans(countLoansByStatus(loansInPeriod, Loan.LoanStatus.OVERDUE))
                .returnedLoans(countLoansByStatus(loansInPeriod, Loan.LoanStatus.RETURNED))
                .avgLoansPerDay(calculateAverageLoansPerDay(loansInPeriod.size(), reportingPeriodDays))
                .overdueRate(calculateOverdueRate(loansInPeriod))
                .mostPopularBooks(findMostPopularBooks(loansInPeriod, 5))
                .memberActivitySummary(generateMemberActivitySummary(loansInPeriod))
                .build();
    }

    /**
     * Generates an overdue books report
     */
    public OverdueReport generateOverdueReport() {
        List<Loan> overdueLoans = getOverdueLoans();

        return OverdueReport.builder()
                .reportDate(LocalDate.now())
                .totalOverdueLoans(overdueLoans.size())
                .overdueLoans(overdueLoans)
                .totalFinesOwed(calculateTotalFinesOwed(overdueLoans))
                .averageDaysOverdue(calculateAverageDaysOverdue(overdueLoans))
                .build();
    }

    /**
     * Generates a fine collection report
     */
    public FineReport generateFineReport(LocalDate startDate, LocalDate endDate) {
        List<Fine> finesInPeriod = getFinesInPeriod(startDate, endDate);

        return FineReport.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalFinesIssued(finesInPeriod.size())
                .totalFineAmount(calculateTotalFineAmount(finesInPeriod))
                .finesPaid(countFinesByStatus(finesInPeriod, Fine.FineStatus.PAID))
                .finesPending(countFinesByStatus(finesInPeriod, Fine.FineStatus.PENDING))
                .collectionRate(calculateFineCollectionRate(finesInPeriod))
                .build();
    }

    // Helper methods
    private List<Loan> getLoansInPeriod(LocalDate startDate, LocalDate endDate) {
        // In a real application, this would be a database query
        // For now, we'll use the service layer
        return Collections.emptyList(); // Placeholder
    }

    private List<Loan> getOverdueLoans() {
        // In a real application, this would be a database query
        return Collections.emptyList(); // Placeholder
    }

    private List<Fine> getFinesInPeriod(LocalDate startDate, LocalDate endDate) {
        // In a real application, this would be a database query
        return Collections.emptyList(); // Placeholder
    }

    private int countLoansByStatus(List<Loan> loans, Loan.LoanStatus status) {
        return (int) loans.stream()
                .filter(loan -> loan.getStatus() == status)
                .count();
    }

    private int countFinesByStatus(List<Fine> fines, Fine.FineStatus status) {
        return (int) fines.stream()
                .filter(fine -> fine.getStatus() == status)
                .count();
    }

    private double calculateAverageLoansPerDay(int totalLoans, long days) {
        return days > 0 ? (double) totalLoans / days : 0.0;
    }

    private double calculateOverdueRate(List<Loan> loans) {
        if (loans.isEmpty())
            return 0.0;

        long overdueCount = loans.stream()
                .filter(loan -> loan.getStatus() == Loan.LoanStatus.OVERDUE)
                .count();

        return (double) overdueCount / loans.size() * 100;
    }

    private List<String> findMostPopularBooks(List<Loan> loans, int limit) {
        return loans.stream()
                .collect(Collectors.groupingBy(
                        loan -> loan.getBookCopy().getBook().getTitle(),
                        Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private Map<String, Integer> generateMemberActivitySummary(List<Loan> loans) {
        return loans.stream()
                .collect(Collectors.groupingBy(
                        loan -> loan.getMember().getFullName(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));
    }

    private double calculateTotalFinesOwed(List<Loan> overdueLoans) {
        return overdueLoans.stream()
                .mapToDouble(loan -> {
                    Fine fine = fineService.calculateOverdueFine(loan);
                    return fine != null ? fine.getAmount() : 0.0;
                })
                .sum();
    }

    private double calculateAverageDaysOverdue(List<Loan> overdueLoans) {
        if (overdueLoans.isEmpty())
            return 0.0;

        return overdueLoans.stream()
                .mapToInt(Loan::getOverdueDays)
                .average()
                .orElse(0.0);
    }

    private double calculateTotalFineAmount(List<Fine> fines) {
        return fines.stream()
                .mapToDouble(Fine::getAmount)
                .sum();
    }

    private double calculateFineCollectionRate(List<Fine> fines) {
        if (fines.isEmpty())
            return 0.0;

        long paidFines = fines.stream()
                .filter(fine -> fine.getStatus() == Fine.FineStatus.PAID)
                .count();

        return (double) paidFines / fines.size() * 100;
    }

    // Report DTOs
    public static class MonthlyReport {
        private LocalDate startDate;
        private LocalDate endDate;
        private long reportingPeriodDays;
        private int totalLoans;
        private int activeLoans;
        private int overdueLoans;
        private int returnedLoans;
        private double avgLoansPerDay;
        private double overdueRate;
        private List<String> mostPopularBooks;
        private Map<String, Integer> memberActivitySummary;

        // Builder pattern
        public static MonthlyReportBuilder builder() {
            return new MonthlyReportBuilder();
        }

        // Getters
        public LocalDate getStartDate() {
            return startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public long getReportingPeriodDays() {
            return reportingPeriodDays;
        }

        public int getTotalLoans() {
            return totalLoans;
        }

        public int getActiveLoans() {
            return activeLoans;
        }

        public int getOverdueLoans() {
            return overdueLoans;
        }

        public int getReturnedLoans() {
            return returnedLoans;
        }

        public double getAvgLoansPerDay() {
            return avgLoansPerDay;
        }

        public double getOverdueRate() {
            return overdueRate;
        }

        public List<String> getMostPopularBooks() {
            return mostPopularBooks;
        }

        public Map<String, Integer> getMemberActivitySummary() {
            return memberActivitySummary;
        }

        public static class MonthlyReportBuilder {
            private MonthlyReport report = new MonthlyReport();

            public MonthlyReportBuilder startDate(LocalDate startDate) {
                report.startDate = startDate;
                return this;
            }

            public MonthlyReportBuilder endDate(LocalDate endDate) {
                report.endDate = endDate;
                return this;
            }

            public MonthlyReportBuilder reportingPeriodDays(long days) {
                report.reportingPeriodDays = days;
                return this;
            }

            public MonthlyReportBuilder totalLoans(int total) {
                report.totalLoans = total;
                return this;
            }

            public MonthlyReportBuilder activeLoans(int active) {
                report.activeLoans = active;
                return this;
            }

            public MonthlyReportBuilder overdueLoans(int overdue) {
                report.overdueLoans = overdue;
                return this;
            }

            public MonthlyReportBuilder returnedLoans(int returned) {
                report.returnedLoans = returned;
                return this;
            }

            public MonthlyReportBuilder avgLoansPerDay(double avg) {
                report.avgLoansPerDay = avg;
                return this;
            }

            public MonthlyReportBuilder overdueRate(double rate) {
                report.overdueRate = rate;
                return this;
            }

            public MonthlyReportBuilder mostPopularBooks(List<String> books) {
                report.mostPopularBooks = books;
                return this;
            }

            public MonthlyReportBuilder memberActivitySummary(Map<String, Integer> summary) {
                report.memberActivitySummary = summary;
                return this;
            }

            public MonthlyReport build() {
                return report;
            }
        }
    }

    public static class OverdueReport {
        private LocalDate reportDate;
        private int totalOverdueLoans;
        private List<Loan> overdueLoans;
        private double totalFinesOwed;
        private double averageDaysOverdue;

        public static OverdueReportBuilder builder() {
            return new OverdueReportBuilder();
        }

        // Getters
        public LocalDate getReportDate() {
            return reportDate;
        }

        public int getTotalOverdueLoans() {
            return totalOverdueLoans;
        }

        public List<Loan> getOverdueLoans() {
            return overdueLoans;
        }

        public double getTotalFinesOwed() {
            return totalFinesOwed;
        }

        public double getAverageDaysOverdue() {
            return averageDaysOverdue;
        }

        public static class OverdueReportBuilder {
            private OverdueReport report = new OverdueReport();

            public OverdueReportBuilder reportDate(LocalDate date) {
                report.reportDate = date;
                return this;
            }

            public OverdueReportBuilder totalOverdueLoans(int total) {
                report.totalOverdueLoans = total;
                return this;
            }

            public OverdueReportBuilder overdueLoans(List<Loan> loans) {
                report.overdueLoans = loans;
                return this;
            }

            public OverdueReportBuilder totalFinesOwed(double amount) {
                report.totalFinesOwed = amount;
                return this;
            }

            public OverdueReportBuilder averageDaysOverdue(double average) {
                report.averageDaysOverdue = average;
                return this;
            }

            public OverdueReport build() {
                return report;
            }
        }
    }

    public static class FineReport {
        private LocalDate startDate;
        private LocalDate endDate;
        private int totalFinesIssued;
        private double totalFineAmount;
        private int finesPaid;
        private int finesPending;
        private double collectionRate;

        public static FineReportBuilder builder() {
            return new FineReportBuilder();
        }

        // Getters
        public LocalDate getStartDate() {
            return startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public int getTotalFinesIssued() {
            return totalFinesIssued;
        }

        public double getTotalFineAmount() {
            return totalFineAmount;
        }

        public int getFinesPaid() {
            return finesPaid;
        }

        public int getFinesPending() {
            return finesPending;
        }

        public double getCollectionRate() {
            return collectionRate;
        }

        public static class FineReportBuilder {
            private FineReport report = new FineReport();

            public FineReportBuilder startDate(LocalDate startDate) {
                report.startDate = startDate;
                return this;
            }

            public FineReportBuilder endDate(LocalDate endDate) {
                report.endDate = endDate;
                return this;
            }

            public FineReportBuilder totalFinesIssued(int total) {
                report.totalFinesIssued = total;
                return this;
            }

            public FineReportBuilder totalFineAmount(double amount) {
                report.totalFineAmount = amount;
                return this;
            }

            public FineReportBuilder finesPaid(int paid) {
                report.finesPaid = paid;
                return this;
            }

            public FineReportBuilder finesPending(int pending) {
                report.finesPending = pending;
                return this;
            }

            public FineReportBuilder collectionRate(double rate) {
                report.collectionRate = rate;
                return this;
            }

            public FineReport build() {
                return report;
            }
        }
    }
}