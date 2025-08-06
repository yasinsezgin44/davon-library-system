// frontend/app/admin/dashboard/page.tsx
"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { LibraryCard } from "@/components/library-card";
import { LibraryHeader } from "@/components/library-header";
import { LibrarySidebar } from "@/components/library-sidebar";
import { LibraryFooter } from "@/components/library-footer";
import { getBooks, getAllUsers, createBook } from "@/lib/api";
import { Book } from "@/types/book";
import { User } from "@/types/user";
import { LibraryButton } from "@/components/library-button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useToastHelpers } from "@/components/toast-notification";

export default function AdminDashboardPage() {
  const [books, setBooks] = useState<Book[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [newBookTitle, setNewBookTitle] = useState("");
  const [newBookIsbn, setNewBookIsbn] = useState("");
  const router = useRouter();
  const { success, error } = useToastHelpers();

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      router.push("/login");
      return;
    }
    fetchData();
  }, [router]);

  const fetchData = async () => {
    try {
      const [booksData, usersData] = await Promise.all([
        getBooks(),
        getAllUsers(),
      ]);
      setBooks(booksData);
      setUsers(usersData);
    } catch (error) {
      console.error("Failed to fetch dashboard data:", error);
      if (error.message.includes("401")) {
        router.push("/login");
      }
    }
  };

  const handleAddBook = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await createBook({ title: newBookTitle, isbn: newBookIsbn });
      setNewBookTitle("");
      setNewBookIsbn("");
      fetchData(); // Refresh the book list
      success("Book added!", "The new book has been successfully added.");
    } catch (err) {
      error("Failed to add book", "Please try again later.");
    }
  };

  return (
    <div className="min-h-screen bg-clean-white">
      <LibraryHeader />
      <LibrarySidebar />
      <main className="lg:ml-64 p-4 lg:p-8">
        <h1 className="text-3xl font-bold text-dark-gray mb-8">
          Admin Dashboard
        </h1>
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <LibraryCard>
            <h2 className="text-2xl font-bold text-dark-gray mb-4">
              Add New Book
            </h2>
            <form onSubmit={handleAddBook}>
              <div className="space-y-4">
                <div>
                  <Label htmlFor="title">Title</Label>
                  <Input
                    id="title"
                    type="text"
                    value={newBookTitle}
                    onChange={(e) => setNewBookTitle(e.target.value)}
                  />
                </div>
                <div>
                  <Label htmlFor="isbn">ISBN</Label>
                  <Input
                    id="isbn"
                    type="text"
                    value={newBookIsbn}
                    onChange={(e) => setNewBookIsbn(e.target.value)}
                  />
                </div>
              </div>
              <LibraryButton type="submit" className="mt-6">
                Add Book
              </LibraryButton>
            </form>
          </LibraryCard>
          <LibraryCard>
            <h2 className="text-2xl font-bold text-dark-gray mb-4">Books</h2>
            <ul>
              {books.map((book) => (
                <li key={book.id}>{book.title}</li>
              ))}
            </ul>
          </LibraryCard>
        </div>
      </main>
      <LibraryFooter />
    </div>
  );
}
