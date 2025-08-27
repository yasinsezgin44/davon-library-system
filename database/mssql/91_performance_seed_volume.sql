-- =============================================================
-- Volume Seed Script for Performance Testing
-- Populates large data sets to stress-test queries
-- =============================================================
SET NOCOUNT ON;
DECLARE @TargetBooks INT = 10000;
-- adjust as needed
DECLARE @TargetMembers INT = 5000;
-- adjust as needed
DECLARE @CopiesPerBook INT = 5;
-- adjust as needed
-- Ensure supporting rows exist
IF NOT EXISTS (
    SELECT 1
    FROM publishers
    WHERE name = 'Perf Publisher'
)
INSERT INTO publishers (name)
VALUES ('Perf Publisher');
IF NOT EXISTS (
    SELECT 1
    FROM categories
    WHERE name = 'Performance'
)
INSERT INTO categories (name, description)
VALUES ('Performance', 'Synthetic data category');
IF NOT EXISTS (
    SELECT 1
    FROM authors
    WHERE name = 'Perf Author'
)
INSERT INTO authors (name)
VALUES ('Perf Author');
DECLARE @PublisherId BIGINT = (
        SELECT TOP 1 id
        FROM publishers
        WHERE name = 'Perf Publisher'
    );
DECLARE @CategoryId BIGINT = (
        SELECT TOP 1 id
        FROM categories
        WHERE name = 'Performance'
    );
DECLARE @AuthorId BIGINT = (
        SELECT TOP 1 id
        FROM authors
        WHERE name = 'Perf Author'
    );
-- Insert books to reach target
DECLARE @ExistingBooks INT = (
        SELECT COUNT(*)
        FROM books
    );
IF @ExistingBooks < @TargetBooks BEGIN
DECLARE @ToInsert INT = @TargetBooks - @ExistingBooks;
;
WITH n AS (
    SELECT TOP (@ToInsert) ROW_NUMBER() OVER (
            ORDER BY (
                    SELECT NULL
                )
        ) AS rn
    FROM sys.all_objects a
        CROSS JOIN sys.all_objects b
)
INSERT INTO books (
        title,
        isbn,
        publication_year,
        description,
        pages,
        publisher_id,
        category_id
    )
SELECT CONCAT('Perf Bulk Book ', rn),
    RIGHT(CONCAT('0000000000000', rn + @ExistingBooks), 13),
    2024,
    'Bulk seeded book',
    300,
    @PublisherId,
    @CategoryId
FROM n;
INSERT INTO book_authors (book_id, author_id)
SELECT b.id,
    @AuthorId
FROM books b
WHERE b.category_id = @CategoryId
    AND NOT EXISTS (
        SELECT 1
        FROM book_authors ba
        WHERE ba.book_id = b.id
    );
END -- Create copies per book
;
WITH bl AS (
    SELECT id
    FROM books
)
INSERT INTO book_copies (
        book_id,
        acquisition_date,
        condition,
        status,
        location
    )
SELECT bl.id,
    GETDATE(),
    'New',
    'AVAILABLE',
    'PERF_VOL'
FROM bl
    OUTER APPLY (
        SELECT COUNT(*) AS existing
        FROM book_copies c
        WHERE c.book_id = bl.id
    ) x
WHERE x.existing < @CopiesPerBook;
-- Insert members to reach target
DECLARE @ExistingMembers INT = (
        SELECT COUNT(*)
        FROM members
    );
IF @ExistingMembers < @TargetMembers BEGIN
DECLARE @ToAdd INT = @TargetMembers - @ExistingMembers;
;
WITH n AS (
    SELECT TOP (@ToAdd) ROW_NUMBER() OVER (
            ORDER BY (
                    SELECT NULL
                )
        ) AS rn
    FROM sys.all_objects a
        CROSS JOIN sys.all_objects b
)
INSERT INTO users (
        username,
        password_hash,
        full_name,
        email,
        status
    )
SELECT CONCAT('bulk_member_', rn + @ExistingMembers),
    'x',
    CONCAT('Bulk Member ', rn + @ExistingMembers),
    CONCAT('bulk', rn + @ExistingMembers, '@mail.com'),
    'ACTIVE'
FROM n;
INSERT INTO members (user_id, address)
SELECT u.id,
    'BULK'
FROM users u
WHERE u.username LIKE 'bulk_member_%'
    AND NOT EXISTS (
        SELECT 1
        FROM members m
        WHERE m.user_id = u.id
    );
END PRINT 'Volume seed complete.';