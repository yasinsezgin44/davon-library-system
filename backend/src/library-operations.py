from .book import Book 

def add_new_book(title, author, isbn):

    if not title or not author or not isbn:
        print("Error: Title, author, and ISBN are required.")
        return None # Return None or raise an error

    new_book = Book(title, author, isbn)
    print(f"Created new book: {new_book.title}")

    return new_book