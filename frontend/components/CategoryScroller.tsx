// Mock data for categories - replace with API call
const categories = [
  "Fiction",
  "Science",
  "Fantasy",
  "History",
  "Mystery",
  "Romance",
  "Thriller",
  "Biography",
  "Poetry",
  "Non-Fiction",
];

const CategoryScroller = () => {
  return (
    <div className="py-4">
      <h2 className="text-2xl font-bold mb-4">Browse by Category</h2>
      <div className="flex space-x-4 overflow-x-auto pb-4">
        {categories.map((category, index) => (
          <div key={index} className="flex-shrink-0">
            <a
              href="#"
              className="block bg-gray-200 hover:bg-gray-300 rounded-full px-4 py-2 font-semibold"
            >
              {category}
            </a>
          </div>
        ))}
      </div>
    </div>
  );
};

export default CategoryScroller;
