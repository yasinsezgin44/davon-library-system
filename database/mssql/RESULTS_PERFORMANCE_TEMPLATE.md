## Performance Test Results – Davon Library Management System (MSSQL)

 - Date: 2025-08-27
 - Environment: SQL Server 15.0.4430.1 (RTM), Developer Edition (64-bit); CPU 8 logical; RAM ~6350 MB; Storage: local (unspecified)
 - Database name: LibraryManagementSystem
 - Dataset size: books=10000, book_copies=20003, members=5000, users=5002, loans=2002, loan_history=2003
 - Scripts used: `91_performance_seed_volume.sql`, `90_performance_harness.sql`, `01b_index_optimizations.sql`

### 1) Dataset Snapshot

 - Total books: 10000
 - Total book copies: 20003
 - Total members: 5000
 - Total loans: 2002

### 2) Baseline Measurements (no extra indexes)

- Query/Procedure: Available copy lookup (book_copies WHERE status='AVAILABLE')
  - Avg elapsed (ms): ~14
  - Avg CPU (ms): ~14
  - Logical reads: ~104 per execution (sample)
  - Notes: Random selection via ORDER BY NEWID() is the hotspot

- Query/Procedure: Random member selection (#member_ids temp pool)
  - Avg elapsed (ms): ~2
  - Avg CPU (ms): ~2
  - Logical reads: ~13 per execution
  - Notes: Stable costs; temp table scan

- Procedure: CheckoutBook (INSERT loans, UPDATE book_copies, INSERT loan_history)
  - Avg elapsed (ms): ~3 combined per checkout (per-statement ~0–1ms)
  - Avg CPU (ms): ~3 combined
  - Logical reads: small; dominated by available-copy lookup
  - Notes: Write path is efficient; lookup dominates

### 3) Optimized Measurements (after `01b_index_optimizations.sql`)

 - Query/Procedure: Available copy lookup (book_copies WHERE status='AVAILABLE')
   - Avg elapsed (ms): ~10
   - Avg CPU (ms): ~10
   - Logical reads: ~70 per execution (sample)
   - Delta vs baseline: ~-29% elapsed, ~-33% reads

 - Query/Procedure: Random member selection (#member_ids temp pool)
   - Avg elapsed (ms): ~2
   - Avg CPU (ms): ~2
   - Logical reads: ~13 per execution (sample)
   - Delta vs baseline: ~0%

 - Procedure: CheckoutBook (INSERT loans, UPDATE book_copies, INSERT loan_history)
   - Avg elapsed (ms): ~3 combined per checkout (per-statement ~0–1ms)
   - Avg CPU (ms): ~3 combined
   - Logical reads: minimal on target rows; dominated by availability lookup above
   - Delta vs baseline: ~0–small improvement (lookup dominates overall)

### 4) Query Store / Top CPU Consumers (snapshot)

```
total_cpu_ms  total_elapsed_ms  execution_count  avg_elapsed_ms  query_text (excerpt)
-------------------------------------------------------------------------------------
21327         21329             2000             ~10             SELECT TOP 1 id FROM book_copies WHERE status='AVAILABLE' ORDER BY NEWID()
 5732          5732             2000             ~2              SELECT TOP 1 id FROM #member_ids ORDER BY NEWID()
  548           548             2000             ~0.3            INSERT INTO loans (...)
  487           487             2000             ~0.2            INSERT INTO loan_history (... SELECT ... FROM book_copies ...)
  388           389             2000             ~0.2            UPDATE book_copies SET status='CHECKED_OUT' WHERE id=@BookCopyId
```

### 5) Observations & Recommendations

 - Hotspots: Random available-copy selection on `book_copies` dominates CPU/elapsed time.
 - Beneficial indexes: `IX_book_copies_book_status`, `IX_loans_status_due`, filtered `IX_loans_active_due`, filtered `IX_reservations_pending`.
 - Consider filtered indexes on: `book_copies(status='AVAILABLE')` by `book_id` (already covered via `(book_id, status)`), additional INCLUDEs if access patterns expand.
 - Next steps: Evaluate full-text for `books`/`authors` search; consider columnstore on `loan_history`/`transactions` for analytics; optionally replace `ORDER BY NEWID()` with a keyed sampling strategy if contention rises.
