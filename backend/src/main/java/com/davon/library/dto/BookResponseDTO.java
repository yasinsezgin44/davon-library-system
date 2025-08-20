package com.davon.library.dto;

import java.time.LocalDate;

public class BookResponseDTO {
    public Long id;
    public String title;
    public String authorName;
    public String isbn;
    public int publicationYear;
    public String description;
    public boolean isAvailable;
    public String publisher;
    public LocalDate publicationDate;
    public String genre;
    public String language;
    public String coverImageUrl;

    public Long id() {
        return id;
    }
}
