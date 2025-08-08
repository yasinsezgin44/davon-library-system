import Image from "next/image";
import Link from "next/link";

type BookCardProps = {
  id: number;
  title: string;
  author: string;
  imageUrl: string;
};

const BookCard = ({ id, title, author, imageUrl }: BookCardProps) => {
  return (
    <Link href={`/books/${id}`}>
      <div className="border rounded-lg overflow-hidden shadow-lg hover:shadow-xl transition-shadow duration-300 cursor-pointer">
        <div className="relative w-full h-64">
          <Image
            src={imageUrl}
            alt={`Cover of ${title}`}
            layout="fill"
            objectFit="cover"
          />
        </div>
        <div className="p-4">
          <h3 className="text-lg font-bold">{title}</h3>
          <p className="text-gray-600">{author}</p>
        </div>
      </div>
    </Link>
  );
};

export default BookCard;
