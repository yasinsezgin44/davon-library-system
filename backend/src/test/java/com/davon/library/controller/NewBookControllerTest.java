package com.davon.library.controller;

import com.davon.library.model.Author;
import com.davon.library.model.Book;
import com.davon.library.model.Category;
import com.davon.library.model.Publisher;
import com.davon.library.repository.*;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class NewBookControllerTest {

    @Inject
    BookRepository bookRepository;
    @Inject
    AuthorRepository authorRepository;
    @Inject
    PublisherRepository publisherRepository;
    @Inject
    CategoryRepository categoryRepository;
    @Inject
    BookCopyRepository bookCopyRepository;
    @Inject
    LoanRepository loanRepository;
    @Inject
    ReservationRepository reservationRepository;
    @Inject
    FineRepository fineRepository;

    private static final String VALID_ISBN = "978-0-321-35668-0";
    private static final String ANOTHER_VALID_ISBN = "978-0-13-235088-4";

    @BeforeEach
    @Transactional
    void setup() {
        fineRepository.deleteAll();
        loanRepository.deleteAll();
        reservationRepository.deleteAll();
        bookCopyRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        publisherRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void testGetAllBooks_Empty() {
        given()
                .when().get("/api/books")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(0));
    }

    @Test
    @Transactional
    void testGetAllBooks_WithData() {
        bookRepository.persist(Book.builder().title("Effective Java").isbn(VALID_ISBN).build());
        given()
                .when().get("/api/books")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(1));
    }

    @Test
    void testCreateBook_Success() {
        String bookJson = String.format("""
            {
                "title": "Clean Code",
                "isbn": "%s",
                "publicationYear": 2008,
                "pages": 464
            }
            """, ANOTHER_VALID_ISBN);

        given()
                .contentType(ContentType.JSON)
                .body(bookJson)
                .when().post("/api/books")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("title", is("Clean Code"))
                .body("isbn", is(ANOTHER_VALID_ISBN))
                .body("id", notNullValue());
    }

    @Test
    void testCreateBook_InvalidData_ShouldReturnBadRequest() {
        String invalidBookJson = """
            {
                "title": "",
                "isbn": "invalid-isbn",
                "publicationYear": 999
            }
            """;
        given()
                .contentType(ContentType.JSON)
                .body(invalidBookJson)
                .when().post("/api/books")
                .then()
                .statusCode(400);
    }

    @Test
    void testGetBookById_Found() {
        Book book = Book.builder().title("Test Driven Development").isbn(VALID_ISBN).build();
        bookRepository.persist(book);

        given()
                .when().get("/api/books/" + book.getId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", is(book.getId().intValue()))
                .body("title", is("Test Driven Development"));
    }

    @Test
    void testGetBookById_NotFound() {
        given()
                .when().get("/api/books/99999")
                .then()
                .statusCode(404);
    }

    @Test
    void testUpdateBook_Success() {
        Book book = Book.builder().title("Refactoring").isbn(ANOTHER_VALID_ISBN).build();
        bookRepository.persist(book);

        Category category = Category.builder().name("Software Engineering").build();
        categoryRepository.persist(category);
        Author author = Author.builder().name("Martin Fowler").build();
        authorRepository.persist(author);
        Publisher publisher = Publisher.builder().name("Addison-Wesley").build();
        publisherRepository.persist(publisher);

        String updatedBookJson = String.format("""
            {
                "title": "Refactoring: Improving the Design of Existing Code",
                "publicationYear": 2018,
                "authors": [{"id": %d}],
                "publisher": {"id": %d},
                "category": {"id": %d}
            }
            """, author.getId(), publisher.getId(), category.getId());

        given()
                .contentType(ContentType.JSON)
                .body(updatedBookJson)
                .when().put("/api/books/" + book.getId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("title", is("Refactoring: Improving the Design of Existing Code"))
                .body("publicationYear", is(2018));
    }

    @Test
    void testUpdateBook_NotFound() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"title\": \"This will fail\"}")
                .when().put("/api/books/99999")
                .then()
                .statusCode(404);
    }

    @Test
    void testDeleteBook_Success() {
        Book book = Book.builder().title("The Pragmatic Programmer").isbn(VALID_ISBN).build();
        bookRepository.persist(book);

        given()
                .when().delete("/api/books/" + book.getId())
                .then()
                .statusCode(204);

        given()
                .when().get("/api/books/" + book.getId())
                .then()
                .statusCode(404);
    }

    @Test
    void testDeleteBook_NotFound() {
        given()
                .when().delete("/api/books/99999")
                .then()
                .statusCode(404);
    }

    @Test
    @Transactional
    void testSearchBooks_ByTitle() {
        bookRepository.persist(Book.builder().title("Testable Java").isbn(VALID_ISBN).build());
        bookRepository.persist(Book.builder().title("Untestable Code").isbn(ANOTHER_VALID_ISBN).build());

        given()
                .param("q", "Testable")
                .when().get("/api/books/search")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(1))
                .body("[0].title", is("Testable Java"));
    }
}
