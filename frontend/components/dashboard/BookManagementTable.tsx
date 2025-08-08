"use client";

import { useState, useEffect } from "react";
import apiClient from "../../lib/apiClient";

const BookManagementTable = () => {
  const [books, setBooks] = useState([]);
  // Add states for modals: const [isCreateModalOpen, setCreateModalOpen] = useState(false); etc.

  useEffect(() => {
    const fetchBooks = async () => {
      try {
        const response = await apiClient.get("/books");
        setBooks(response.data);
      } catch (error) {
        console.error("Failed to fetch books:", error);
      }
    };
    fetchBooks();
  }, []);

  const handleCreate = async (bookData) => {
    try {
      const response = await apiClient.post("/books", bookData);
      setBooks([...books, response.data]);
    } catch (error) {
      console.error("Failed to create book:", error);
    }
  };

  const handleUpdate = async (id, bookData) => {
    try {
      const response = await apiClient.put(`/books/${id}`, bookData);
      setBooks(books.map((book) => (book.id === id ? response.data : book)));
    } catch (error) {
      console.error("Failed to update book:", error);
    }
  };

  const handleDelete = async (id) => {
    try {
      await apiClient.delete(`/books/${id}`);
      setBooks(books.filter((book) => book.id !== id));
    } catch (error) {
      console.error("Failed to delete book:", error);
    }
  };

  return (
    <div className="mt-8">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-2xl font-bold">Book Management</h2>
        <button className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600">
          Add New Book
        </button>
      </div>
      <div className="bg-white shadow-md rounded-lg overflow-x-auto">
        <table className="min-w-full leading-normal">
          <thead>
            <tr>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                Title
              </th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                Author
              </th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                ISBN
              </th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                Stock
              </th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                Actions
              </th>
            </tr>
          </thead>
          <tbody>
            {books.map((book) => (
              <tr key={book.id}>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  <p className="text-gray-900 whitespace-no-wrap">
                    {book.title}
                  </p>
                </td>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  <p className="text-gray-900 whitespace-no-wrap">
                    {book.authorName}
                  </p>
                </td>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  <p className="text-gray-900 whitespace-no-wrap">
                    {book.isbn}
                  </p>
                </td>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  <p className="text-gray-900 whitespace-no-wrap">
                    {book.quantity}
                  </p>
                </td>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  <button className="text-indigo-600 hover:text-indigo-900 mr-4">
                    Edit
                  </button>
                  <button
                    onClick={() => handleDelete(book.id)}
                    className="text-red-600 hover:text-red-900"
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {/* Add Modals for Create, Update, Delete here */}
    </div>
  );
};

export default BookManagementTable;
