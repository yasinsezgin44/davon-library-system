# Design Decisions Documentation - Davon Library System

## Architecture Overview

The Davon Library System follows a layered architecture with clear separation of concerns:

- **Entity Layer**: Core domain models (User, Book, Loan)
- **Controller Layer**: Handles HTTP requests and coordinates operations
- **Service Layer**: Contains business logic and operations
- **Context Layer**: Manages application state
- **Component Layer**: User interface elements

## Entity Design Decisions

### User Management

- **User Inheritance Model**: Implemented a base User entity with Admin and Member as specialized types to:

  - Reduce code duplication for common user attributes
  - Allow polymorphic operations on all user types
  - Support role-based access control naturally

- **Member-specific Attributes**: Added dedicated fields like borrowingLimit and fineBalance to track library-specific member data without bloating the base User entity

### Library Inventory

- **Book as Central Entity**: Designed Book with rich metadata including coverImage to:

  - Enhance user experience with visual identification
  - Support comprehensive search functionality
  - Enable proper cataloging with industry-standard fields

- **BookCopy Separation**: Implemented BookCopy as a distinct entity from Book to:
  - Track individual physical copies with different statuses and conditions
  - Allow multiple copies of the same book title
  - Support inventory management operations

## Authentication Approach

- **Token-based Authentication**: Chose JWT-based auth to:

  - Maintain stateless server architecture
  - Support secure API access
  - Enable cross-platform authentication

- **AuthContext Pattern**: Implemented a central auth context to:
  - Provide user state across the application
  - Encapsulate authentication logic
  - Simplify component access to user information

## Service Design

- **Specialized Services**: Created distinct services (AuthService, InventoryService) to:
  - Maintain separation of concerns
  - Allow independent scaling and maintenance
  - Support future extension without cross-service impacts

## Data Validation

- **Entity-level Validation**: Added methods like validateMetadata() and validateISBN() to:
  - Ensure data integrity at the domain level
  - Centralize validation logic
  - Prevent invalid data from entering the system

## Reservation and Loan System

- **Status Enumerations**: Implemented enum types for reservation and loan statuses to:
  - Enforce consistent state management
  - Prevent invalid state transitions
  - Simplify status-based operations and reporting
