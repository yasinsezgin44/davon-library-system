"use client";

import { useSearchParams } from "next/navigation";
import { useEffect, useState } from "react";
import apiClient from "../../lib/apiClient";
import { useAuth } from "../../context/AuthContext";
import BookCard from "../../components/BookCard";

interface Book {
  id: number;
  title: string;
  author: string;
  coverImageUrl: string;
}

const SearchPage = () => {
  const searchParams = useSearchParams();
  const query = searchParams.get("query");
  const [books, setBooks] = useState<Book[]>([]);
  const { isAuthReady } = useAuth();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!isAuthReady) return;
    if (query) {
      const fetchBooks = async () => {
        try {
          setLoading(true);
          const response = await apiClient.get(`/books/search?query=${query}`);
          setBooks(response.data);
        } catch (error) {
          console.error("Failed to fetch search results:", error);
        } finally {
          setLoading(false);
        }
      };
      fetchBooks();
    } else {
      setBooks([]);
    }
  }, [query, isAuthReady]);

  return (
    <div className="container mx-auto py-10">
      <h1 className="text-3xl font-bold mb-6">Search Results for "{query}"</h1>
      {loading ? (
        <p>Loading...</p>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
          {books.map((book) => (
            <BookCard
              key={book.id}
              id={book.id}
              title={book.title}
              author={book.author}
              imageUrl={book.coverImageUrl}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default SearchPage;
