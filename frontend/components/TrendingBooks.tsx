"use client";
import React, { useState, useEffect } from "react";
import apiClient from "../lib/apiClient";
import { useAuth } from "../context/AuthContext";
import BookCard from "./BookCard";

type TrendingBook = {
  id: number;
  title: string;
  author: string;
  coverImageUrl: string;
};

const TrendingBooks = () => {
  const [books, setBooks] = useState<TrendingBook[]>([]);
  const { isAuthReady } = useAuth();

  useEffect(() => {
    if (!isAuthReady) return;
    const fetchTrendingBooks = async () => {
      try {
        const response = await apiClient.get("/books/trending", {
          public: true,
        } as any);
        setBooks(response.data);
      } catch (error) {
        console.error("Failed to fetch trending books:", error);
      }
    };
    fetchTrendingBooks();
  }, [isAuthReady]);

  return (
    <div className="py-8">
      <h2 className="text-2xl font-bold mb-6">Trending Books</h2>
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-6">
        {books.map((book) => (
          <BookCard
            key={book.id}
            id={book.id}
            title={book.title}
            author={book.author}
            imageUrl={book.coverImageUrl || ""}
          />
        ))}
      </div>
    </div>
  );
};

export default TrendingBooks;
