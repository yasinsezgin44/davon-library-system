import Image from "next/image";
import Link from "next/link";

type BookCardProps = {
  id: number;
  title: string;
  author: string;
  imageUrl: string;
};

const BookCard = ({ id, title, author, imageUrl }: BookCardProps) => {
  const placeholderImage = "/images/default_book_image.jpeg";

  let imageSrc = placeholderImage;
  if (imageUrl && imageUrl.trim() !== "") {
    if (imageUrl.startsWith("http")) {
      imageSrc = imageUrl;
    } else {
      imageSrc = `http://localhost:8083${imageUrl}`;
    }
  }

  return (
    <Link href={`/books/${id}`}>
      <div className="border rounded-lg overflow-hidden shadow-lg hover:shadow-xl transition-shadow duration-300 cursor-pointer h-full flex flex-col">
        <div className="relative w-full h-64">
          <Image
            src={imageSrc}
            alt={`Cover of ${title}`}
            layout="fill"
            objectFit="cover"
          />
        </div>
        <div className="p-4 flex-grow">
          <h3 className="text-lg font-bold">{title}</h3>
          <p className="text-gray-400">{author}</p>
        </div>
      </div>
    </Link>
  );
};

export default BookCard;
