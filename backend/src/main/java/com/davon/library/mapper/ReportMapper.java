package com.davon.library.mapper;

import com.davon.library.dto.ReportResponseDTO;
import com.davon.library.model.Report;

public class ReportMapper {

    public static ReportResponseDTO toResponseDTO(Report report) {
        if (report == null) {
            return null;
        }
        return new ReportResponseDTO(
                report.getId(),
                report.getTitle(),
                report.getStartDate(),
                report.getEndDate(),
                report.getContent(),
                report.getGeneratedBy(),
                report.getCreatedAt(),
                report.getUpdatedAt());
    }
}
