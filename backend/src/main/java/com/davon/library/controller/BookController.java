package com.davon.library.controller;

import com.davon.library.model.Book;
import com.davon.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * REST controller for book-related operations.
 */
@Path("/api/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Books", description = "Book management operations")
public class BookController {

    @Inject
    BookService bookService;

    @GET
    @Operation(summary = "Get all books", description = "Retrieve a list of all books in the library")
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get book by ID", description = "Retrieve a specific book by its ID")
    public Response getBookById(@PathParam("id") Long id) {
        try {
            Book book = bookService.getBookById(id);
            return Response.ok(book).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Operation(summary = "Create new book", description = "Add a new book to the library")
    public Response createBook(Book book) {
        try {
            Book createdBook = bookService.createBook(book);
            return Response.status(Response.Status.CREATED).entity(createdBook).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update book", description = "Update an existing book")
    public Response updateBook(@PathParam("id") Long id, Book book) {
        try {
            Book updatedBook = bookService.updateBook(id, book);
            return Response.ok(updatedBook).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete book", description = "Remove a book from the library")
    public Response deleteBook(@PathParam("id") Long id) {
        try {
            bookService.deleteBook(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/search")
    @Operation(summary = "Search books", description = "Search for books by title, author, or ISBN")
    public List<Book> searchBooks(@QueryParam("q") String query) {
        return bookService.searchBooks(query);
    }
}