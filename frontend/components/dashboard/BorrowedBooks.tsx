import React, { useState, useEffect } from "react";
import apiClient from "../../lib/apiClient";

const BorrowedBooks = () => {
  const [books, setBooks] = useState([]);

  useEffect(() => {
    const fetchBorrowedBooks = async () => {
      try {
        const response = await apiClient.get("/dashboard/loans");
        setBooks(response.data);
      } catch (error) {
        console.error("Failed to fetch borrowed books:", error);
      }
    };
    fetchBorrowedBooks();
  }, []);

  return (
    <div className="mt-8">
      <h2 className="text-2xl font-bold mb-4">Borrowed Books</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {books.map((loan) => (
          <div key={loan.id} className="p-4 border rounded-lg shadow-sm">
            <h3 className="font-bold">{loan.book.title}</h3>
            <p className="text-sm text-gray-600">{loan.book.authorName}</p>
            <p className="text-sm mt-2">
              <strong>Due Date:</strong>{" "}
              {new Date(loan.dueDate).toLocaleDateString()}
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
