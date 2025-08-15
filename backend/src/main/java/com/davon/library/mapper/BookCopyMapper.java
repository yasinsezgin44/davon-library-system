package com.davon.library.mapper;

import com.davon.library.dto.BookCopyResponseDTO;
import com.davon.library.model.BookCopy;

public class BookCopyMapper {

    public static BookCopyResponseDTO toResponseDTO(BookCopy bookCopy) {
        if (bookCopy == null) {
            return null;
        }
        return new BookCopyResponseDTO(
                bookCopy.getId(),
                BookMapper.toShallowResponseDTO(bookCopy.getBook()),
                bookCopy.getAcquisitionDate(),
                bookCopy.getCondition(),
                bookCopy.getStatus(),
                bookCopy.getLocation(),
                bookCopy.getCreatedAt(),
                bookCopy.getUpdatedAt()
        );
    }
}
