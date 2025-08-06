package com.davon.library.controller;

import com.davon.library.model.Book;
import com.davon.library.model.Category;
import com.davon.library.service.BookService;
import com.davon.library.service.CategoryService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

@Path("/api/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Books", description = "Book management operations")
public class BookController {

    @Inject
    BookService bookService;

    @Inject
    CategoryService categoryService;

    @GET
    @Operation(summary = "Get all books")
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a book by its ID")
    public Response getBookById(@PathParam("id") Long id) {
        return bookService.getBookById(id)
                .map(book -> Response.ok(book).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Operation(summary = "Create a new book")
    public Response createBook(Book book) {
        Book createdBook = bookService.createBook(book);
        return Response.status(Response.Status.CREATED).entity(createdBook).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a book's details")
    public Response updateBook(@PathParam("id") Long id, Book book) {
        Book updatedBook = bookService.updateBook(id, book);
        return Response.ok(updatedBook).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a book")
    public Response deleteBook(@PathParam("id") Long id) {
        bookService.deleteBook(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/search")
    @Operation(summary = "Search for books")
    public List<Book> searchBooks(@QueryParam("query") String query) {
        return bookService.searchBooks(query);
    }

    @GET
    @Path("/trending")
    @Operation(summary = "Get trending books")
    public List<Book> getTrendingBooks() {
        // Placeholder for trending logic
        return bookService.getAllBooks().stream().limit(5).collect(Collectors.toList());
    }

    @GET
    @Path("/genres")
    @Operation(summary = "Get all book genres")
    public List<Category> getGenres() {
        return categoryService.getAllCategories();
    }
}
