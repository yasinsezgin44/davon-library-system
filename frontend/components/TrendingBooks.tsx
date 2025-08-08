"use client";
import React, { useState, useEffect } from "react";
import apiClient from "../lib/apiClient";
import BookCard from "./BookCard";

const TrendingBooks = () => {
  const [books, setBooks] = useState([]);

  useEffect(() => {
    const fetchTrendingBooks = async () => {
      try {
        const response = await apiClient.get("/books/trending");
        console.log("Trending Books Response:", response.data);
        setBooks(response.data);
      } catch (error) {
        console.error("Failed to fetch trending books:", error);
      }
    };
    fetchTrendingBooks();
  }, []);

  return (
    <div className="py-8">
      <h2 className="text-2xl font-bold mb-6">Trending Books</h2>
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-6">
        {books.map((book) => (
          <BookCard
            key={book.id}
            title={book.title}
            author={book.authorName}
            imageUrl={book.coverImageUrl}
          />
        ))}
      </div>
    </div>
  );
};

export default TrendingBooks;
