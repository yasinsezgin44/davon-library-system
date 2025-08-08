import Image from "next/image";

type BookCardProps = {
  title: string;
  author: string;
  imageUrl: string;
};

const BookCard = ({ title, author, imageUrl }: BookCardProps) => {
  return (
    <div className="border rounded-lg overflow-hidden shadow-lg hover:shadow-xl transition-shadow duration-300">
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
        <button className="mt-4 w-full bg-blue-500 text-white py-2 rounded hover:bg-blue-600 transition-colors duration-300">
          View Details
        </button>
      </div>
    </div>
  );
};

export default BookCard;
