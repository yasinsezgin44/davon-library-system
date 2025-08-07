// frontend/app/admin/dashboard/page.tsx
"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { LibraryCard } from "@/components/library-card";
import {
  getBooks,
  getAllUsers,
  createBook,
  deleteBook,
  deleteUser,
} from "@/lib/api";
import { Book } from "@/types/book";
import { User } from "@/types/user";
import { useToastHelpers } from "@/components/toast-notification";
import withAuth from "@/components/auth/withAuth";
import { AppLayout } from "@/components/layout/AppLayout";
import { BookForm } from "@/components/forms/BookForm";
import { UserTable } from "@/components/UserTable";
import { SubmitHandler } from "react-hook-form";

function AdminDashboardPage() {
  const [books, setBooks] = useState<Book[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const { success, error } = useToastHelpers();

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [booksData, usersData] = await Promise.all([
        getBooks(),
        getAllUsers(),
      ]);
      setBooks(booksData);
      setUsers(usersData);
    } catch (err) {
      console.error("Failed to fetch dashboard data:", err);
      error("Failed to load data", "There was a problem fetching data.");
    }
  };

  const handleAddBook: SubmitHandler<Book> = async (data) => {
    try {
      await createBook(data);
      fetchData(); // Refresh the book list
      success("Book added!", "The new book has been successfully added.");
    } catch (err) {
      error("Failed to add book", "Please try again later.");
    }
  };

  const handleDeleteBook = async (id: number) => {
    if (window.confirm("Are you sure you want to delete this book?")) {
      try {
        await deleteBook(id);
        fetchData(); // Refresh the book list
        success("Book deleted!", "The book has been successfully deleted.");
      } catch (err) {
        error("Failed to delete book", "Please try again later.");
      }
    }
  };

  const handleDeleteUser = async (id: number) => {
    if (window.confirm("Are you sure you want to delete this user?")) {
      try {
        await deleteUser(id);
        fetchData(); // Refresh the user list
        success("User deleted!", "The user has been successfully deleted.");
      } catch (err) {
        error("Failed to delete user", "Please try again later.");
      }
    }
  };

  return (
    <AppLayout>
      <h1 className="text-3xl font-bold text-dark-gray mb-8">
        Admin Dashboard
      </h1>
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <LibraryCard>
          <h2 className="text-2xl font-bold text-dark-gray mb-4">
            Add New Book
          </h2>
          <BookForm onSubmit={handleAddBook} />
        </LibraryCard>
        <LibraryCard>
          <h2 className="text-2xl font-bold text-dark-gray mb-4">Books</h2>
          <ul>
            {books.map((book) => (
              <li key={book.id} className="flex justify-between items-center">
                {book.title}
                <div>
                  <Link
                    href={`/books/${book.id}/edit`}
                    className="text-blue-500 hover:underline mr-4"
                  >
                    Edit
                  </Link>
                  <button
                    onClick={() => handleDeleteBook(book.id)}
                    className="text-red-500 hover:underline"
                  >
                    Delete
                  </button>
                </div>
              </li>
            ))}
          </ul>
        </LibraryCard>
      </div>
      <div className="mt-8">
        <LibraryCard>
          <h2 className="text-2xl font-bold text-dark-gray mb-4">Users</h2>
          <UserTable users={users} onDelete={handleDeleteUser} />
        </LibraryCard>
      </div>
    </AppLayout>
  );
}

export default withAuth(AdminDashboardPage, ["ADMIN"]);
