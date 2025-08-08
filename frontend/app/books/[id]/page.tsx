"use client";

import { useState, useEffect } from "react";
import { useParams } from "next/navigation";
import apiClient from "../../../lib/apiClient";
import Image from "next/image";

interface Book {
  id: number;
  title: string;
  author: string;
  description: string;
  publicationYear: number;
  isbn: string;
  coverImageUrl: string;
}

const BookDetailPage = () => {
  const [book, setBook] = useState<Book | null>(null);
  const params = useParams();
  const { id } = params;

  useEffect(() => {
    if (id) {
      const fetchBook = async () => {
        try {
          const response = await apiClient.get(`/books/${id}`);
          setBook(response.data);
        } catch (error) {
          console.error("Failed to fetch book:", error);
        }
      };
      fetchBook();
    }
  }, [id]);

  if (!book) {
    return <p>Loading...</p>;
  }

  return (
    <div className="container mx-auto py-10">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
        <div className="md:col-span-1">
          <Image
            src={book.coverImageUrl}
            alt={book.title}
            width={400}
            height={600}
            className="rounded-lg shadow-lg"
          />
        </div>
        <div className="md:col-span-2">
          <h1 className="text-4xl font-bold mb-4">{book.title}</h1>
          <p className="text-xl text-gray-700 mb-4">by {book.author}</p>
          <p className="text-gray-600 mb-6">{book.description}</p>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <strong className="font-semibold">Publication Year:</strong>{" "}
              {book.publicationYear}
            </div>
            <div>
              <strong className="font-semibold">ISBN:</strong> {book.isbn}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default BookDetailPage;

