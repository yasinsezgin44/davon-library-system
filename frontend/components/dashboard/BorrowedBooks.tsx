// Mock data for borrowed books - replace with API call
const borrowedBooks = [
  { id: 1, title: "Dune", author: "Frank Herbert", dueDate: "2024-08-15" },
  { id: 2, title: "1984", author: "George Orwell", dueDate: "2024-08-22" },
  {
    id: 3,
    title: "To Kill a Mockingbird",
    author: "Harper Lee",
    dueDate: "2024-09-01",
  },
];

const BorrowedBooks = () => {
  return (
    <div className="mt-8">
      <h2 className="text-2xl font-bold mb-4">Borrowed Books</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {borrowedBooks.map((book) => (
          <div key={book.id} className="p-4 border rounded-lg shadow-sm">
            <h3 className="font-bold">{book.title}</h3>
            <p className="text-sm text-gray-600">{book.author}</p>
            <p className="text-sm mt-2">
              <strong>Due Date:</strong> {book.dueDate}
            </p>
            <button className="mt-4 w-full bg-blue-500 text-white py-2 rounded hover:bg-blue-600 transition-colors duration-300">
              Return
            </button>
          </div>
        ))}
      </div>
    </div>
  );
};

export default BorrowedBooks;
