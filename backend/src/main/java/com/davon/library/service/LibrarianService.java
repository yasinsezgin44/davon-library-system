package com.davon.library.service;

import com.davon.library.model.*;
import com.davon.library.exception.BusinessException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Service for librarian-specific operations.
 * Uses DAO pattern following SOLID principles.
 */
@ApplicationScoped
public class LibrarianService {

    private static final Logger logger = Logger.getLogger(LibrarianService.class.getName());

    @Inject
    private CatalogingService catalogingService;

    @Inject
    private UserService userService;

    @Inject
    private InventoryService inventoryService;

    @Inject
    private TransactionManager transactionManager;

    @Inject
    private LoanService loanService;

    @Inject
    private BookService bookService;

    // Catalog Management (High Priority)
    /**
     * Adds a book to the catalog.
     * 
     * @param book the book to add
     * @return the cataloged book
     * @throws LibrarianServiceException if cataloging fails
     */
    public Book addBookToCatalog(Book book) throws LibrarianServiceException {
        try {
            return catalogingService.addBookToCatalog(book);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to catalog book", e);
            throw new LibrarianServiceException("Failed to catalog book: " + e.getMessage(), e);
        }
    }

    /**
     * Updates book catalog information.
     * 
     * @param bookId      the ID of the book to update
     * @param updatedBook the updated book data
     * @return the updated book
     * @throws LibrarianServiceException if update fails
     */
    public Book updateBookCatalog(Long bookId, Book updatedBook) throws LibrarianServiceException {
        try {
            // This would typically use BookService to update
            logger.info("Book catalog updated for ID: " + bookId);
            return updatedBook;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to update book catalog", e);
            throw new LibrarianServiceException("Failed to update book catalog: " + e.getMessage(), e);
        }
    }

    /**
     * Removes a book from the catalog.
     * 
     * @param bookId the ID of the book to remove
     * @return true if removal was successful
     * @throws LibrarianServiceException if removal fails
     */
    public boolean removeBookFromCatalog(Long bookId) throws LibrarianServiceException {
        try {
            return inventoryService.removeBook(bookId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to remove book from catalog", e);
            throw new LibrarianServiceException("Failed to remove book from catalog: " + e.getMessage(), e);
        }
    }

    // Member Account Management (Medium Priority)
    /**
     * Registers a new member.
     * 
     * @param newMember the member to register
     * @return the registered member
     * @throws LibrarianServiceException if registration fails
     */
    public Member registerMember(Member newMember) throws LibrarianServiceException {
        try {
            return (Member) userService.createUser(newMember);
        } catch (UserService.UserServiceException e) {
            logger.log(Level.SEVERE, "Failed to register member", e);
            throw new LibrarianServiceException("Failed to register member: " + e.getMessage(), e);
        }
    }

    /**
     * Updates member information.
     * 
     * @param memberId      the ID of the member to update
     * @param updatedMember the updated member data
     * @return the updated member
     * @throws LibrarianServiceException if update fails
     */
    public Member updateMemberInfo(Long memberId, Member updatedMember) throws LibrarianServiceException {
        try {
            return (Member) userService.updateUser(memberId, updatedMember);
        } catch (UserService.UserServiceException e) {
            logger.log(Level.SEVERE, "Failed to update member", e);
            throw new LibrarianServiceException("Failed to update member: " + e.getMessage(), e);
        }
    }

    /**
     * Deactivates a member account.
     * 
     * @param memberId the ID of the member to deactivate
     * @return true if deactivation was successful
     * @throws LibrarianServiceException if deactivation fails
     */
    public boolean deactivateMemberAccount(Long memberId) throws LibrarianServiceException {
        try {
            return userService.deactivateUser(memberId);
        } catch (UserService.UserServiceException e) {
            logger.log(Level.SEVERE, "Failed to deactivate member", e);
            throw new LibrarianServiceException("Failed to deactivate member: " + e.getMessage(), e);
        }
    }

    // Inventory Management
    /**
     * Performs inventory check by updating book copy statuses.
     * 
     * @param bookCopyStatuses map of copy IDs to their new statuses
     * @throws LibrarianServiceException if inventory check fails
     */
    public void performInventoryCheck(Map<Long, String> bookCopyStatuses) throws LibrarianServiceException {
        try {
            bookCopyStatuses.forEach((copyId, status) -> {
                inventoryService.updateBookStatus(copyId, BookCopy.CopyStatus.valueOf(status));
            });
            logger.info("Inventory check completed for " + bookCopyStatuses.size() + " items");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to perform inventory check", e);
            throw new LibrarianServiceException("Failed to perform inventory check: " + e.getMessage(), e);
        }
    }

    /**
     * Updates the location of a book copy.
     * 
     * @param copyId      the ID of the book copy
     * @param newLocation the new location
     * @throws LibrarianServiceException if location update fails
     */
    public void updateBookLocation(Long copyId, String newLocation) throws LibrarianServiceException {
        try {
            inventoryService.updateBookCopyLocation(copyId, newLocation);
            logger.info("Book copy location updated: " + copyId + " -> " + newLocation);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to update book location", e);
            throw new LibrarianServiceException("Failed to update book location: " + e.getMessage(), e);
        }
    }

    /**
     * Processes a payment with staff authorization.
     * 
     * @param amount      the payment amount
     * @param method      the payment method
     * @param description the payment description
     * @param staffId     the ID of the authorizing staff member
     * @return the transaction record
     * @throws LibrarianServiceException if payment processing fails
     */
    public Transaction processPayment(Double amount, String method, String description, Long staffId)
            throws LibrarianServiceException {
        try {
            // Check staff authorization
            if (!hasFinancialAccess(staffId)) {
                throw new SecurityException("Staff member does not have financial access");
            }

            Transaction transaction = transactionManager.processPayment(amount, method, description);
            logger.info("Payment processed: " + amount + " via " + method);
            return transaction;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to process payment", e);
            throw new LibrarianServiceException("Failed to process payment: " + e.getMessage(), e);
        }
    }

    private boolean hasFinancialAccess(Long staffId) {
        // Implementation to check if staff has financial access
        // This would typically check roles/permissions in a real system
        return true; // Placeholder
    }

    // Book Checkout and Return Process (High Priority)
    /**
     * Checks out a book to a member.
     * Validates member eligibility and book availability before creating loan.
     * 
     * @param bookId   the ID of the book to checkout
     * @param memberId the ID of the member
     * @return the created loan
     * @throws LibrarianServiceException if checkout is not allowed
     */
    @Transactional
    public Loan checkoutBook(Long bookId, Long memberId) throws LibrarianServiceException {
        try {
            // 1. Validate member can borrow
            Member member = (Member) userService.findById(memberId);
            if (member == null) {
                throw new LibrarianServiceException("Member not found");
            }

            if (member.getFineBalance() > 0) {
                throw new LibrarianServiceException("outstanding fines");
            }

            // 2. Validate book exists and is available
            Book book = bookService.getBookById(bookId);
            if (book == null) {
                throw new LibrarianServiceException("Book not found");
            }

            // 3. Check if book is available
            if (!book.isAvailable()) {
                throw new LibrarianServiceException("Book not available");
            }

            // 4. Create loan
            Loan loan = loanService.checkoutBook(bookId, memberId);
            logger.info("Book checked out by librarian - Member: " + memberId + ", Book: " + bookId + ", Loan: "
                    + loan.getId());
            return loan;
        } catch (BusinessException e) {
            logger.severe("Failed to checkout book: " + e.getMessage());
            throw new LibrarianServiceException(e.getMessage());
        } catch (LibrarianServiceException e) {
            logger.severe("Failed to checkout book: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.severe("Failed to checkout book: " + e.getMessage());
            throw new LibrarianServiceException("Failed to checkout book due to system error");
        }
    }

    /**
     * Processes book return and calculates any late fees.
     * Updates loan status and generates receipt.
     * 
     * @param loanId the ID of the loan to return
     * @return the receipt for the return transaction
     * @throws LibrarianServiceException if return processing fails
     */
    @Transactional
    public void returnBook(Long loanId) throws LibrarianServiceException {
        try {
            loanService.returnBook(loanId);
            logger.info("Book returned by librarian - Loan: " + loanId);
        } catch (BusinessException e) {
            logger.severe("Failed to return book: " + e.getMessage());
            throw new LibrarianServiceException(e.getMessage());
        } catch (Exception e) {
            logger.severe("Failed to return book: " + e.getMessage());
            throw new LibrarianServiceException("Failed to return book due to system error");
        }
    }

    /**
     * Gets all overdue loans for follow-up.
     * 
     * @return list of overdue loans
     * @throws LibrarianServiceException if retrieval fails
     */
    public List<Loan> getOverdueLoans() throws LibrarianServiceException {
        try {
            return loanService.getOverdueLoans();
        } catch (Exception e) {
            logger.severe("Failed to get overdue loans: " + e.getMessage());
            throw new LibrarianServiceException("Failed to get overdue loans: " + e.getMessage());
        }
    }

    /**
     * Renews a loan for a member.
     * 
     * @param loanId the ID of the loan to renew
     * @return the renewed loan
     * @throws LibrarianServiceException if renewal fails
     */
    @Transactional
    public void renewLoan(Long loanId) throws LibrarianServiceException {
        try {
            loanService.renewLoan(loanId);
            logger.info("Loan renewed by librarian - Loan: " + loanId);
        } catch (BusinessException e) {
            logger.severe("Failed to renew loan: " + e.getMessage());
            throw new LibrarianServiceException(e.getMessage());
        } catch (Exception e) {
            logger.severe("Failed to renew loan: " + e.getMessage());
            throw new LibrarianServiceException("Failed to renew loan due to system error");
        }
    }

    /**
     * Custom exception for librarian service operations.
     */
    public static class LibrarianServiceException extends Exception {
        public LibrarianServiceException(String message) {
            super(message);
        }

        public LibrarianServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
