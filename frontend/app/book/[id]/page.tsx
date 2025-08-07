// frontend/app/book/[id]/page.tsx
"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { LibraryCard } from "@/components/library-card";
import { LibraryButton } from "@/components/library-button";
import { getBookById, reserveBook } from "@/lib/api";
import { Book } from "@/types/book";
import { useToastHelpers } from "@/components/toast-notification";
import { AppLayout } from "@/components/layout/AppLayout";
import withAuth from "@/components/auth/withAuth";

function BookDetailPage() {
  const [book, setBook] = useState<Book | null>(null);
  const params = useParams();
  const { success, error } = useToastHelpers();

  useEffect(() => {
    const fetchBook = async () => {
      try {
        const bookData = await getBookById(Number(params.id));
        setBook(bookData);
      } catch (err) {
        console.error("Failed to fetch book data:", err);
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
    <AppLayout>
      <LibraryCard>
        <h1 className="text-3xl font-bold text-dark-gray mb-4">{book.title}</h1>
        <p className="text-lg text-dark-gray mb-4">
          {book.authors?.map((a) => a.name).join(", ")}
        </p>
        <p className="text-dark-gray/70 mb-8">{book.description}</p>
        <LibraryButton onClick={handleReserve}>Reserve Book</LibraryButton>
      </LibraryCard>
    </AppLayout>
  );
}

export default withAuth(BookDetailPage, ["MEMBER"]);
