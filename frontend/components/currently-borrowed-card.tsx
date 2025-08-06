// frontend/components/currently-borrowed-card.tsx
"use client";

import { LibraryCard } from "./library-card";
import { Book } from "@/types/book";
import { useEffect, useState } from "react";
import { getBooks } from "@/lib/api";

export function CurrentlyBorrowedCard() {
  const [borrowedBooks, setBorrowedBooks] = useState<Book[]>([]);

  useEffect(() => {
    async function fetchBorrowedBooks() {
      // This is a mock. In a real app, you would fetch the user's borrowed books.
      try {
        const allBooks = await getBooks();
        setBorrowedBooks(allBooks.slice(0, 3));
      } catch (error) {
        console.error("Failed to fetch books", error);
      }
    }
    fetchBorrowedBooks();
  }, []);

  return (
    <LibraryCard className="p-6">
      <h2 className="text-xl font-semibold text-dark-gray mb-4">
        Currently Borrowed
      </h2>
      <div className="space-y-4">
        {borrowedBooks.map((book) => (
          <div key={book.id} className="flex items-center space-x-4">
            <div className="w-16 h-24 bg-gray-200 rounded-md"></div>
            <div className="flex-1">
              <h3 className="font-semibold text-dark-gray">{book.title}</h3>
              <p className="text-sm text-dark-gray/70">by {book.authorName}</p>
              <div className="mt-2">
                <p className="text-xs text-dark-gray/60">
                  Due: October 26, 2024
                </p>
                <div className="w-full bg-gray-200 rounded-full h-1.5 mt-1">
                  <div
                    className="bg-modern-teal h-1.5 rounded-full"
                    style={{ width: "75%" }}
                  ></div>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </LibraryCard>
  );
}
