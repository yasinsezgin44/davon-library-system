package com.davon.library.service;

import com.davon.library.model.*;
import java.time.LocalDate;
import java.util.List;

public class OverdueProcessingService {
    private final LoanRepository loanRepository;
    private final NotificationService notificationService;
    private final FineService fineService;

    public OverdueProcessingService(LoanRepository loanRepository,
            NotificationService notificationService,
            FineService fineService) {
        this.loanRepository = loanRepository;
        this.notificationService = notificationService;
        this.fineService = fineService;
    }

    public void processOverdueItems() {
        List<Loan> overdueLoans = loanRepository.findOverdueLoans(LocalDate.now());

        for (Loan loan : overdueLoans) {
            if (loan.getStatus() != Loan.LoanStatus.OVERDUE) {
                loan.setStatus(Loan.LoanStatus.OVERDUE);
                loanRepository.save(loan);

                // Calculate fine
                Fine fine = fineService.calculateOverdueFine(loan);

                // Send notification
                notificationService.sendOverdueNotice(loan);
            }
        }
    }
}
