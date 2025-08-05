// frontend/app/books/new/page.tsx
"use client";

import { useState } from "react";
import { LibraryButton } from "@/components/library-button";
import { useToastHelpers } from "@/components/toast-notification";
import { fetchApi } from "@/lib/api";
import { useRouter } from "next/navigation";

export default function NewBookPage() {
  const [title, setTitle] = useState("");
  const [author, setAuthor] = useState("");
  const [isbn, setIsbn] = useState("");
  const [published, setPublished] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const { success, error } = useToastHelpers();
  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      await fetchApi("/books", {
        method: "POST",
        body: JSON.stringify({
          title,
          author,
          isbn,
          published,
          available: true,
        }),
      });
      success("Book created!", "The new book has been added to the library.");
      router.push("/books");
    } catch (err) {
      error("Creation failed", "An error occurred while creating the book.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-clean-white lg:ml-64 p-4 lg:p-8">
      <div className="max-w-xl mx-auto pt-32 lg:pt-16">
        <h1 className="text-3xl font-bold text-dark-gray mb-8">Add New Book</h1>
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
            {isLoading ? "Adding Book..." : "Add Book"}
          </LibraryButton>
        </form>
      </div>
    </div>
  );
}
