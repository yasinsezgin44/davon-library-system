import Image from "next/image";
import Link from "next/link";
import { useAuth } from "@/context/AuthContext";
import apiClient from "@/lib/apiClient";
import toast from "react-hot-toast";

type BookCardProps = {
  id: number;
  title: string;
  author: string;
  imageUrl: string;
};

const BookCard = ({ id, title, author, imageUrl }: BookCardProps) => {
  const { user } = useAuth();
  const placeholderImage = "/images/default_book_image.jpeg";

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
      await apiClient.post(
        `/librarian/checkout?bookId=${id}&memberId=${user.id}`
      );
      toast.success("Book borrowed successfully!");
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
            className="w-full bg-blue-500 text-white py-2 rounded-md hover:bg-blue-600 transition-colors"
          >
            Borrow
          </button>
        </div>
      )}
    </div>
  );
};

export default BookCard;
