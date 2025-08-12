"use client";

import { useState, useEffect } from "react";
import apiClient from "../../lib/apiClient";
import { useAuth } from "../../context/AuthContext";
import CreateBookModal from "./CreateBookModal";
import UpdateBookModal from "./UpdateBookModal";
import DeleteConfirmationModal from "./DeleteConfirmationModal";

export type Author = {
  id: number;
  name: string;
};

export type Book = {
  id: number;
  title: string;
  authors: Author[];
  isbn: string;
  quantity: number; // This will be derived or fetched separately
};

const BookManagementTable = () => {
  const [books, setBooks] = useState<Book[]>([]);
  const { isAuthReady } = useAuth();
  const [loading, setLoading] = useState(true);
  const [isCreateModalOpen, setCreateModalOpen] = useState(false);
  const [isUpdateModalOpen, setUpdateModalOpen] = useState(false);
  const [isDeleteModalOpen, setDeleteModalOpen] = useState(false);
  const [selectedBook, setSelectedBook] = useState<Book | null>(null);

  useEffect(() => {
    if (!isAuthReady) return;

    const fetchBooks = async () => {
      setLoading(true);
      try {
        const response = await apiClient.get("/books");
        const adaptedBooks = response.data.map((book: any) => ({
          ...book,
          authors: book.authors || [],
          quantity: book.copies?.length || 0,
        }));
        setBooks(adaptedBooks);
      } catch (error) {
        console.error("Failed to fetch books:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchBooks();
  }, [isAuthReady]);

  const handleCreate = async (
    bookData: Omit<Book, "id" | "quantity" | "authors"> & {
      authorIds: number[];
      publisherId: number;
      categoryId: number;
    }
  ) => {
    try {
      const response = await apiClient.post("/books", bookData);
      const newBook = {
        ...response.data,
        authors: response.data.authors || [],
        quantity: response.data.copies?.length || 0,
      };
      setBooks([...books, newBook]);
      setCreateModalOpen(false);
    } catch (error) {
      console.error("Failed to create book:", error);
    }
  };

  const handleUpdate = async (
    id: number,
    bookData: Partial<
      Omit<Book, "id" | "quantity" | "authors"> & {
        authorIds: number[];
        publisherId: number;
        categoryId: number;
      }
    >
  ) => {
    try {
      const response = await apiClient.put(`/books/${id}`, bookData);
      const updatedBook = {
        ...response.data,
        authors: response.data.authors || [],
        quantity: response.data.copies?.length || 0,
      };
      setBooks(books.map((book) => (book.id === id ? updatedBook : book)));
      setUpdateModalOpen(false);
      setSelectedBook(null);
    } catch (error) {
      console.error("Failed to update book:", error);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await apiClient.delete(`/books/${id}`);
      setBooks(books.filter((book) => book.id !== id));
      setDeleteModalOpen(false);
      setSelectedBook(null);
    } catch (error) {
      console.error("Failed to delete book:", error);
    }
  };

  const openUpdateModal = (book: Book) => {
    setSelectedBook(book);
    setUpdateModalOpen(true);
  };

  const openDeleteModal = (book: Book) => {
    setSelectedBook(book);
    setDeleteModalOpen(true);
  };

  if (loading) {
    return (
      <div className="text-center py-10">
        <p>Loading books...</p>
      </div>
    );
  }

  return (
    <div className="bg-white shadow-md rounded-lg overflow-hidden">
      <div className="flex justify-between items-center mb-4 px-6 py-4">
        <h2 className="text-2xl font-bold text-gray-800">Book Management</h2>
        <button
          onClick={() => setCreateModalOpen(true)}
          className="px-4 py-2 rounded-md font-semibold text-sm bg-green-500 text-white hover:bg-green-600"
        >
          Add New Book
        </button>
      </div>
      <div className="overflow-x-auto">
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
                    {book.authors.map((author) => author.name).join(", ")}
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
                  <div className="flex items-center">
                    <button
                      onClick={() => openUpdateModal(book)}
                      className="px-4 py-2 rounded-md font-semibold text-sm bg-indigo-500 text-white hover:bg-indigo-600 mr-2"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => openDeleteModal(book)}
                      className="px-4 py-2 rounded-md font-semibold text-sm bg-red-500 text-white hover:bg-red-600"
                    >
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <CreateBookModal
        isOpen={isCreateModalOpen}
        onClose={() => setCreateModalOpen(false)}
        onCreate={handleCreate}
      />
      <UpdateBookModal
        isOpen={isUpdateModalOpen}
        onClose={() => {
          setUpdateModalOpen(false);
          setSelectedBook(null);
        }}
        onUpdate={handleUpdate}
        book={selectedBook}
      />
      <DeleteConfirmationModal
        isOpen={isDeleteModalOpen}
        onClose={() => {
          setDeleteModalOpen(false);
          setSelectedBook(null);
        }}
        onConfirm={() => {
          if (selectedBook) {
            handleDelete(selectedBook.id);
          }
        }}
        itemName={selectedBook ? selectedBook.title : ""}
      />
    </div>
  );
};

export default BookManagementTable;
