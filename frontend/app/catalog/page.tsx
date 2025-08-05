"use client"

import { useState } from "react"
import { LibrarySidebar } from "@/components/library-sidebar"
import { LibraryHeader } from "@/components/library-header"
import { FilterSidebar } from "@/components/filter-sidebar"
import { BookCard } from "@/components/book-card"
import { mockBooks } from "@/data/mock-books"
import { Filter, Grid, List, Search } from "lucide-react"
import { cn } from "@/lib/utils"

export default function CatalogPage() {
  const [isFilterOpen, setIsFilterOpen] = useState(false)
  const [viewMode, setViewMode] = useState<"grid" | "list">("grid")
  const [searchQuery, setSearchQuery] = useState("")

  const filteredBooks = mockBooks.filter(
    (book) =>
      book.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      book.author.toLowerCase().includes(searchQuery.toLowerCase()) ||
      book.genre.toLowerCase().includes(searchQuery.toLowerCase()),
  )

  return (
    <div className="min-h-screen bg-clean-white">
      <LibraryHeader />
      <LibrarySidebar />
      <FilterSidebar isOpen={isFilterOpen} onClose={() => setIsFilterOpen(false)} />

      <main className="lg:ml-64 p-4 lg:p-8">
        <div className="max-w-7xl mx-auto">
          {/* Page Header */}
          <div className="mb-8 pt-32 lg:pt-16">
            <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
              <div>
                <h1 className="text-3xl font-bold text-dark-gray mb-2">Book Catalog</h1>
                <p className="text-dark-gray/70">Discover your next favorite book from our collection</p>
              </div>

              {/* Controls */}
              <div className="flex items-center gap-4">
                {/* Search */}
                <div className="relative flex-1 lg:w-80">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-dark-gray/50" />
                  <input
                    type="text"
                    placeholder="Search books..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    className="w-full pl-10 pr-4 py-2 rounded-lg bg-white/60 backdrop-blur-sm border border-white/30 text-dark-gray placeholder-dark-gray/50 focus:outline-none focus:ring-2 focus:ring-modern-teal/50 focus:border-modern-teal transition-all duration-200"
                  />
                </div>

                {/* View Toggle */}
                <div className="flex items-center bg-white/60 backdrop-blur-sm border border-white/30 rounded-lg p-1">
                  <button
                    onClick={() => setViewMode("grid")}
                    className={cn(
                      "p-2 rounded-md transition-colors",
                      viewMode === "grid" ? "bg-modern-teal text-white" : "text-dark-gray hover:bg-white/50",
                    )}
                  >
                    <Grid className="h-4 w-4" />
                  </button>
                  <button
                    onClick={() => setViewMode("list")}
                    className={cn(
                      "p-2 rounded-md transition-colors",
                      viewMode === "list" ? "bg-modern-teal text-white" : "text-dark-gray hover:bg-white/50",
                    )}
                  >
                    <List className="h-4 w-4" />
                  </button>
                </div>

                {/* Filter Toggle */}
                <button
                  onClick={() => setIsFilterOpen(!isFilterOpen)}
                  className="flex items-center gap-2 px-4 py-2 bg-modern-teal text-white rounded-lg hover:bg-modern-teal/90 transition-colors"
                >
                  <Filter className="h-4 w-4" />
                  <span className="hidden sm:inline">Filters</span>
                </button>
              </div>
            </div>
          </div>

          {/* Results Info */}
          <div className="mb-6">
            <p className="text-dark-gray/70">
              Showing {filteredBooks.length} of {mockBooks.length} books
            </p>
          </div>

          {/* Book Grid */}
          <div
            className={cn(
              "transition-all duration-300",
              isFilterOpen && "lg:ml-80",
              viewMode === "grid"
                ? "grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6"
                : "space-y-4",
            )}
          >
            {filteredBooks.map((book) => (
              <BookCard key={book.id} book={book} />
            ))}
          </div>

          {/* Empty State */}
          {filteredBooks.length === 0 && (
            <div className="text-center py-16">
              <div className="w-24 h-24 mx-auto mb-4 bg-gray-100 rounded-full flex items-center justify-center">
                <Search className="h-8 w-8 text-gray-400" />
              </div>
              <h3 className="text-lg font-semibold text-dark-gray mb-2">No books found</h3>
              <p className="text-dark-gray/70">Try adjusting your search or filters to find what you're looking for.</p>
            </div>
          )}
        </div>
      </main>
    </div>
  )
}
