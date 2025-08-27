-- =============================================================
-- Performance Testing Harness (MSSQL)
-- =============================================================
SET NOCOUNT ON;
-- Parameters
DECLARE @NumMembers INT = 200;
DECLARE @NumBooks INT = 500;
DECLARE @CopiesPerBook INT = 3;
DECLARE @NumCheckouts INT = 2000;
PRINT 'Performance harness started at ' + CONVERT(vARCHAR(30), GETDATE(), 120);
-- Capture IO and TIME for ad-hoc timing review
SET STATISTICS IO ON;
SET STATISTICS TIME ON;
-- =============================================================
-- 1) Seed synthetic data if not present
-- =============================================================
-- Create temp tables for IDs
IF OBJECT_ID('tempdb..#book_ids') IS NOT NULL DROP TABLE #book_ids;
IF OBJECT_ID('tempdb..#member_ids') IS NOT NULL DROP TABLE #member_ids;
CREATE TABLE #book_ids (id BIGINT PRIMARY KEY);
CREATE TABLE #member_ids (id BIGINT PRIMARY KEY);
-- Ensure supporting rows exist
IF NOT EXISTS (
    SELECT 1
    FROM publishers
    WHERE name = 'Perf Publisher'
) BEGIN
INSERT INTO publishers (name)
VALUES ('Perf Publisher');
END;
IF NOT EXISTS (
    SELECT 1
    FROM categories
    WHERE name = 'Performance'
) BEGIN
INSERT INTO categories (name, description)
VALUES ('Performance', 'Synthetic data category');
END;
IF NOT EXISTS (
    SELECT 1
    FROM authors
    WHERE name = 'Perf Author'
) BEGIN
INSERT INTO authors (name)
VALUES ('Perf Author');
END;
DECLARE @PublisherId BIGINT = (
        SELECT TOP 1 id
        FROM publishers
        ORDER BY id
    );
DECLARE @CategoryId BIGINT = (
        SELECT TOP 1 id
        FROM categories
        WHERE name = 'Performance'
    );
DECLARE @AuthorId BIGINT = (
        SELECT TOP 1 id
        FROM authors
        ORDER BY id
    );
-- Insert books up to @NumBooks
DECLARE @ExistingBooks INT = (
        SELECT COUNT(*)
        FROM books
    );
IF @ExistingBooks < @NumBooks BEGIN;
WITH seq(n) AS (
    SELECT 1
    UNION ALL
    SELECT 1
    FROM sys.objects
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
SELECT TOP (@NumBooks - @ExistingBooks) CONCAT(
        'Perf Book ',
        ROW_NUMBER() OVER (
            ORDER BY (
                    SELECT NULL
                )
        )
    ),
    RIGHT(
        CONCAT(
            '0000000000000',
            ROW_NUMBER() OVER (
                ORDER BY (
                        SELECT NULL
                    )
            )
        ),
        13
    ),
    2024,
    'Synthetic book for performance testing',
    250,
    @PublisherId,
    @CategoryId
FROM seq s1
    CROSS JOIN seq s2;
-- produce enough rows
-- Link to author
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
END;
-- Create copies per book to reach @CopiesPerBook
;
WITH BookList AS (
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
    'PERF'
FROM BookList bl
    OUTER APPLY (
        SELECT COUNT(*) AS existing
        FROM book_copies bc
        WHERE bc.book_id = bl.id
    ) AS x
WHERE x.existing < @CopiesPerBook;
-- Ensure @NumMembers test members exist
DECLARE @ExistingMembers INT = (
        SELECT COUNT(*)
        FROM members
    );
IF @ExistingMembers < @NumMembers BEGIN
DECLARE @ToAdd INT = @NumMembers - @ExistingMembers;
;
WITH nums AS (
    SELECT TOP (@ToAdd) ROW_NUMBER() OVER (
            ORDER BY (
                    SELECT NULL
                )
        ) AS n
    FROM sys.all_objects
)
INSERT INTO users (
        username,
        password_hash,
        full_name,
        email,
        status
    )
SELECT CONCAT('perf_member_', n + @ExistingMembers),
    'x',
    CONCAT('Perf Member ', n + @ExistingMembers),
    CONCAT('perf', n + @ExistingMembers, '@mail.com'),
    'ACTIVE'
FROM nums;
INSERT INTO members (user_id, address)
SELECT id,
    'PERF'
FROM users u
WHERE u.username LIKE 'perf_member_%'
    AND NOT EXISTS (
        SELECT 1
        FROM members m
        WHERE m.user_id = u.id
    );
END;
-- Collect ID pools
INSERT INTO #book_ids (id) SELECT id FROM book_copies;
INSERT INTO #member_ids (id) SELECT user_id FROM members;
    -- =============================================================
    -- 2) Workload: Randomized checkouts and returns using stored procs
    -- =============================================================
DECLARE @i INT = 0;
DECLARE @Max INT = @NumCheckouts;
WHILE @i < @Max BEGIN
DECLARE @MemberId BIGINT = (
        SELECT TOP 1 id
        FROM #member_ids ORDER BY NEWID());
        DECLARE @CopyId BIGINT = (
                SELECT TOP 1 id
                FROM book_copies
                WHERE status = 'AVAILABLE'
                ORDER BY NEWID()
            );
IF @CopyId IS NOT NULL BEGIN BEGIN TRY EXEC CheckoutBook @MemberId = @MemberId,
@BookCopyId = @CopyId,
@DueDays = 7;
END TRY BEGIN CATCH -- ignore errors during perf run
END CATCH
END
SET @i + = 1;
END -- Partial returns to simulate mix
UPDATE TOP (50) loans
SET status = 'RETURNED',
    return_date = GETDATE()
WHERE status = 'ACTIVE';
-- =============================================================
-- 3) Capture metrics snapshot
-- =============================================================
SELECT t.name AS table_name,
    p.rows AS row_count,
    CAST(au.total_pages * 8.0 / 1024 AS DECIMAL(10, 2)) AS size_mb
FROM sys.tables t
    JOIN sys.indexes i ON t.object_id = i.object_id
    AND i.index_id IN (0, 1)
    JOIN sys.partitions p ON i.object_id = p.object_id
    AND i.index_id = p.index_id
    JOIN sys.allocation_units au ON p.partition_id = au.container_id
GROUP BY t.name,
    p.rows,
    au.total_pages
ORDER BY size_mb DESC;
-- Top 10 most expensive recent queries (by total_worker_time)
SELECT TOP 10 qs.total_worker_time / 1000 AS total_cpu_ms,
    qs.total_elapsed_time / 1000 AS total_elapsed_ms,
    qs.execution_count,
    (
        qs.total_elapsed_time / NULLIF(qs.execution_count, 0)
    ) / 1000 AS avg_elapsed_ms,
    DB_NAME(st.dbid) AS db_name,
    SUBSTRING(
        st.text,
        (qs.statement_start_offset / 2) + 1,
        (
            (
                CASE
                    qs.statement_end_offset
                    WHEN -1 THEN DATALENGTH(st.text)
                    ELSE qs.statement_end_offset
                END - qs.statement_start_offset
            ) / 2
        ) + 1
    ) AS query_text
FROM sys.dm_exec_query_stats qs
    CROSS APPLY sys.dm_exec_sql_text(qs.sql_handle) st
ORDER BY qs.total_worker_time DESC;
PRINT 'Performance harness finished at ' + CONVERT(vARCHAR(30), GETDATE(), 120);
SET STATISTICS IO OFF;
SET STATISTICS TIME OFF;