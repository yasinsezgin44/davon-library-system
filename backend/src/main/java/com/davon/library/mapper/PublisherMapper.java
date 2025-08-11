package com.davon.library.mapper;

import com.davon.library.dto.PublisherResponseDTO;
import com.davon.library.model.Publisher;

public class PublisherMapper {

    public static PublisherResponseDTO toResponseDTO(Publisher publisher) {
        if (publisher == null) {
            return null;
        }
        return new PublisherResponseDTO(
                publisher.getId(),
                publisher.getName(),
                publisher.getAddress(),
                publisher.getContact(),
                publisher.getCreatedAt(),
                publisher.getUpdatedAt()
        );
    }
}
