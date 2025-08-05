// frontend/app/books/[id]/edit/page.tsx
"use client";

import { useState, useEffect } from "react";
import { LibraryButton } from "@/components/library-button";
import { useToastHelpers } from "@/components/toast-notification";
import { fetchApi } from "@/lib/api";
import { useRouter, useParams } from "next/navigation";

interface Book {
  id: number;
  title: string;
  author: string;
  isbn: string;
  published: string;
  available: boolean;
}

export default function EditBookPage() {
  const [book, setBook] = useState<Book | null>(null);
  const [title, setTitle] = useState("");
  const [author, setAuthor] = useState("");
  const [isbn, setIsbn] = useState("");
  const [published, setPublished] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const { success, error } = useToastHelpers();
  const router = useRouter();
  const params = useParams();
  const id = params.id;

  useEffect(() => {
    async function getBook() {
      const bookData = await fetchApi(`/books/${id}`);
      setBook(bookData);
      setTitle(bookData.title);
      setAuthor(bookData.author);
      setIsbn(bookData.isbn);
      setPublished(new Date(bookData.published).toISOString().split("T")[0]);
    }
    getBook();
  }, [id]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      await fetchApi(`/books/${id}`, {
        method: "PUT",
        body: JSON.stringify({
          title,
          author,
          isbn,
          published,
          available: book?.available,
        }),
      });
      success("Book updated!", "The book has been successfully updated.");
      router.push(`/book/${id}`);
    } catch (err) {
      error("Update failed", "An error occurred while updating the book.");
    } finally {
      setIsLoading(false);
    }
  };

  if (!book) {
    return <div>Loading...</div>;
  }

  return (
    <div className="min-h-screen bg-clean-white lg:ml-64 p-4 lg:p-8">
      <div className="max-w-xl mx-auto pt-32 lg:pt-16">
        <h1 className="text-3xl font-bold text-dark-gray mb-8">Edit Book</h1>
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label
              htmlFor="title"
              className="block text-sm font-medium text-dark-gray mb-2"
            >
              Title
            </label>
            <input
              id="title"
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="w-full px-4 py-3 rounded-lg border border-gray-300 bg-white/60"
              required
            />
          </div>
          <div>
            <label
              htmlFor="author"
              className="block text-sm font-medium text-dark-gray mb-2"
            >
              Author
            </label>
            <input
              id="author"
              type="text"
              value={author}
              onChange={(e) => setAuthor(e.target.value)}
              className="w-full px-4 py-3 rounded-lg border border-gray-300 bg-white/60"
              required
            />
          </div>
          <div>
            <label
              htmlFor="isbn"
              className="block text-sm font-medium text-dark-gray mb-2"
            >
              ISBN
            </label>
            <input
              id="isbn"
              type="text"
              value={isbn}
              onChange={(e) => setIsbn(e.target.value)}
              className="w-full px-4 py-3 rounded-lg border border-gray-300 bg-white/60"
              required
            />
          </div>
          <div>
            <label
              htmlFor="published"
              className="block text-sm font-medium text-dark-gray mb-2"
            >
              Published Date
            </label>
            <input
              id="published"
              type="date"
              value={published}
              onChange={(e) => setPublished(e.target.value)}
              className="w-full px-4 py-3 rounded-lg border border-gray-300 bg-white/60"
              required
            />
          </div>
          <LibraryButton
            type="submit"
            className="w-full py-3 text-lg font-semibold"
            disabled={isLoading}
          >
            {isLoading ? "Updating Book..." : "Update Book"}
          </LibraryButton>
        </form>
      </div>
    </div>
  );
}
