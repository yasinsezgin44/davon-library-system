# Davon Library Management System

A comprehensive Java backend for managing library operations, including book management, inventory tracking, user management, and loan processing.

## Project Structure

```
davon-library-system/
├── backend/              # Java backend code
│   ├── src/main/java/    # Source code
│   │   └── com/davon/library/
│   │       ├── config/   # Application configuration
│   │       ├── controller/ # API endpoints
│   │       ├── dto/      # Data Transfer Objects
│   │       ├── exception/ # Custom exceptions
│   │       ├── model/    # Entity classes
│   │       ├── repository/ # Data access implementations
│   │       └── service/  # Business logic
│   └── src/test/java/    # Unit tests
│       └── com/davon/library/
│           ├── controller/ # Controller tests
│           ├── repository/ # Repository tests
│           └── service/  # Service tests
└── docs/                # Documentation
    ├── ClassDesign.md   # Class design documentation
    ├── CursorFeatures.md # Cursor IDE features
    └── README.md        # This file
```

## Getting Started

### Prerequisites

- Java JDK 17 or higher
- Gradle 7.0 or higher

### Build and Run

1. Clone the repository

   ```bash
   git clone https://github.com/username/davon-library-system.git
   cd davon-library-system
   ```

2. Build the project

   ```bash
   ./gradlew build
   ```

3. Run the application
   ```bash
   ./gradlew run
   ```

### Running Tests

To run the unit tests:

```bash
./gradlew test
```

## Features

- **Book Management**: Add, update, delete, and search for books
- **Inventory Tracking**: Manage book copies and their availability
- **User Management**: Handle different user types (members, librarians, admins)
- **Loan Processing**: Manage book borrowing, returns, and extensions
- **Data Validation**: Enforce business rules and validate data integrity

## Technology Stack

- **Java**: Core programming language
- **Gradle**: Build system and dependency management
- **JUnit 5**: Testing framework
- **Mockito**: Mocking framework for testing
- **Lombok**: Reduces boilerplate code
- **SLF4J**: Logging framework

## Design Patterns

- **Repository Pattern**: For data access abstraction
- **DTO Pattern**: For API request/response separation
- **Builder Pattern**: For object construction (via Lombok)
- **Dependency Injection**: For component composition
