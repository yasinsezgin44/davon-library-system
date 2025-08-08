import BookCard from './BookCard';

// Mock data for trending books - replace with API call
const trendingBooks = [
  { id: 1, title: 'The Midnight Library', author: 'Matt Haig', imageUrl: '/images/book1.jpg' },
  { id: 2, title: 'Project Hail Mary', author: 'Andy Weir', imageUrl: '/images/book2.jpg' },
  { id: 3, title: 'The Four Winds', author: 'Kristin Hannah', imageUrl: '/images/book3.jpg' },
  { id: 4, title: 'Klara and the Sun', author: 'Kazuo Ishiguro', imageUrl: '/images/book4.jpg' },
  { id: 5, title: 'The Vanishing Half', author: 'Brit Bennett', imageUrl: '/images/book5.jpg' },
  { id: 6, title: 'Anxious People', author: 'Fredrik Backman', imageUrl: '/images/book6.jpg' },
];

const TrendingBooks = () => {
  return (
    <div className="py-8">
      <h2 className="text-2xl font-bold mb-6">Trending Books</h2>
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-6">
        {trendingBooks.map(book => (
          <BookCard key={book.id} title={book.title} author={book.author} imageUrl={book.imageUrl} />
        ))}
      </div>
    </div>
  );
};

export default TrendingBooks;

