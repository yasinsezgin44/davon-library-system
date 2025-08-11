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
    const executeSearch = async () => {
      if (!isAuthReady) return;
      setLoading(true);
      try {
        const params = new URLSearchParams();
        if (query) {
          params.append("query", query);
        } else if (categoryId) {
          params.append("categoryId", categoryId);
        }

        if (params.toString()) {
          const response = await apiClient.get(
            `/books/search?${params.toString()}`
          );
          setBooks(response.data);
        } else {
          setBooks([]);
        }
      } catch (error) {
        console.error("Failed to fetch search results:", error);
        setBooks([]);
      } finally {
        setLoading(false);
      }
    };

    executeSearch();
  }, [query, categoryId, isAuthReady]);

  let title = "Search Results";
  if (query) {
    title = `Search Results for "${query}"`;
  } else if (categoryName) {
    title = `Books in category: "${categoryName}"`;
  }

  return (
    <div className="container mx-auto py-10">
      <h1 className="text-3xl font-bold mb-6">{title}</h1>
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
