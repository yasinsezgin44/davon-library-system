package com.davon.library.mapper;

import com.davon.library.dto.BookRequestDTO;
import com.davon.library.dto.BookResponseDTO;
import com.davon.library.dto.BookShallowResponseDTO;
import com.davon.library.model.Author;
import com.davon.library.model.Book;
import com.davon.library.model.Category;
import com.davon.library.model.Publisher;

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookMapper {

    private static final Logger log = LoggerFactory.getLogger(BookMapper.class);

    public static Book toEntity(BookRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Book book = new Book();
        book.setTitle(dto.title());
        book.setIsbn(dto.isbn());
        book.setPublicationYear(dto.publicationYear());
        book.setDescription(dto.description());
        book.setCoverImage(dto.coverImage());
        book.setPages(dto.pages());

        if (dto.publisherId() != null) {
            Publisher publisher = new Publisher();
            publisher.setId(dto.publisherId());
            book.setPublisher(publisher);
        }

        if (dto.categoryId() != null) {
            Category category = new Category();
            category.setId(dto.categoryId());
            book.setCategory(category);
        }

        if (dto.authorIds() != null) {
            book.setAuthors(dto.authorIds().stream().map(authorId -> {
                Author author = new Author();
                author.setId(authorId);
                return author;
            }).collect(Collectors.toSet()));
        }

        return book;
    }

    public static BookResponseDTO toResponseDTO(Book book) {
        if (book == null) {
            return null;
        }

        log.info("Mapping book with ID: {}", book.getId());

        BookResponseDTO dto = new BookResponseDTO(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getDescription(),
                book.getPublicationYear(),
                book.getPages(),
                book.getCoverImageUrl(),
                PublisherMapper.toDTO(book.getPublisher()),
                CategoryMapper.toDTO(book.getCategory()),
                book.getAuthors().stream()
                        .map(AuthorMapper::toDTO)
                        .collect(Collectors.toList()),
                book.getCopies().stream()
                        .map(BookCopyMapper::toResponseDTO)
                        .collect(Collectors.toList()),
                book.getCreatedAt(),
                book.getUpdatedAt());

        log.info("Successfully mapped book with ID: {}", book.getId());
        return dto;
    }

    public static BookShallowResponseDTO toShallowResponseDTO(Book book) {
        if (book == null) {
            return null;
        }

        return new BookShallowResponseDTO(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getDescription(),
                book.getPublicationYear(),
                book.getPages(),
                book.getCoverImageUrl(),
                PublisherMapper.toDTO(book.getPublisher()),
                CategoryMapper.toDTO(book.getCategory()),
                book.getAuthors().stream()
                        .map(AuthorMapper::toDTO)
                        .collect(Collectors.toList()),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }
}
