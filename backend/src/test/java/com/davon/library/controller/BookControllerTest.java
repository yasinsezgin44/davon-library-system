package com.davon.library.controller;

import com.davon.library.model.Author;
import com.davon.library.model.Book;
import com.davon.library.model.Category;
import com.davon.library.model.Publisher;
import com.davon.library.repository.*;
import com.davon.library.service.BookService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusTest
class BookControllerTest {

    @Inject
    BookService bookService;

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

    @BeforeEach
    @Transactional
    void setup() {
        loanRepository.deleteAll();
        bookCopyRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        publisherRepository.deleteAll();
        categoryRepository.deleteAll();

        Category category = new Category();
        category.setName("Test Category");
        categoryRepository.persist(category);

        Author author = new Author();
        author.setName("Test Author");
        authorRepository.persist(author);

        Publisher publisher = new Publisher();
        publisher.setName("Test Publisher");
        publisherRepository.persist(publisher);
    }

        @Test
        void testGetAllBooks() {
                given()
                                .when().get("/api/books")
                                .then()
                                .statusCode(200)
                                .contentType(ContentType.JSON)
                                .body("size()", greaterThanOrEqualTo(0));
        }

        @Test
        void testCreateAndGetBook() {
                String uniqueISBN = generateUniqueISBN();
                
                        String bookJson = """
				{
					"title": "Test Book",
					"isbn": "%s",
					"publicationYear": 2023,
					"description": "A test book",
					"pages": 200
				}
				""".formatted(uniqueISBN);

                given()
                                .contentType(ContentType.JSON)
                                .body(bookJson)
                                .when().post("/api/books")
                                .then()
                                .statusCode(201)
                                .contentType(ContentType.JSON)
                                .body("title", is("Test Book"))
                                .body("isbn", is(uniqueISBN));
        }

        @Test
        void testSearchBooks() {
                String uniquePrefix = "ControllerTest" + System.nanoTime();
                String bookJson = """
                                {
                                    "title": "%s Java Programming",
                                    "isbn": "%s",
                                    "description": "A comprehensive guide to Java",
                                    "publicationYear": 2023,
                                    "pages": 400
                                }
                                """.formatted(uniquePrefix, generateUniqueISBN());

                given()
                                .contentType(ContentType.JSON)
                                .body(bookJson)
                                .when().post("/api/books")
                                .then()
                                .statusCode(201);

                given()
                                .param("q", uniquePrefix)
                                .when().get("/api/books/search")
                                .then()
                                .statusCode(200)
                                .contentType(ContentType.JSON)
                                .body("size()", greaterThanOrEqualTo(1));
        }

        @Test
        void testGetBookByIdNotFound() {
                given()
                                .when().get("/api/books/999")
                                .then()
                                .statusCode(404);
        }

        @Test
        void testDeleteBookNotFound() {
                given()
                                .when().delete("/api/books/999")
                                .then()
                                .statusCode(404);
        }

        @Test
        void testHealthEndpoint() {
                given()
                                .when().get("/api/health")
                                .then()
                                .statusCode(200)
                                .contentType(ContentType.JSON)
                                .body("status", is("UP"));
        }

        @Test
        void testUpdateBook() {
                String uniqueISBN = generateUniqueISBN();
                String bookJson = """
                                {
                                    "title": "Original Title",
                                    "isbn": "%s",
                                    "publicationYear": 2023,
                                    "description": "Original description",
                                    "pages": 200
                                }
                                """.formatted(uniqueISBN);

                String bookId = given()
                                .contentType(ContentType.JSON)
                                .body(bookJson)
                                .when().post("/api/books")
                                .then()
                                .statusCode(201)
                                .extract()
                                .jsonPath()
                                .getString("id");

                Category category = categoryRepository.findAll().firstResult();
                Author author = authorRepository.findAll().firstResult();
                Publisher publisher = publisherRepository.findAll().firstResult();
                String updatedBookJson = """
						{
							"title": "Updated Title",
							"isbn": "%s",
							"publicationYear": 2024,
							"description": "Updated description",
							"pages": 250,
							"authors": [
								{
									"id": %d
								}
							],
							"publisher": {
								"id": %d
							},
							"category": {
								"id": %d
							}
						}
						""".formatted(uniqueISBN, author.getId(), publisher.getId(), category.getId());

                given()
                                .contentType(ContentType.JSON)
                                .body(updatedBookJson)
                                .when().put("/api/books/" + bookId)
                                .then()
                                .statusCode(200)
                                .body("title", is("Updated Title"))
                                .body("publicationYear", is(2024))
                                .body("pages", is(250));
        }

        @Test
        void testCreateBookWithInvalidData() {
                String invalidBookJson = """
                                {
                                    "title": "",
                                    "isbn": "invalid-isbn",
                                    "publicationYear": 1800
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
        void testGetBookById() {
                String uniqueISBN = generateUniqueISBN();
                String bookJson = """
                                {
                                    "title": "Test Book for Get",
                                    "isbn": "%s",
                                    "publicationYear": 2023,
                                    "description": "A test book for get operation",
                                    "pages": 300
                                }
                                """.formatted(uniqueISBN);

                String bookId = given()
                                .contentType(ContentType.JSON)
                                .body(bookJson)
                                .when().post("/api/books")
                                .then()
                                .statusCode(201)
                                .extract()
                                .jsonPath()
                                .getString("id");

                given()
                                .when().get("/api/books/" + bookId)
                                .then()
                                .statusCode(200)
                                .body("title", is("Test Book for Get"))
                                .body("isbn", is(uniqueISBN))
                                .body("pages", is(300));
        }

        @Test
        void testDeleteBook() {
                String uniqueISBN = generateUniqueISBN();
                String bookJson = """
                                {
                                    "title": "Book to Delete",
                                    "isbn": "%s",
                                    "publicationYear": 2023,
                                    "description": "A book that will be deleted",
                                    "pages": 150
                                }
                                """.formatted(uniqueISBN);

                String bookId = given()
                                .contentType(ContentType.JSON)
                                .body(bookJson)
                                .when().post("/api/books")
                                .then()
                                .statusCode(201)
                                .extract()
                                .jsonPath()
                                .getString("id");

                given()
                                .when().delete("/api/books/" + bookId)
                                .then()
                                .statusCode(204);

                given()
                                .when().get("/api/books/" + bookId)
                                .then()
                                .statusCode(404);
        }

        private String generateUniqueISBN() {
                long timestamp = System.nanoTime();
                return "978" + String.format("%010d", Math.abs(timestamp % 10000000000L));
        }
}