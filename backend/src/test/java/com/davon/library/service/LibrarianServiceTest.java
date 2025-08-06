package com.davon.library.service;

import com.davon.library.model.*;
import com.davon.library.exception.BusinessException;
import com.davon.library.service.LibrarianService.LibrarianServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LibrarianService Checkout/Return Tests")
class LibrarianServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private BookService bookService;
    @Mock
    private LoanService loanService;
    @Mock
    private TransactionService transactionService;
    @InjectMocks
    private LibrarianService librarianService;

    private User user;
    private Member member;
    private Book testBook;
    private Loan testLoan;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("johndoe");
        user.setPasswordHash("hashedpassword123");
        user.setEmail("test@library.com");
        user.setFullName("John Doe");
        user.setActive(true);

        member = new Member();
        member.setUser(user);
        member.setFineBalance(BigDecimal.ZERO);
        user.setMember(member); 

        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setIsbn("1234567890");

        testLoan = new Loan();
        testLoan.setId(1L);
        testLoan.setMember(member);
        testLoan.setCheckoutDate(LocalDate.now());
        testLoan.setDueDate(LocalDate.now().plusDays(14));
        testLoan.setStatus("ACTIVE");
    }

    @Test
    @DisplayName("Should successfully checkout book when member and book are valid")
    void testCheckoutBookSuccess() throws Exception {
        when(userService.findById(1L)).thenReturn(user);
        when(bookService.getBookById(1L)).thenReturn(testBook);
        when(bookService.isBookAvailable(1L)).thenReturn(true);
        when(loanService.checkoutBook(1L, 1L)).thenReturn(testLoan);

        Loan result = librarianService.checkoutBook(1L, 1L);

        assertNotNull(result);
        assertEquals(testLoan.getId(), result.getId());
        verify(userService).findById(1L);
        verify(bookService).getBookById(1L);
        verify(bookService).isBookAvailable(1L);
        verify(loanService).checkoutBook(1L, 1L);
    }

    @Test
    @DisplayName("Should successfully return book")
    void testReturnBookSuccess() throws Exception {
        doNothing().when(loanService).returnBook(1L);
        librarianService.returnBook(1L);
        verify(loanService).returnBook(1L);
    }

    @Test
    @DisplayName("Should successfully get overdue loans")
    void testGetOverdueLoansSuccess() throws Exception {
        List<Loan> overdueLoans = Arrays.asList(testLoan);
        when(loanService.getOverdueLoans()).thenReturn(overdueLoans);
        List<Loan> result = librarianService.getOverdueLoans();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testLoan.getId(), result.get(0).getId());
        verify(loanService).getOverdueLoans();
    }

    @Test
    @DisplayName("Should successfully renew loan")
    void testRenewLoanSuccess() throws Exception {
        when(loanService.renewLoan(1L)).thenReturn(testLoan);
        librarianService.renewLoan(1L);
        verify(loanService).renewLoan(1L);
    }
}
