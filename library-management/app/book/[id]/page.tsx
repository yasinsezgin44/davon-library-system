"use client"

import { useState } from "react"
import { useParams, useRouter } from "next/navigation"
import { LibrarySidebar } from "@/components/library-sidebar"
import { LibraryHeader } from "@/components/library-header"
import { LibraryButton } from "@/components/library-button"
import { BookDetailTabs } from "@/components/book-detail-tabs"
import { mockBooks } from "@/data/mock-books"
import { Star, Heart, Share2, ArrowLeft, Calendar, Clock } from "lucide-react"
import { cn } from "@/lib/utils"

export default function BookDetailPage() {
  const params = useParams()
  const router = useRouter()
  const [isLiked, setIsLiked] = useState(false)
  const [imageLoaded, setImageLoaded] = useState(false)

  // Find the book by ID (in a real app, this would be an API call)
  const book = mockBooks.find((b) => b.id === params.id) || mockBooks[0]

  // Get related books (same genre, excluding current book)
  const relatedBooks = mockBooks.filter((b) => b.genre === book.genre && b.id !== book.id).slice(0, 4)

  const renderStars = (rating: number) => {
    return Array.from({ length: 5 }, (_, i) => (
      <Star key={i} className={cn("h-5 w-5", i < rating ? "fill-yellow-400 text-yellow-400" : "text-gray-300")} />
    ))
  }

  return (
    <div className="min-h-screen bg-clean-white">
      <LibraryHeader />
      <LibrarySidebar />

      <main className="lg:ml-64 p-4 lg:p-8">
        <div className="max-w-7xl mx-auto">
          {/* Back Navigation */}
          <div className="mb-8 pt-32 lg:pt-16">
            <button
              onClick={() => router.back()}
              className="flex items-center space-x-2 text-dark-gray/70 hover:text-dark-gray transition-colors mb-6"
            >
              <ArrowLeft className="h-4 w-4" />
              <span>Back to Catalog</span>
            </button>
          </div>

          {/* Main Content - Two Column Layout */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 mb-12">
            {/* Left Column - Book Cover */}
            <div className="flex justify-center lg:justify-start">
              <div className="relative group">
                <div
                  className={cn(
                    "w-80 h-[480px] rounded-2xl overflow-hidden shadow-2xl transition-all duration-500",
                    "bg-gradient-to-br from-gray-200 to-gray-300",
                    imageLoaded && "shadow-3xl",
                  )}
                >
                  <div
                    className={cn(
                      "absolute inset-0 bg-gradient-to-br from-gray-200 to-gray-300 animate-pulse",
                      imageLoaded && "hidden",
                    )}
                  />
                  <img
                    src={book.cover || "/placeholder.svg?height=480&width=320"}
                    alt={book.title}
                    className={cn(
                      "w-full h-full object-cover transition-all duration-700",
                      "group-hover:scale-105",
                      !imageLoaded && "opacity-0",
                    )}
                    onLoad={() => setImageLoaded(true)}
                  />
                </div>

                {/* Floating Action Buttons */}
                <div className="absolute top-4 right-4 space-y-2 opacity-0 group-hover:opacity-100 transition-opacity duration-300">
                  <button
                    onClick={() => setIsLiked(!isLiked)}
                    className="p-3 bg-white/90 backdrop-blur-sm rounded-full shadow-lg hover:bg-white transition-colors"
                  >
                    <Heart className={cn("h-5 w-5", isLiked ? "fill-red-500 text-red-500" : "text-dark-gray")} />
                  </button>
                  <button className="p-3 bg-white/90 backdrop-blur-sm rounded-full shadow-lg hover:bg-white transition-colors">
                    <Share2 className="h-5 w-5 text-dark-gray" />
                  </button>
                </div>
              </div>
            </div>

            {/* Right Column - Book Information */}
            <div className="space-y-6">
              {/* Title and Author */}
              <div>
                <h1 className="text-4xl font-bold text-dark-gray mb-3 leading-tight">{book.title}</h1>
                <p className="text-xl text-dark-gray/70 mb-4">by {book.author}</p>

                {/* Genre Badge */}
                <span className="inline-block px-3 py-1 bg-modern-teal/10 text-modern-teal text-sm font-medium rounded-full">
                  {book.genre}
                </span>
              </div>

              {/* Rating */}
              <div className="flex items-center space-x-3">
                <div className="flex items-center space-x-1">{renderStars(Math.floor(book.rating))}</div>
                <span className="text-lg font-semibold text-dark-gray">{book.rating}</span>
                <span className="text-dark-gray/70">(127 reviews)</span>
              </div>

              {/* Price */}
              <div className="text-3xl font-bold text-dark-gray">${book.price}</div>

              {/* Description */}
              <div>
                <h3 className="text-lg font-semibold text-dark-gray mb-3">Description</h3>
                <p className="text-dark-gray/80 leading-relaxed">
                  {book.description ||
                    "A captivating story that will take you on an unforgettable journey through the depths of human emotion and experience. This masterfully crafted narrative explores themes of love, loss, and redemption with beautiful prose and compelling characters that will stay with you long after you turn the final page."}
                </p>
              </div>

              {/* Availability Status */}
              <div className="flex items-center space-x-4 p-4 bg-gray-50 rounded-lg">
                <div className="flex items-center space-x-2">
                  <div className={cn("w-3 h-3 rounded-full", book.isAvailable ? "bg-green-500" : "bg-red-500")} />
                  <span className="font-medium text-dark-gray">
                    {book.isAvailable ? "Available Now" : "Currently On Loan"}
                  </span>
                </div>
                {!book.isAvailable && (
                  <div className="flex items-center space-x-1 text-dark-gray/70">
                    <Clock className="h-4 w-4" />
                    <span className="text-sm">Expected return: Jan 25, 2024</span>
                  </div>
                )}
              </div>

              {/* Action Buttons */}
              <div className="space-y-3">
                <LibraryButton className="w-full py-4 text-lg" disabled={!book.isAvailable}>
                  {book.isAvailable ? "Borrow Book" : "Join Waitlist"}
                </LibraryButton>

                <div className="grid grid-cols-2 gap-3">
                  <LibraryButton variant="outline" className="py-3">
                    Add to Wishlist
                  </LibraryButton>
                  <LibraryButton variant="outline" className="py-3">
                    Reserve Copy
                  </LibraryButton>
                </div>
              </div>

              {/* Quick Info */}
              <div className="grid grid-cols-2 gap-4 pt-4 border-t border-gray-200">
                <div className="flex items-center space-x-2 text-dark-gray/70">
                  <Calendar className="h-4 w-4" />
                  <span className="text-sm">Published 2023</span>
                </div>
                <div className="flex items-center space-x-2 text-dark-gray/70">
                  <span className="text-sm">342 pages</span>
                </div>
              </div>
            </div>
          </div>

          {/* Tabbed Interface */}
          <BookDetailTabs book={book} relatedBooks={relatedBooks} />
        </div>
      </main>
    </div>
  )
}
