import Image from "next/image";
import Link from "next/link";
import { useAuth } from "@/context/AuthContext";
import apiClient from "@/lib/apiClient";
import toast from "react-hot-toast";
import { useState, useEffect } from "react";

type BookCardProps = {
  id: number;
  title: string;
  author: string;
  imageUrl: string;
  isAvailable: boolean;
};

const BookCard = ({
  id,
  title,
  author,
  imageUrl,
  isAvailable,
}: BookCardProps) => {
  const { user } = useAuth();
  const [available, setAvailable] = useState(isAvailable);
  const placeholderImage = "/images/default_book_image.jpeg";

  useEffect(() => {
    setAvailable(isAvailable);
  }, [isAvailable]);

  let imageSrc = placeholderImage;
  if (imageUrl && imageUrl.trim() !== "") {
    if (imageUrl.startsWith("http")) {
      imageSrc = imageUrl;
    } else {
      imageSrc = `http://localhost:8083${imageUrl}`;
    }
  }

  const borrowBook = async () => {
    if (!user) {
      toast.error("You must be logged in to borrow a book.");
      return;
    }

    try {
      await apiClient.post(`/loans/borrow?bookId=${id}`);
      toast.success("Book borrowed successfully!");
      setAvailable(false);
    } catch (error) {
      toast.error("Failed to borrow book. It may be unavailable.");
      console.error("Failed to borrow book:", error);
    }
  };

  return (
    <div className="border rounded-lg overflow-hidden shadow-lg hover:shadow-xl transition-shadow duration-300 h-full flex flex-col">
      <Link href={`/books/${id}`} className="flex-grow">
        <div className="relative w-full h-64">
          <Image
            src={imageSrc}
            alt={`Cover of ${title}`}
            layout="fill"
            objectFit="cover"
          />
        </div>
        <div className="p-4">
          <h3 className="text-lg font-bold">{title}</h3>
          <p className="text-gray-400">{author}</p>
        </div>
      </Link>
      {user && (
        <div className="p-4 border-t">
          <button
            onClick={borrowBook}
            className={`w-full py-2 rounded-md transition-colors ${
              !available
                ? "bg-gray-400 text-gray-700 cursor-not-allowed"
                : "bg-blue-500 text-white hover:bg-blue-600"
            }`}
            disabled={!available}
          >
            {available ? "Borrow" : "Borrowed"}
          </button>
        </div>
      )}
    </div>
  );
};

export default BookCard;
