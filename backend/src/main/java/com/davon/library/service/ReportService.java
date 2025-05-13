package com.davon.library.service;

import com.davon.library.model.*;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class ReportService {
    private final LoanRepository loanRepository;
    private final InventoryService inventoryService;
    private final UserService userService;

    public Report generateDailyCirculation() {
        LocalDate today = LocalDate.now();
        // Count checkouts and returns for today
        int checkouts = countCheckoutsForDate(today);
        int returns = countReturnsForDate(today);

        Report report = Report.builder()
                .title("Daily Circulation Report")
                .dateGenerated(today)
                .build();

        // Add report content
        report.addContent("Date", today.toString());
        report.addContent("Total Checkouts", String.valueOf(checkouts));
        report.addContent("Total Returns", String.valueOf(returns));

        return report;
    }

    public Report generateOverdueItemsReport() {
        // Implementation to generate overdue items report
        Report report = Report.builder()
                .title("Overdue Items Report")
                .dateGenerated(LocalDate.now())
                .build();

        // Add report content
        // ...

        return report;
    }

    public Report generateInventoryStatusReport() {
        int totalBooks = inventoryService.getTotalBooks();
        List<Book> availableBooks = inventoryService.getAvailableBooks();

        Report report = Report.builder()
                .title("Inventory Status Report")
                .dateGenerated(LocalDate.now())
                .build();

        report.addContent("Total Books", String.valueOf(totalBooks));
        report.addContent("Available Books", String.valueOf(availableBooks.size()));

        return report;
    }

    public Report generateMembershipReport() {
        int totalMembers = countActiveMembers();
        int newMembersThisMonth = countNewMembersThisMonth();

        Report report = Report.builder()
                .title("Membership Report")
                .dateGenerated(LocalDate.now())
                .build();

        report.addContent("Total Active Members", String.valueOf(totalMembers));
        report.addContent("New Members This Month", String.valueOf(newMembersThisMonth));

        return report;
    }

    // Helper methods
    private int countCheckoutsForDate(LocalDate date) {
        // Implementation to count checkouts for a specific date
        return 0; // Placeholder
    }

    private int countReturnsForDate(LocalDate date) {
        // Implementation to count returns for a specific date
        return 0; // Placeholder
    }

    private int countActiveMembers() {
        // Count active members
        return (int) userService.getUsers().stream()
                .filter(user -> user instanceof Member && user.isActive())
                .count();
    }

    private int countNewMembersThisMonth() {
        // Count new members this month
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);

        // This is a placeholder - in a real implementation we would check
        // member registration date
        return 0;
    }
}
