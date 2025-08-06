// frontend/app/admin/dashboard/page.tsx
"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { LibraryCard } from "@/components/library-card";
import { LibraryHeader } from "@/components/library-header";
import { LibrarySidebar } from "@/components/library-sidebar";
import { LibraryFooter } from "@/components/library-footer";
import { getBooks, getAllUsers } from "@/lib/api";
import { Book } from "@/types/book";
import { User } from "@/types/user";

export default function AdminDashboardPage() {
  const [books, setBooks] = useState<Book[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const router = useRouter();

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      router.push("/login");
      return;
    }

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
        // Handle token expiration, e.g., redirect to login
        if (error.message.includes("401")) {
          router.push("/login");
        }
      }
    };
    fetchData();
  }, [router]);

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
            <h2 className="text-2xl font-bold text-dark-gray mb-4">Books</h2>
            <ul>
              {books.map((book) => (
                <li key={book.id}>{book.title}</li>
              ))}
            </ul>
          </LibraryCard>
          <LibraryCard>
            <h2 className="text-2xl font-bold text-dark-gray mb-4">Users</h2>
            <ul>
              {users.map((user) => (
                <li key={user.id}>{user.fullName}</li>
              ))}
            </ul>
          </LibraryCard>
        </div>
      </main>
      <LibraryFooter />
    </div>
  );
}
