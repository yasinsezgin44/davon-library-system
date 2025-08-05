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
INSERT INTO authors (name, biography, birth_date) VALUES
('J.K. Rowling', 'Author of the Harry Potter series.', '1965-07-31'),
('George R.R. Martin', 'Author of A Song of Ice and Fire.', '1948-09-20'),
('J.R.R. Tolkien', 'Author of The Lord of the Rings.', '1892-01-03'),
('Isaac Asimov', 'A prolific writer of science fiction.', '1920-01-02');

-- Insert Books
INSERT INTO books (title, isbn, publication_year, description) VALUES
('Harry Potter and the Sorcerer''s Stone', '9780590353427', 1997, 'The first book in the Harry Potter series.'),
('A Game of Thrones', '9780553386790', 1996, 'The first book in A Song of Ice and Fire.'),
('The Hobbit', '9780618260300', 1937, 'A fantasy novel and prequel to The Lord of the Rings.'),
('Foundation', '9780553803719', 1951, 'The first book in the Foundation series.');

-- Link Books and Authors
INSERT INTO book_authors (book_id, author_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4);

-- Insert Users
INSERT INTO users (username, password_hash, full_name, email, active) VALUES
('john.doe', 'hash123', 'John Doe', 'john.doe@example.com', 1),
('jane.smith', 'hash456', 'Jane Smith', 'jane.smith@example.com', 1),
('test.user', 'hash789', 'Test User', 'test.user@example.com', 0);
