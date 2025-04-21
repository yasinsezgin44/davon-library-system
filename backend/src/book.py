class Book:
    def __init__(self, title, author, isbn):
        self.title = title
        self.author = author
        self.isbn = isbn

    def __str__(self):
        return f"'{self.title}' by {self.author} (ISBN: {self.isbn})"

    def __repr__(self):
         return f"Book(title='{self.title}', author='{self.author}', isbn='{self.isbn}')"
