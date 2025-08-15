# Davon Library Management System

A comprehensive Java backend and Next.js frontend for managing library operations, including book management, inventory tracking, user management, and loan processing.

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
├── frontend/             # Next.js frontend code
│   ├── app/              # Application pages
│   ├── components/       # Reusable components
│   ├── lib/              # Utility functions
│   └── public/           # Static assets
└── docs/                 # Documentation
    ├── ClassDesign.md    # Class design documentation
    ├── CursorFeatures.md # Cursor IDE features
    └── README.md         # This file
```

## Getting Started

### Prerequisites

- Java JDK 21 or higher
- Maven 3.6 or higher
- Node.js 18 or higher

### Build and Run

1.  **Clone the repository**

    ```bash
    git clone https://github.com/username/davon-library-system.git
    cd davon-library-system
    ```

2.  **Run the backend**

    ```bash
    cd backend
    mvn quarkus:dev
    ```

3.  **Run the frontend**

    In a new terminal, navigate to the `frontend` directory:

    ```bash
    cd frontend
    npm install
    npm run dev
    ```

    The frontend will be available at `http://localhost:3000`.

### Running Tests

To run the backend unit tests:

```bash
cd backend
mvn test
```

## Features

- **Book Management**: Add, update, delete, and search for books
- **User Management**: User registration and login
- **Inventory Tracking**: Manage book copies and their availability
- **Loan Processing**: Manage book borrowing, returns, and extensions
- **Data Validation**: Enforce business rules and validate data integrity

## Technology Stack

### Backend

- **Java**: Core programming language
- **Quarkus**: Supersonic, subatomic Java framework
- **Maven**: Build system and dependency management
- **JUnit 5**: Testing framework
- **Mockito**: Mocking framework for testing
- **Lombok**: Reduces boilerplate code
- **SLF4J**: Logging framework

### Frontend

- **Next.js**: React framework for production
- **React**: A JavaScript library for building user interfaces
- **TypeScript**: Typed JavaScript at scale
- **Tailwind CSS**: A utility-first CSS framework

## Design Patterns

- **Repository Pattern**: For data access abstraction
- **DTO Pattern**: For API request/response separation
- **Builder Pattern**: For object construction (via Lombok)
- **Dependency Injection**: For component composition
