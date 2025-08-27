-- =============================================================
-- Index and Performance Optimizations for MSSQL Schema
-- Safe to run multiple times (checks sys.indexes before creating)
-- =============================================================
-- book_authors: speed up joins and aggregations
IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_book_authors_book_id'
        AND object_id = OBJECT_ID('book_authors')
) BEGIN CREATE NONCLUSTERED INDEX IX_book_authors_book_id ON book_authors (book_id) INCLUDE (author_id);
END IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_book_authors_author_id'
        AND object_id = OBJECT_ID('book_authors')
) BEGIN CREATE NONCLUSTERED INDEX IX_book_authors_author_id ON book_authors (author_id) INCLUDE (book_id);
END -- book_copies: support lookups for availability and book-level aggregations
IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_book_copies_book_status'
        AND object_id = OBJECT_ID('book_copies')
) BEGIN CREATE NONCLUSTERED INDEX IX_book_copies_book_status ON book_copies (book_id, status) INCLUDE (id, location);
END -- loans: common filters by status/due date and joins on member/copy
IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_loans_member_id'
        AND object_id = OBJECT_ID('loans')
) BEGIN CREATE NONCLUSTERED INDEX IX_loans_member_id ON loans (member_id);
END IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_loans_book_copy_id'
        AND object_id = OBJECT_ID('loans')
) BEGIN CREATE NONCLUSTERED INDEX IX_loans_book_copy_id ON loans (book_copy_id);
END IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_loans_status_due'
        AND object_id = OBJECT_ID('loans')
) BEGIN CREATE NONCLUSTERED INDEX IX_loans_status_due ON loans (status, due_date) INCLUDE (checkout_date, return_date);
END -- Filtered index for active loans only (useful for overdue/active queries)
IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_loans_active_due'
        AND object_id = OBJECT_ID('loans')
) BEGIN CREATE NONCLUSTERED INDEX IX_loans_active_due ON loans (due_date)
WHERE status = 'ACTIVE';
END -- reservations: prioritize pending and by time
IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_reservations_book_status_time'
        AND object_id = OBJECT_ID('reservations')
) BEGIN CREATE NONCLUSTERED INDEX IX_reservations_book_status_time ON reservations (book_id, status, reservation_time) INCLUDE (member_id, priority_number);
END -- Filtered index for pending reservations only
IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_reservations_pending'
        AND object_id = OBJECT_ID('reservations')
) BEGIN CREATE NONCLUSTERED INDEX IX_reservations_pending ON reservations (book_id, reservation_time)
WHERE status = 'PENDING';
END -- fines: lookup by member and pending fines
IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_fines_member_id'
        AND object_id = OBJECT_ID('fines')
) BEGIN CREATE NONCLUSTERED INDEX IX_fines_member_id ON fines (member_id) INCLUDE (status, amount, issue_date);
END IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_fines_pending'
        AND object_id = OBJECT_ID('fines')
) BEGIN CREATE NONCLUSTERED INDEX IX_fines_pending ON fines (member_id, issue_date)
WHERE status = 'PENDING';
END -- books: cover common search fields and provide covering data
IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_books_title_isbn_category'
        AND object_id = OBJECT_ID('books')
) BEGIN CREATE NONCLUSTERED INDEX IX_books_title_isbn_category ON books (title, isbn, category_id) INCLUDE (
    publisher_id,
    publication_year,
    cover_image,
    pages,
    created_at,
    updated_at
);
END -- transactions: reporting by month/type
IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_transactions_date_type'
        AND object_id = OBJECT_ID('transactions')
) BEGIN CREATE NONCLUSTERED INDEX IX_transactions_date_type ON transactions ([date], [type]) INCLUDE (amount);
END -- users: accelerate last_login queries (existing username/email indexes already present)
IF NOT EXISTS (
    SELECT 1
    FROM sys.indexes
    WHERE name = 'IX_users_last_login'
        AND object_id = OBJECT_ID('users')
) BEGIN CREATE NONCLUSTERED INDEX IX_users_last_login ON users (last_login) INCLUDE (status, active);
END -- Note: Review index usage with sys.dm_db_index_usage_stats after deploying.
-- Drop or adjust indexes that are not beneficial under real workloads.