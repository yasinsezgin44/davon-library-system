package com.davon.library.mapper;

import com.davon.library.dto.LibrarianResponseDTO;
import com.davon.library.model.Librarian;

public class LibrarianMapper {

    public static LibrarianResponseDTO toResponseDTO(Librarian librarian) {
        if (librarian == null) {
            return null;
        }
        return new LibrarianResponseDTO(
                librarian.getId(),
                UserMapper.toResponseDTO(librarian.getUser()),
                librarian.getEmploymentDate(),
                librarian.getEmployeeId());
    }
}
