// Mock data for reading history - replace with API call
const readingHistory = [
  { id: 1, title: 'The Catcher in the Rye', author: 'J.D. Salinger', returnedDate: '2024-07-10' },
  { id: 2, title: 'The Great Gatsby', author: 'F. Scott Fitzgerald', returnedDate: '2024-06-25' },
  { id: 3, title: 'Moby Dick', author: 'Herman Melville', returnedDate: '2024-05-18' },
];

const ReadingHistory = () => {
  return (
    <div className="mt-8">
      <h2 className="text-2xl font-bold mb-4">Reading History</h2>
      <ul className="space-y-4">
        {readingHistory.map(book => (
          <li key={book.id} className="p-4 border rounded-lg shadow-sm flex justify-between items-center">
            <div>
              <h3 className="font-bold">{book.title}</h3>
              <p className="text-sm text-gray-600">{book.author}</p>
            </div>
            <p className="text-sm">
              <strong>Returned on:</strong> {book.returnedDate}
            </p>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ReadingHistory;

