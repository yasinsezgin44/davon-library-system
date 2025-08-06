// frontend/app/page.tsx
'use client';

import { useEffect, useState } from 'react';
import { LibraryCard } from "@/components/library-card";
import { LibraryButton } from "@/components/library-button";
import { LibrarySidebar } from "@/components/library-sidebar";
import { LibraryHeader } from "@/components/library-header";
import { LibraryFooter } from "@/components/library-footer";
import { HeroSection } from "@/components/hero-section";
import { getTrendingBooks, getGenres, getLibraryInfo } from '../lib/api';
import { Book } from '../types/book';
import { Category } from '../types/category';

export default function HomePage() {
  const [trendingBooks, setTrendingBooks] = useState<Book[]>([]);
  const [genres, setGenres] = useState<Category[]>([]);
  const [libraryInfo, setLibraryInfo] = useState<any>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [books, genres, info] = await Promise.all([
          getTrendingBooks(),
          getGenres(),
          getLibraryInfo(),
        ]);
        setTrendingBooks(books);
        setGenres(genres);
        setLibraryInfo(info);
      } catch (error) {
        console.error('Failed to fetch homepage data:', error);
      }
    };
    fetchData();
  }, []);

  return (
    <div className="min-h-screen bg-clean-white">
      <LibraryHeader />
      <LibrarySidebar />

      <main className="lg:ml-64 p-4 lg:p-8">
        <div className="max-w-7xl mx-auto">
          <HeroSection />

          <div className="my-8">
            <h2 className="text-3xl font-bold text-dark-gray mb-4">Trending Books</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
              {trendingBooks.map((book) => (
                <LibraryCard key={book.id}>
                  <h3 className="text-xl font-bold text-dark-gray">{book.title}</h3>
                  <p className="text-dark-gray/70">{book.authors?.map(a => a.name).join(', ')}</p>
                </LibraryCard>
              ))}
            </div>
          </div>

          <div className="my-8">
            <h2 className="text-3xl font-bold text-dark-gray mb-4">Browse by Genre</h2>
            <div className="flex flex-wrap gap-4">
              {genres.map((genre) => (
                <LibraryButton key={genre.id} variant="outline">
                  {genre.name}
                </LibraryButton>
              ))}
            </div>
          </div>
        </div>
      </main>

      <LibraryFooter info={libraryInfo} />
    </div>
  );
}
