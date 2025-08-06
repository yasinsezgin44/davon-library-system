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
    private TransactionService transactionService;

    @Inject
    private LoanService loanService;

    @Inject
    private BookService bookService;

    public Book addBookToCatalog(Book book) throws LibrarianServiceException {
        try {
            return catalogingService.addBookToCatalog(book);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to catalog book", e);
            throw new LibrarianServiceException("Failed to catalog book: " + e.getMessage(), e);
        }
    }

    public Book updateBookCatalog(Long bookId, Book updatedBook) throws LibrarianServiceException {
        try {
            logger.info("Book catalog updated for ID: " + bookId);
            return updatedBook;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to update book catalog", e);
            throw new LibrarianServiceException("Failed to update book catalog: " + e.getMessage(), e);
        }
    }

    public boolean removeBookFromCatalog(Long bookId) throws LibrarianServiceException {
        try {
            return inventoryService.removeBook(bookId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to remove book from catalog", e);
            throw new LibrarianServiceException("Failed to remove book from catalog: " + e.getMessage(), e);
        }
    }

    public Member registerMember(Member newMember) throws LibrarianServiceException {
        try {
            return (Member) userService.createUser(newMember);
        } catch (UserService.UserServiceException e) {
            logger.log(Level.SEVERE, "Failed to register member", e);
            throw new LibrarianServiceException("Failed to register member: " + e.getMessage(), e);
        }
    }

    public Member updateMemberInfo(Long memberId, Member updatedMember) throws LibrarianServiceException {
        try {
            return (Member) userService.updateUser(memberId, updatedMember);
        } catch (UserService.UserServiceException e) {
            logger.log(Level.SEVERE, "Failed to update member", e);
            throw new LibrarianServiceException("Failed to update member: " + e.getMessage(), e);
        }
    }

    public boolean deactivateMemberAccount(Long memberId) throws LibrarianServiceException {
        try {
            return userService.deactivateUser(memberId);
        } catch (UserService.UserServiceException e) {
            logger.log(Level.SEVERE, "Failed to deactivate member", e);
            throw new LibrarianServiceException("Failed to deactivate member: " + e.getMessage(), e);
        }
    }

    public void performInventoryCheck(Map<Long, String> bookCopyStatuses) throws LibrarianServiceException {
        try {
            bookCopyStatuses.forEach((copyId, status) -> {
                inventoryService.updateBookStatus(copyId, status);
            });
            logger.info("Inventory check completed for " + bookCopyStatuses.size() + " items");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to perform inventory check", e);
            throw new LibrarianServiceException("Failed to perform inventory check: " + e.getMessage(), e);
        }
    }

    public void updateBookLocation(Long copyId, String newLocation) throws LibrarianServiceException {
        try {
            inventoryService.updateBookCopyLocation(copyId, newLocation);
            logger.info("Book copy location updated: " + copyId + " -> " + newLocation);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to update book location", e);
            throw new LibrarianServiceException("Failed to update book location: " + e.getMessage(), e);
        }
    }

    public Transaction processPayment(Double amount, String method, String description, Long staffId)
            throws LibrarianServiceException {
        try {
            if (!hasFinancialAccess(staffId)) {
                throw new SecurityException("Staff member does not have financial access");
            }

            Transaction transaction = new Transaction();
            transaction.setAmount(BigDecimal.valueOf(amount));
            transaction.setPaymentMethod(method);
            transaction.setDescription(description);
            transaction.setDate(LocalDate.now());
            transaction.setType("FINE_PAYMENT");

            transactionService.createTransaction(transaction);
            logger.info("Payment processed: " + amount + " via " + method);
            return transaction;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to process payment", e);
            throw new LibrarianServiceException("Failed to process payment: " + e.getMessage(), e);
        }
    }

    private boolean hasFinancialAccess(Long staffId) {
        return true;
    }

    @Transactional
    public Loan checkoutBook(Long bookId, Long memberId) throws LibrarianServiceException {
        try {
            Member member = (Member) userService.findById(memberId);
            if (member == null) {
                throw new LibrarianServiceException("Member not found");
            }

            if (member.getFineBalance().doubleValue() > 0) {
                throw new LibrarianServiceException("outstanding fines");
            }

            Book book = bookService.getBookById(bookId);
            if (book == null) {
                throw new LibrarianServiceException("Book not found");
            }

            if (!bookService.isBookAvailable(bookId)) {
                throw new LibrarianServiceException("Book not available");
            }

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

    public List<Loan> getOverdueLoans() throws LibrarianServiceException {
        try {
            return loanService.getOverdueLoans();
        } catch (Exception e) {
            logger.severe("Failed to get overdue loans: " + e.getMessage());
            throw new LibrarianServiceException("Failed to get overdue loans: " + e.getMessage());
        }
    }

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

    public static class LibrarianServiceException extends Exception {
        public LibrarianServiceException(String message) {
            super(message);
        }

        public LibrarianServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}