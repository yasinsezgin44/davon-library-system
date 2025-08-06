// frontend/app/book/[id]/page.tsx
"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { LibraryCard } from "@/components/library-card";
import { LibraryHeader } from "@/components/library-header";
import { LibrarySidebar } from "@/components/library-sidebar";
import { LibraryFooter } from "@/components/library-footer";
import { LibraryButton } from "@/components/library-button";
import { getBookById, reserveBook } from "@/lib/api";
import { Book } from "@/types/book";
import { useToastHelpers } from "@/components/toast-notification";

export default function BookDetailPage() {
  const [book, setBook] = useState<Book | null>(null);
  const params = useParams();
  const router = useRouter();
  const { success, error } = useToastHelpers();

  useEffect(() => {
    const fetchBook = async () => {
      try {
        const bookData = await getBookById(Number(params.id));
        setBook(bookData);
      } catch (error) {
        console.error("Failed to fetch book data:", error);
      }
    };
    if (params.id) {
      fetchBook();
    }
  }, [params.id]);

  const handleReserve = async () => {
    try {
      await reserveBook(book!.id);
      success("Book reserved!", "You have successfully reserved this book.");
    } catch (err) {
      error("Reservation failed", "Please try again later.");
    }
  };

  if (!book) {
    return <div>Loading...</div>;
  }

  return (
    <div className="min-h-screen bg-clean-white">
      <LibraryHeader />
      <LibrarySidebar />
      <main className="lg:ml-64 p-4 lg:p-8">
        <LibraryCard>
          <h1 className="text-3xl font-bold text-dark-gray mb-4">
            {book.title}
          </h1>
          <p className="text-lg text-dark-gray mb-4">
            {book.authors?.map((a) => a.name).join(", ")}
          </p>
          <p className="text-dark-gray/70 mb-8">{book.description}</p>
          <LibraryButton onClick={handleReserve}>Reserve Book</LibraryButton>
        </LibraryCard>
      </main>
      <LibraryFooter />
    </div>
  );
}
