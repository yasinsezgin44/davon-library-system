package com.davon.library.dao.impl;

import com.davon.library.dao.FineDAO;
import com.davon.library.model.Fine;
import com.davon.library.model.Member;
import com.davon.library.model.FineReason;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MSSQLFineDAOImpl implements FineDAO {
    @Override
    public Optional<Fine> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Fine save(Fine fine) {
        return fine;
    }

    @Override
    public List<Fine> findByMember(Member member) {
        return List.of();
    }

    @Override
    public List<Fine> findUnpaidFines(Member member) {
        return List.of();
    }

    @Override
    public double getTotalUnpaidAmount(Member member) {
        return 0.0;
    }

    @Override
    public Fine update(Fine fine) {
        return fine;
    }

    @Override
    public void deleteById(Long id) {
        // No-op for now
    }

    @Override
    public List<Fine> findOverdueFines(LocalDate date) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public List<Fine> findByReason(FineReason reason) {
        return List.of();
    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public List<Fine> findAll() {
        return List.of();
    }
}