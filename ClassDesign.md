# Library Management System - Class Design Documentation

## Architecture Overview

The system follows a layered architecture pattern with clear separation of concerns:

- **Controller Layer**: Handles incoming requests and routes them to appropriate services
- **Service Layer**: Contains business logic and application workflows
- **Repository Layer**: Manages data access and persistence
- **Model Layer**: Defines the data entities and their relationships
- **DTO Layer**: Data Transfer Objects for API request/response objects
- **Exception Layer**: Custom exceptions for error handling

## Core Models

### Book

- Represents a book in the library system
- Properties: title, ISBN, publicationYear, description, etc.
- Relationships: authors, publisher, category
- Methods: validateISBN(), isAvailable(), validateMetadata(), etc.

### BookCopy

- Represents a physical copy of a book
- Properties: condition, location, acquisitionDate, etc.
- Relationships: book, loans

### User (Base Class)

- Abstract base class for system users
- Properties: username, passwordHash, email, active status
- Subclasses: Member, Librarian, Admin

### Member

- Represents a library member who can borrow books
- Properties: membershipType, memberSince, etc.
- Relationships: loans, reservations

### Loan

- Represents a book borrowing transaction
- Properties: loanDate, dueDate, returnDate, etc.
- Relationships: member, bookCopy

## Service Layer Components

### BookService

- Manages book-related operations
- Methods: createBook(), updateBook(), deleteBook(), searchBooks()

### InventoryService

- Manages book copies and availability
- Methods: addBookCopy(), removeBookCopy(), checkAvailability()

### LoanService

- Manages borrowing transactions
- Methods: createLoan(), returnBook(), extendLoan()

### UserService

- Manages user accounts
- Methods: createUser(), updateUser(), authenticate()

## Repository Layer Components

### BookRepository

- Interface for book data access
- Implementation: InMemoryBookRepository

### LoanRepository

- Interface for loan data access

### UserRepository

- Interface for user data access

## Package Structure

```
com.davon.library
├── config           // Application configuration
├── controller       // REST API endpoints
├── dto              // Data Transfer Objects
├── exception        // Custom exceptions
├── model            // Entity classes
├── repository       // Data access implementations
└── service          // Business logic
```

## Design Patterns

1. **Repository Pattern**: Abstracts data access operations
2. **DTO Pattern**: Separates API models from domain models
3. **Builder Pattern**: Used for complex object construction (via Lombok)
4. **Dependency Injection**: Components receive their dependencies through constructor injection
