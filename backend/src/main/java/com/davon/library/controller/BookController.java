package com.davon.library.controller;

import com.davon.library.dto.BookRequestDTO;
import com.davon.library.dto.BookResponseDTO;
import com.davon.library.dto.CategoryResponseDTO;
import com.davon.library.mapper.BookMapper;
import com.davon.library.mapper.CategoryMapper;
import com.davon.library.model.Book;
import com.davon.library.service.BookService;
import com.davon.library.service.CategoryService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import com.davon.library.dto.CategoryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/api/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Books", description = "Book management operations")
public class BookController {

    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    @Inject
    BookService bookService;

    @Inject
    CategoryService categoryService;

    @GET
    @Operation(summary = "Get all books")
    @PermitAll
    public List<BookResponseDTO> getAllBooks() {
        try {
            return bookService.getAllBooks().stream()
                    .map(BookMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching all books", e);
            throw e;
        }
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a book by its ID")
    @PermitAll
    public Response getBookById(@PathParam("id") Long id) {
        return bookService.getBookById(id)
                .map(book -> Response.ok(BookMapper.toResponseDTO(book)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @RolesAllowed({ "ADMIN", "LIBRARIAN" })
    @Operation(summary = "Create a new book")
    @SecurityRequirement(name = "jwt")
    public Response createBook(@Valid BookRequestDTO book) {
        Book createdBook = bookService.createBook(book);
        return Response.status(Response.Status.CREATED).entity(BookMapper.toResponseDTO(createdBook)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "ADMIN", "LIBRARIAN" })
    @Operation(summary = "Update a book's details")
    @SecurityRequirement(name = "jwt")
    public Response updateBook(@PathParam("id") Long id, @Valid BookRequestDTO book) {
        Book updatedBook = bookService.updateBook(id, BookMapper.toEntity(book));
        return Response.ok(BookMapper.toResponseDTO(updatedBook)).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "ADMIN", "LIBRARIAN" })
    @Operation(summary = "Delete a book")
    @SecurityRequirement(name = "jwt")
    public Response deleteBook(@PathParam("id") Long id) {
        bookService.deleteBook(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/search")
    @Operation(summary = "Search for books")
    @PermitAll
    public List<BookResponseDTO> searchBooks(@QueryParam("query") String query) {
        return bookService.searchBooks(query).stream()
                .map(BookMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/trending")
    @Operation(summary = "Get trending books")
    @PermitAll
    @Transactional
    public List<BookResponseDTO> getTrendingBooks() {
        // Placeholder for trending logic
        return bookService.getAllBooks().stream()
                .limit(5)
                .map(BookMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/genres")
    @Operation(summary = "Get all book genres")
    @PermitAll
    public List<CategoryDTO> getGenres() {
        return categoryService.getAllCategories().stream()
                .map(CategoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/genre/{genreId}")
    @Operation(summary = "Get books by genre")
    @PermitAll
    public List<BookResponseDTO> getBooksByGenre(@PathParam("genreId") Long genreId) {
        return bookService.getBooksByCategory(genreId).stream()
                .map(BookMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
