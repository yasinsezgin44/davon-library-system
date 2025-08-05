// frontend/app/test-api/page.tsx
"use client";

import { useState, useEffect } from "react";
import { getDatabaseStatus, getBooks, getUsers } from "../../lib/api";
import { Book } from "../../types/book";
import { User } from "../../types/user";

export default function TestApiPage() {
  const [dbStatus, setDbStatus] = useState(null);
  const [books, setBooks] = useState<Book[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function fetchData() {
      try {
        const status = await getDatabaseStatus();
        setDbStatus(status);

        const booksData = await getBooks();
        setBooks(booksData);

        const usersData = await getUsers();
        setUsers(usersData);
      } catch (err) {
        setError(
          err instanceof Error ? err.message : "An unknown error occurred"
        );
      }
    }

    fetchData();
  }, []);

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-2xl font-bold mb-4">API Integration Test</h1>

      {error && (
        <div
          className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative"
          role="alert"
        >
          <strong className="font-bold">Error:</strong>
          <span className="block sm:inline"> {error}</span>
        </div>
      )}

      <div className="mt-4">
        <h2 className="text-xl font-semibold">Database Status</h2>
        {dbStatus ? (
          <pre className="bg-gray-100 p-2 rounded">
            {JSON.stringify(dbStatus, null, 2)}
          </pre>
        ) : (
          <p>Loading...</p>
        )}
      </div>

      <div className="mt-4">
        <h2 className="text-xl font-semibold">Books</h2>
        {books.length > 0 ? (
          <ul className="list-disc list-inside">
            {books.map((book) => (
              <li key={book.id}>
                {book.title} by {book.author}
              </li>
            ))}
          </ul>
        ) : (
          <p>No books found.</p>
        )}
      </div>

      <div className="mt-4">
        <h2 className="text-xl font-semibold">Users</h2>
        {users.length > 0 ? (
          <ul className="list-disc list-inside">
            {users.map((user) => (
              <li key={user.id}>
                {user.name} ({user.email})
              </li>
            ))}
          </ul>
        ) : (
          <p>No users found.</p>
        )}
      </div>
    </div>
  );
}
