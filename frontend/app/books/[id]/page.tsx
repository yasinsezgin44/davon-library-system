"use client";

import { useState, useEffect } from "react";
import { useParams } from "next/navigation";
import apiClient from "../../../lib/apiClient";
import Image from "next/image";
import { useAuth } from "../../../context/AuthContext";
import { FaSpinner } from "react-icons/fa";

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
  const [loading, setLoading] = useState(true);
  const params = useParams();
  const { id } = params;
  const { isAuthReady } = useAuth();

  useEffect(() => {
    const fetchBook = async () => {
      if (id && isAuthReady) {
        setLoading(true);
        try {
          const response = await apiClient.get(`/books/${id}`);
          setBook(response.data);
        } catch (error) {
          console.error("Failed to fetch book:", error);
        } finally {
          setLoading(false);
        }
      }
    };
    fetchBook();
  }, [id, isAuthReady]);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <FaSpinner className="animate-spin text-4xl text-gray-500" />
      </div>
    );
  }

  if (!book) {
    return <p>Book not found.</p>;
  }

  const placeholderImage = "/images/default_book_image.jpeg";

  let imageSrc = placeholderImage;
  if (book.coverImageUrl && book.coverImageUrl.trim() !== "") {
    if (book.coverImageUrl.startsWith("http")) {
      imageSrc = book.coverImageUrl;
    } else {
      imageSrc = `http://localhost:8083${book.coverImageUrl}`;
    }
  }

  return (
    <div className="container mx-auto py-10">
      <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
        <div className="md:col-span-1">
          <Image
            src={imageSrc}
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
