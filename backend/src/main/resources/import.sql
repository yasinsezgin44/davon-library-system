-- =================================================================
-- Comprehensive Seed Script for Library Management System
-- =================================================================

-- =================================================================
-- 1. Core Data: Authors, Publishers, Categories
-- =================================================================
INSERT INTO authors (name, biography, birth_date, created_at, updated_at) VALUES
('George Orwell', 'English novelist, essayist, journalist and critic.', '1903-06-25', GETDATE(), GETDATE()),
('J.R.R. Tolkien', 'English writer, poet, philologist, and academic.', '1892-01-03', GETDATE(), GETDATE()),
('Frank Herbert', 'American science fiction author best known for the novel Dune.', '1920-10-08', GETDATE(), GETDATE()),
('Jane Austen', 'English novelist known for her six major novels.', '1775-12-16', GETDATE(), GETDATE()),
('Isaac Asimov', 'American writer and professor of biochemistry at Boston University.', '1920-01-02', GETDATE(), GETDATE());

INSERT INTO publishers (name, address, contact, created_at, updated_at) VALUES
('Penguin Books', '17, Hudson St, New York, NY 10014, USA', 'contact@penguin.com', GETDATE(), GETDATE()),
('HarperCollins', '195, Broadway, New York, NY 10007, USA', 'info@harpercollins.com', GETDATE(), GETDATE()),
('Chilton Books', 'Radnor, Pennsylvania, USA', 'support@chilton.com', GETDATE(), GETDATE());

INSERT INTO categories (name, description, created_at, updated_at) VALUES
('Dystopian Fiction', 'A genre of speculative fiction that explores social and political structures in a dark, nightmare world.', GETDATE(), GETDATE()),
('High Fantasy', 'A subgenre of fantasy fiction, defined by its setting in an imaginary world.', GETDATE(), GETDATE()),
('Science Fiction', 'A genre of speculative fiction that typically deals with imaginative and futuristic concepts.', GETDATE(), GETDATE()),
('Classic Romance', 'Romance novels that are considered classics of literature.', GETDATE(), GETDATE());


-- =================================================================
-- 2. Book Information
-- =================================================================
-- Note: publisher_id and category_id correspond to the IDs from the inserts above
INSERT INTO books (title, isbn, publication_year, description, publisher_id, category_id, created_at, updated_at) VALUES
('1984', '9780451524935', 1949, 'A dystopian social science fiction novel and cautionary tale.', 1, 1, GETDATE(), GETDATE()),
('The Lord of the Rings', '9780618640157', 1954, 'An epic high-fantasy novel.', 2, 2, GETDATE(), GETDATE()),
('Dune', '9780441013593', 1965, 'A landmark science fiction novel.', 3, 3, GETDATE(), GETDATE()),
('Pride and Prejudice', '9780141439518', 1813, 'A romantic novel of manners.', 1, 4, GETDATE(), GETDATE()),
('Foundation', '9780553803719', 1951, 'The first book in the Foundation series.', 2, 3, GETDATE(), GETDATE());

-- Link books to their authors
INSERT INTO book_authors (book_id, author_id) VALUES
(1, 1), -- 1984 by George Orwell
(2, 2), -- The Lord of the Rings by J.R.R. Tolkien
(3, 3), -- Dune by Frank Herbert
(4, 4), -- Pride and Prejudice by Jane Austen
(5, 5); -- Foundation by Isaac Asimov


-- =================================================================
-- 3. User and Role Management
-- =================================================================
INSERT INTO roles (name, description, created_at, updated_at) VALUES
('ADMIN', 'System administrator with full access.', GETDATE(), GETDATE()),
('LIBRARIAN', 'Library staff with rights to manage books and loans.', GETDATE(), GETDATE()),
('MEMBER', 'Registered user with rights to borrow books.', GETDATE(), GETDATE());

-- Insert base user records (Passwords are placeholders)
INSERT INTO users (username, password_hash, full_name, email, phone_number, created_at, updated_at) VALUES
('admin_user', 'hashed_password_admin', 'Alice Admin', 'alice.admin@library.com', '555-0101', GETDATE(), GETDATE()),
('librarian_user', 'hashed_password_librarian', 'Bob Librarian', 'bob.librarian@library.com', '555-0102', GETDATE(), GETDATE()),
('member_user_1', 'hashed_password_member1', 'Charlie Member', 'charlie.member@email.com', '555-0103', GETDATE(), GETDATE()),
('member_user_2', 'hashed_password_member2', 'Diana Member', 'diana.member@email.com', '555-0104', GETDATE(), GETDATE());

-- Assign roles to users
-- Note: user_id and role_id correspond to the IDs from the inserts above
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- Alice is an ADMIN
(2, 2), -- Bob is a LIBRARIAN
(3, 3), -- Charlie is a MEMBER
(4, 3); -- Diana is a MEMBER

-- Create role-specific table entries
INSERT INTO admins (user_id, admin_level, department) VALUES (1, 1, 'IT');
INSERT INTO librarians (user_id, employee_id) VALUES (2, 'L-1023');
INSERT INTO members (user_id, address) VALUES
(3, '123 Bookworm Lane, Reading Town'),
(4, '456 Storybook Ave, Novel City');


-- =================================================================
-- 4. Library Inventory: Book Copies
-- =================================================================
-- Note: book_id corresponds to the IDs from the books table
INSERT INTO book_copies (book_id, status, location, created_at, updated_at) VALUES
(1, 'AVAILABLE', 'Fiction Aisle 1', GETDATE(), GETDATE()),      -- 1984
(1, 'CHECKED_OUT', 'On Loan', GETDATE(), GETDATE()),         -- 1984
(2, 'AVAILABLE', 'Fantasy Aisle 2', GETDATE(), GETDATE()),     -- The Lord of the Rings
(2, 'AVAILABLE', 'Fantasy Aisle 2', GETDATE(), GETDATE()),     -- The Lord of the Rings
(3, 'RESERVED', 'Reservation Shelf', GETDATE(), GETDATE()), -- Dune
(3, 'AVAILABLE', 'Sci-Fi Aisle 3', GETDATE(), GETDATE()),      -- Dune
(4, 'AVAILABLE', 'Classics Aisle 4', GETDATE(), GETDATE()),    -- Pride and Prejudice
(5, 'CHECKED_OUT', 'On Loan', GETDATE(), GETDATE());         -- Foundation


-- =================================================================
-- 5. Transactional Data: Loans and Reservations
-- =================================================================
-- Note: member_id and book_copy_id correspond to IDs above
INSERT INTO loans (member_id, book_copy_id, checkout_date, due_date, status, created_at, updated_at) VALUES
   (3, 2, DATEADD(day, -10, GETDATE()), DATEADD(day, 4, GETDATE()), 'ACTIVE', GETDATE(), GETDATE()),  -- Charlie borrowed a copy of 1984
   (4, 8, DATEADD(day, -5, GETDATE()), DATEADD(day, 9, GETDATE()), 'ACTIVE', GETDATE(), GETDATE());   -- Diana borrowed a copy of Foundation

INSERT INTO reservations (member_id, book_id, status, reservation_time) VALUES
(4, 3, 'READY_FOR_PICKUP', GETDATE()); -- Diana reserved Dune

