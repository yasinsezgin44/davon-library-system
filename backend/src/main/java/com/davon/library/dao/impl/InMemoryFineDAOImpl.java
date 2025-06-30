package com.davon.library.dao.impl;

import com.davon.library.dao.FineDAO;
import com.davon.library.dao.DAOException;
import com.davon.library.model.Fine;
import com.davon.library.model.Member;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * In-memory implementation of FineDAO.
 * Thread-safe implementation using ConcurrentHashMap.
 */
@ApplicationScoped
public class InMemoryFineDAOImpl extends AbstractInMemoryDAO<Fine> implements FineDAO {

    @Override
    protected String getEntityName() {
        return "Fine";
    }

    @Override
    protected Fine cloneEntity(Fine entity) {
        if (entity == null) {
            return null;
        }

        return Fine.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .member(entity.getMember()) // Shallow copy for now
                .amount(entity.getAmount())
                .reason(entity.getReason())
                .issueDate(entity.getIssueDate())
                .dueDate(entity.getDueDate())
                .status(entity.getStatus())
                .build();
    }

    @Override
    protected void validateEntity(Fine entity) throws DAOException {
        super.validateEntity(entity);

        if (entity.getAmount() < 0) {
            throw new DAOException("Fine amount cannot be negative", "validate", getEntityName());
        }

        if (entity.getReason() == null) {
            throw new DAOException("Fine must have a reason", "validate", getEntityName());
        }

        if (entity.getIssueDate() == null) {
            throw new DAOException("Fine must have an issue date", "validate", getEntityName());
        }

        if (entity.getStatus() == null) {
            throw new DAOException("Fine must have a status", "validate", getEntityName());
        }
    }

    @Override
    public List<Fine> findByMember(Member member) {
        if (member == null || member.getId() == null) {
            return List.of();
        }

        return storage.values().stream()
                .filter(fine -> fine.getMember() != null &&
                        member.getId().equals(fine.getMember().getId()))
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Fine> findUnpaidFinesByMember(Member member) {
        return findByMember(member).stream()
                .filter(fine -> fine.getStatus() == Fine.FineStatus.PENDING)
                .collect(Collectors.toList());
    }

    @Override
    public List<Fine> findByStatus(Fine.FineStatus status) {
        if (status == null) {
            return List.of();
        }

        return storage.values().stream()
                .filter(fine -> status.equals(fine.getStatus()))
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Fine> findByReason(Fine.FineReason reason) {
        if (reason == null) {
            return List.of();
        }

        return storage.values().stream()
                .filter(fine -> reason.equals(fine.getReason()))
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Fine> findOverdueFines(LocalDate currentDate) {
        if (currentDate == null) {
            currentDate = LocalDate.now();
        }

        final LocalDate today = currentDate;
        return storage.values().stream()
                .filter(fine -> fine.getStatus() == Fine.FineStatus.PENDING &&
                        fine.getDueDate() != null &&
                        fine.getDueDate().isBefore(today))
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public double getTotalUnpaidAmount(Member member) {
        return findUnpaidFinesByMember(member).stream()
                .mapToDouble(Fine::getAmount)
                .sum();
    }
}