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
  const categoryId = searchParams.get("categoryId");
  const categoryName = searchParams.get("categoryName");
  const [books, setBooks] = useState<Book[]>([]);
  const { isAuthReady } = useAuth();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!isAuthReady) return;

    const fetchBooks = async () => {
      try {
        setLoading(true);
        let response;
        if (query) {
          response = await apiClient.get(
            `/books/search?query=${encodeURIComponent(query)}`,
            { public: true } as any
          );
        } else if (categoryId) {
          response = await apiClient.get(`/books/genre/${categoryId}`, {
            public: true,
          } as any);
        }
        if (response) {
          setBooks(response.data);
        }
      } catch (error) {
        console.error("Failed to fetch search results:", error);
      } finally {
        setLoading(false);
      }
    };

    if (query || categoryId) {
      fetchBooks();
    } else {
      setBooks([]);
      setLoading(false);
    }
  }, [query, isAuthReady, categoryId]);

  const getTitle = () => {
    if (query) {
      return `Search Results for "${query}"`;
    }
    if (categoryName) {
      return `Books in category: "${categoryName}"`;
    }
    if (categoryId) {
      return "Books in category";
    }
    return "Search Results";
  };

  return (
    <div className="container mx-auto py-10">
      <h1 className="text-3xl font-bold mb-6">{getTitle()}</h1>
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
