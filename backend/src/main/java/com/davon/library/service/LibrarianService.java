package com.davon.library.service;

import com.davon.library.model.*;
import lombok.RequiredArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class LibrarianService {
    private final CatalogingService catalogingService;
    private final UserService userService;
    private final LoanManager loanManager;
    private final InventoryService inventoryService;
    private final TransactionManager transactionManager;
    private final ReportService reportService;

    // Catalog Management (High Priority)
    public Book addBookToCatalog(Book book) {
        return catalogingService.catalogNewBook(book);
    }

    public Book updateBookCatalog(Long bookId, Book updatedBook) {
        // Implementation that updates book details
        return updatedBook;
    }

    public boolean removeBookFromCatalog(Long bookId) {
        return inventoryService.removeBook(bookId);
    }

    // Member Account Management (Medium Priority)
    public Member registerMember(Member newMember) {
        return (Member) userService.createUser(newMember);
    }

    public Member updateMemberInfo(Long memberId, Member updatedMember) {
        return (Member) userService.updateUser(memberId, updatedMember);
    }

    public boolean deactivateMemberAccount(Long memberId) {
        return userService.deactivateUser(memberId);
    }

    // Circulation Management (High Priority)
    public Loan checkoutBookForMember(Long memberId, Long bookCopyId) {
        Member member = (Member) userService.getUsers().stream()
                .filter(u -> u.getId().equals(memberId))
                .findFirst()
                .orElse(null);

        BookCopy copy = inventoryService.getCopiesForBook(bookCopyId).stream()
                .filter(c -> c.isAvailable())
                .findFirst()
                .orElse(null);

        if (member == null || copy == null) {
            return null;
        }

        return loanManager.checkOutBook(member, copy);
    }

    public void returnBookFromMember(Long loanId) {
        loanManager.returnBook(loanId);
    }

    public boolean renewLoanForMember(Long loanId) {
        return loanManager.renewLoan(loanId);
    }

    // Report Generation (Low Priority)
    public Report generateDailyCirculationReport() {
        return reportService.generateDailyCirculation();
    }

    public Report generateOverdueItemsReport() {
        return reportService.generateOverdueItemsReport();
    }

    public Report generateInventoryStatusReport() {
        return reportService.generateInventoryStatusReport();
    }

    // Inventory Management
    public void performInventoryCheck(Map<Long, String> bookCopyStatuses) {
        bookCopyStatuses.forEach((copyId, status) -> {
            inventoryService.updateBookStatus(copyId, BookCopy.CopyStatus.valueOf(status));
        });
    }

    public void updateBookLocation(Long copyId, String newLocation) {
        inventoryService.updateBookCopyLocation(copyId, newLocation);
    }

    // Payment Processing (requires staff authorization)
    public Receipt processPayment(Double amount, String method, String description, Long staffId) {
        // Check staff authorization
        if (!hasFinancialAccess(staffId)) {
            throw new SecurityException("Staff member does not have financial access");
        }

        Transaction transaction = transactionManager.processPayment(amount, method, description);
        return transaction.generateReceipt();
    }

    private boolean hasFinancialAccess(Long staffId) {
        // Implementation to check if staff has financial access
        // This would typically check roles/permissions in a real system
        return true; // Placeholder
    }
}
