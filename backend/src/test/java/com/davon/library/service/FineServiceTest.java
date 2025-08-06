package com.davon.library.service;

import com.davon.library.model.Fine;
import com.davon.library.model.Member;
import com.davon.library.model.enums.FineStatus;
import com.davon.library.repository.FineRepository;
import com.davon.library.repository.MemberRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class FineServiceTest {

    @Inject
    FineService fineService;

    @InjectMock
    FineRepository fineRepository;

    @InjectMock
    MemberRepository memberRepository;

    private Fine fine;
    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(1L);
        member.setFineBalance(new BigDecimal("10.00"));

        fine = new Fine();
        fine.setId(1L);
        fine.setMember(member);
        fine.setAmount(new BigDecimal("10.00"));
        fine.setStatus(FineStatus.PENDING);
    }

    @Test
    void testCreateFine() {
        fineService.createFine(fine);
        Mockito.verify(fineRepository).persist(any(Fine.class));
    }

    @Test
    void testPayFine() {
        when(fineRepository.findByIdOptional(1L)).thenReturn(Optional.of(fine));
        Fine paidFine = fineService.payFine(1L);
        assertEquals(FineStatus.PAID, paidFine.getStatus());
        assertEquals(BigDecimal.ZERO, paidFine.getMember().getFineBalance());
    }

    @Test
    void testPayFine_notFound() {
        when(fineRepository.findByIdOptional(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> fineService.payFine(1L));
    }

    @Test
    void testGetFinesForMember() {
        when(memberRepository.findByIdOptional(1L)).thenReturn(Optional.of(member));
        fineService.getFinesForMember(1L);
        Mockito.verify(fineRepository).findByMember(member);
    }
}
