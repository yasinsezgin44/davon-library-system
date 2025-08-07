// frontend/app/books/[id]/edit/page.tsx
"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { getBookById, updateBook } from "@/lib/api";
import { Book } from "@/types/book";
import { BookForm } from "@/components/forms/BookForm";
import { useToastHelpers } from "@/components/toast-notification";
import withAuth from "@/components/auth/withAuth";
import { AppLayout } from "@/components/layout/AppLayout";
import { SubmitHandler } from "react-hook-form";

function EditBookPage() {
  const [book, setBook] = useState<Book | null>(null);
  const params = useParams();
  const router = useRouter();
  const { success, error } = useToastHelpers();
  const id = Number(params.id);

  useEffect(() => {
    if (id) {
      const fetchBook = async () => {
        try {
          const bookData = await getBookById(id);
          setBook(bookData);
        } catch (err) {
          console.error("Failed to fetch book:", err);
          error(
            "Failed to load book",
            "There was a problem fetching the book data."
          );
        }
      };
      fetchBook();
    }
  }, [id, error]);

  const handleUpdateBook: SubmitHandler<Book> = async (data) => {
    try {
      await updateBook(id, data);
      success("Book updated!", "The book has been successfully updated.");
      router.push("/admin/dashboard");
    } catch (err) {
      console.error("Failed to update book:", err);
      error("Failed to update book", "Please try again later.");
    }
  };

  if (!book) {
    return <div>Loading...</div>;
  }

  return (
    <AppLayout>
      <h1 className="text-3xl font-bold text-dark-gray mb-8">Edit Book</h1>
      <BookForm onSubmit={handleUpdateBook} defaultValues={book} />
    </AppLayout>
  );
}

export default withAuth(EditBookPage, ["ADMIN", "LIBRARIAN"]);
