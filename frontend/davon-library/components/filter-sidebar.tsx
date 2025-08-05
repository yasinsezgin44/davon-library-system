"use client"

import { useState } from "react"
import { Slider } from "@/components/ui/slider"
import { Checkbox } from "@/components/ui/checkbox"
import { LibraryButton } from "./library-button"
import { Filter, X } from "lucide-react"
import { cn } from "@/lib/utils"

const genres = [
  "Fiction",
  "Non-Fiction",
  "Mystery",
  "Romance",
  "Science Fiction",
  "Fantasy",
  "Biography",
  "History",
  "Self-Help",
  "Technology",
  "Art & Design",
  "Children's Books",
]

interface FilterSidebarProps {
  isOpen: boolean
  onClose: () => void
}

export function FilterSidebar({ isOpen, onClose }: FilterSidebarProps) {
  const [selectedGenres, setSelectedGenres] = useState<string[]>([])
  const [priceRange, setPriceRange] = useState([0, 100])
  const [ratingRange, setRatingRange] = useState([0, 5])

  const handleGenreChange = (genre: string, checked: boolean) => {
    if (checked) {
      setSelectedGenres([...selectedGenres, genre])
    } else {
      setSelectedGenres(selectedGenres.filter((g) => g !== genre))
    }
  }

  const clearFilters = () => {
    setSelectedGenres([])
    setPriceRange([0, 100])
    setRatingRange([0, 5])
  }

  return (
    <>
      {/* Mobile overlay */}
      {isOpen && <div className="lg:hidden fixed inset-0 bg-black/50 z-40" onClick={onClose} />}

      {/* Sidebar */}
      <aside
        className={cn(
          "fixed left-0 top-16 z-40 h-[calc(100vh-4rem)] w-80 transform transition-transform duration-300 ease-in-out lg:translate-x-0 lg:ml-64",
          "bg-white/80 backdrop-blur-md border-r border-white/20 shadow-lg",
          isOpen ? "translate-x-0" : "-translate-x-full",
        )}
      >
        <div className="flex flex-col h-full">
          {/* Header */}
          <div className="flex items-center justify-between p-6 border-b border-gray-200/50">
            <div className="flex items-center space-x-2">
              <Filter className="h-5 w-5 text-dark-gray" />
              <h2 className="text-lg font-semibold text-dark-gray">Filters</h2>
            </div>
            <button onClick={onClose} className="lg:hidden p-2 rounded-lg hover:bg-gray-100/50 transition-colors">
              <X className="h-5 w-5 text-dark-gray" />
            </button>
          </div>

          {/* Scrollable content */}
          <div className="flex-1 overflow-y-auto p-6 space-y-8">
            {/* Genres */}
            <div>
              <h3 className="text-sm font-semibold text-dark-gray mb-4 uppercase tracking-wide">Genres</h3>
              <div className="space-y-3 max-h-64 overflow-y-auto">
                {genres.map((genre) => (
                  <div key={genre} className="flex items-center space-x-3">
                    <Checkbox
                      id={genre}
                      checked={selectedGenres.includes(genre)}
                      onCheckedChange={(checked) => handleGenreChange(genre, checked as boolean)}
                      className="data-[state=checked]:bg-modern-teal data-[state=checked]:border-modern-teal"
                    />
                    <label
                      htmlFor={genre}
                      className="text-sm text-dark-gray cursor-pointer hover:text-modern-teal transition-colors"
                    >
                      {genre}
                    </label>
                  </div>
                ))}
              </div>
            </div>

            {/* Price Range */}
            <div>
              <h3 className="text-sm font-semibold text-dark-gray mb-4 uppercase tracking-wide">Price Range</h3>
              <div className="space-y-4">
                <Slider
                  value={priceRange}
                  onValueChange={setPriceRange}
                  max={100}
                  min={0}
                  step={5}
                  className="w-full"
                />
                <div className="flex justify-between text-sm text-dark-gray/70">
                  <span>${priceRange[0]}</span>
                  <span>${priceRange[1]}</span>
                </div>
              </div>
            </div>

            {/* Rating Range */}
            <div>
              <h3 className="text-sm font-semibold text-dark-gray mb-4 uppercase tracking-wide">Rating</h3>
              <div className="space-y-4">
                <Slider
                  value={ratingRange}
                  onValueChange={setRatingRange}
                  max={5}
                  min={0}
                  step={0.5}
                  className="w-full"
                />
                <div className="flex justify-between text-sm text-dark-gray/70">
                  <span>{ratingRange[0]} ⭐</span>
                  <span>{ratingRange[1]} ⭐</span>
                </div>
              </div>
            </div>

            {/* Active Filters */}
            {selectedGenres.length > 0 && (
              <div>
                <h3 className="text-sm font-semibold text-dark-gray mb-3 uppercase tracking-wide">Active Filters</h3>
                <div className="flex flex-wrap gap-2">
                  {selectedGenres.map((genre) => (
                    <span
                      key={genre}
                      className="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-modern-teal/10 text-modern-teal border border-modern-teal/20"
                    >
                      {genre}
                      <button
                        onClick={() => handleGenreChange(genre, false)}
                        className="ml-2 hover:text-modern-teal/70"
                      >
                        <X className="h-3 w-3" />
                      </button>
                    </span>
                  ))}
                </div>
              </div>
            )}
          </div>

          {/* Footer */}
          <div className="p-6 border-t border-gray-200/50 space-y-3">
            <LibraryButton className="w-full">Apply Filters</LibraryButton>
            <LibraryButton variant="outline" className="w-full" onClick={clearFilters}>
              Clear All
            </LibraryButton>
          </div>
        </div>
      </aside>
    </>
  )
}
