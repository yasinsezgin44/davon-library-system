## MSSQL-Specific Features Implemented

This document summarizes Microsoft SQL Server–specific capabilities used in the Davon Library Management System.

### Identity and Data Types

 - IDENTITY(1,1): Auto-incrementing primary keys across tables (e.g., `users`, `books`, `loans`).
 - DATETIME2: High-precision timestamps for `created_at` and `updated_at`.
 - BIT: Boolean-like flags for fields such as `active`.
 - DECIMAL(p,s): Monetary amounts (e.g., fines, totals) use `DECIMAL(10,2)`.

### Constraints and Integrity

 - CHECK constraints: Enforce valid enums for `status`, `reason`, and `type` columns (e.g., `loans.status`, `fines.reason`).
 - FOREIGN KEY constraints: Maintain referential integrity between core entities (users/members, books/copies, loans, fines, transactions).
 - UNIQUE constraints: Prevent duplicates (`users.username`, `users.email`, `books.isbn`, `librarians.employee_id`).

### Indexing Strategy

 - Baseline indexes: On key lookup columns (`users.username`, `users.email`, `books.isbn`, `books.title`, `loans.status`, `loans.due_date`, etc.).
 - Covering and filtered indexes (see `01b_index_optimizations.sql`):
   - `IX_book_copies_book_status` to accelerate availability checks per book.
   - `IX_loans_status_due` and filtered `IX_loans_active_due` for overdue/active loan queries.
   - Filtered `IX_reservations_pending` for quick access to pending reservations.
   - Additional covering indexes on `books`, `fines`, and `transactions` supporting reporting.

### Stored Procedures and Transactions

 - Procedural operations implemented with `CREATE OR ALTER PROCEDURE`:
   - `GetUserWithRoles`, `SearchBooks`
   - `CheckoutBook`, `ReturnBook`
   - `ProcessFinePayment`
   - Reporting procs: `GetOverdueLoansReport`, `GetMemberActivityReport`, `GetBookUsageReport`, `GetCategoryPerformanceReport`, `GetFinancialReport`, `GetSystemUsagePatterns`, `GetInventoryHealthReport`.
 - Transaction handling with `BEGIN TRANSACTION`, `COMMIT`, `ROLLBACK`.
 - Error handling with `RAISERROR` and guarded validations.
 - Identity retrieval with `SCOPE_IDENTITY()`.

### Date/Time Utilities

 - `GETDATE()` for current timestamps.
 - `DATEADD`, `DATEDIFF`, `DATEPART`, `DATEFROMPARTS` for time arithmetic and reporting windows.

### Aggregations and String Ops

 - `STRING_AGG(... WITHIN GROUP (ORDER BY ...))` to join author names or roles.
 - `CONCAT` for safe string building.

### Performance Testing Support

 - `91_performance_seed_volume.sql`: bulk dataset seeding using system catalog cross joins for scalable row generation.
 - `90_performance_harness.sql`: randomized workload invoking business procedures; collects quick metrics with `SET STATISTICS IO/TIME` and DMV snapshots (`sys.dm_exec_query_stats`).
 - `RESULTS_PERFORMANCE_TEMPLATE.md`: template to capture baseline vs optimized runs.

### Notes and Alternatives

 - Triggers: Omitted from base schema to avoid batch issues; can be added with `GO` separation if needed.
 - Full-Text Search: Not enabled by default; consider `CREATE FULLTEXT INDEX` on `books.title` and `authors.name` for richer search.
 - Partitioning: Consider range partitioning on large tables (`loans`, `transactions`) if data grows significantly.


