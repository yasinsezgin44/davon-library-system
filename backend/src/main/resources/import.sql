-- Clear existing data
DELETE FROM book_authors;
DELETE FROM authors;
DELETE FROM books;
DELETE FROM users;

-- Reset identity columns
DBCC CHECKIDENT ('authors', RESEED, 0);
DBCC CHECKIDENT ('books', RESEED, 0);
DBCC CHECKIDENT ('users', RESEED, 0);

-- Insert Authors
INSERT INTO authors (name, biography, birth_date, created_at, updated_at) VALUES
('J.K. Rowling', 'Author of the Harry Potter series.', '1965-07-31', GETDATE(), GETDATE()),
('George R.R. Martin', 'Author of A Song of Ice and Fire.', '1948-09-20', GETDATE(), GETDATE()),
('J.R.R. Tolkien', 'Author of The Lord of the Rings.', '1892-01-03', GETDATE(), GETDATE()),
('Isaac Asimov', 'A prolific writer of science fiction.', '1920-01-02', GETDATE(), GETDATE());

-- Insert Books
INSERT INTO books (title, isbn, publication_year, description, created_at, updated_at) VALUES
('Harry Potter and the Sorcerer''s Stone', '9780590353427', 1997, 'The first book in the Harry Potter series.', GETDATE(), GETDATE()),
('A Game of Thrones', '9780553386790', 1996, 'The first book in A Song of Ice and Fire.', GETDATE(), GETDATE()),
('The Hobbit', '9780618260300', 1937, 'A fantasy novel and prequel to The Lord of the Rings.', GETDATE(), GETDATE()),
('Foundation', '9780553803719', 1951, 'The first book in the Foundation series.', GETDATE(), GETDATE());

-- Link Books and Authors
INSERT INTO book_authors (book_id, author_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4);

-- Insert Users
INSERT INTO users (username, password_hash, full_name, email, active, created_at, updated_at) VALUES
('john.doe', 'hash123', 'John Doe', 'john.doe@example.com', 1, GETDATE(), GETDATE()),
('jane.smith', 'hash456', 'Jane Smith', 'jane.smith@example.com', 1, GETDATE(), GETDATE()),
('test.user', 'hash789', 'Test User', 'test.user@example.com', 0, GETDATE(), GETDATE());
