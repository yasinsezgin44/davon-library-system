import { LibraryCard } from "@/components/library-card";
import { LibraryButton } from "@/components/library-button";
import { LibrarySidebar } from "@/components/library-sidebar";
import { LibraryHeader } from "@/components/library-header";
import { fetchApi } from "@/lib/api";
import Link from "next/link";

interface Book {
  id: number;
  title: string;
  author: string;
  isbn: string;
  published: string;
}

export default async function BooksPage() {
  const books: Book[] = await fetchApi("/books");

  return (
    <div className="min-h-screen bg-clean-white">
      <LibraryHeader />
      <LibrarySidebar />

      <main className="lg:ml-64 p-4 lg:p-8">
        <div className="max-w-7xl mx-auto">
          <div className="mb-8 pt-32 lg:pt-16">
            <h1 className="text-3xl font-bold text-dark-gray mb-2">Books</h1>
            <p className="text-dark-gray/70">
              Manage your library's book collection
            </p>
          </div>

          <LibraryCard className="p-8">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-semibold text-dark-gray">
                Book Management
              </h2>
              <Link href="/books/new">
                <LibraryButton>Add New Book</LibraryButton>
              </Link>
            </div>
            <ul className="space-y-4">
              {books.map((book) => (
                <li
                  key={book.id}
                  className="p-4 border rounded-lg hover:bg-gray-50"
                >
                  <Link href={`/book/${book.id}`}>
                    <h3 className="text-lg font-semibold">{book.title}</h3>
                    <p className="text-sm text-gray-600">by {book.author}</p>
                    <p className="text-xs text-gray-500 mt-1">
                      ISBN: {book.isbn}
                    </p>
                  </Link>
                </li>
              ))}
            </ul>
          </LibraryCard>
        </div>
      </main>
    </div>
  );
}
