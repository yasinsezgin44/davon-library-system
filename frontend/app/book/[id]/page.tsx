"use client";

import { useState, useEffect } from "react";
import { LibrarySidebar } from "@/components/library-sidebar";
import { LibraryHeader } from "@/components/library-header";
import { LibraryButton } from "@/components/library-button";
import { fetchApi } from "@/lib/api";
import Link from "next/link";
import { ArrowLeft } from "lucide-react";
import { useRouter } from "next/navigation";

interface Book {
  id: number;
  title: string;
  author: string;
  isbn: string;
  published: string;
  available: boolean;
}

export default function BookDetailPage({ params }: { params: { id: string } }) {
  const router = useRouter();
  const [book, setBook] = useState<Book | null>(null);

  useEffect(() => {
    async function getBook() {
      const bookData = await fetchApi(`/books/${params.id}`);
      setBook(bookData);
    }
    getBook();
  }, [params.id]);

  const handleDelete = async () => {
    if (window.confirm("Are you sure you want to delete this book?")) {
      try {
        await fetchApi(`/books/${params.id}`, { method: "DELETE" });
        router.push("/books");
      } catch (err) {
        // Handle error
      }
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
        <div className="max-w-4xl mx-auto">
          <div className="mb-8 pt-32 lg:pt-16">
            <Link
              href="/books"
              className="flex items-center space-x-2 text-dark-gray/70 hover:text-dark-gray transition-colors mb-6"
            >
              <ArrowLeft className="h-4 w-4" />
              <span>Back to Books</span>
            </Link>
          </div>

          <div className="bg-white shadow-lg rounded-lg overflow-hidden">
            <div className="p-8">
              <h1 className="text-4xl font-bold text-dark-gray mb-3 leading-tight">
                {book.title}
              </h1>
              <p className="text-xl text-dark-gray/70 mb-4">by {book.author}</p>
              <div className="grid grid-cols-2 gap-4 pt-4 border-t border-gray-200">
                <div>
                  <p className="text-sm font-medium text-gray-500">ISBN</p>
                  <p className="text-lg text-dark-gray">{book.isbn}</p>
                </div>
                <div>
                  <p className="text-sm font-medium text-gray-500">
                    Published Date
                  </p>
                  <p className="text-lg text-dark-gray">
                    {new Date(book.published).toLocaleDateString()}
                  </p>
                </div>
                <div>
                  <p className="text-sm font-medium text-gray-500">
                    Availability
                  </p>
                  <p
                    className={`text-lg ${
                      book.available ? "text-green-600" : "text-red-600"
                    }`}
                  >
                    {book.available ? "Available" : "Not Available"}
                  </p>
                </div>
              </div>
              <div className="mt-8 flex gap-4">
                <Link href={`/books/${book.id}/edit`}>
                  <LibraryButton>Update</LibraryButton>
                </Link>
                <LibraryButton onClick={handleDelete} variant="danger">
                  Delete
                </LibraryButton>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
