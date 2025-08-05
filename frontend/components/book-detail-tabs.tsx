"use client"

import { useState } from "react"
import { Star, User } from "lucide-react"
import { LibraryButton } from "./library-button"
import { LibraryCard } from "./library-card"
import { BookCard } from "./book-card"
import { cn } from "@/lib/utils"
import type { Book } from "@/data/mock-books"

interface BookDetailTabsProps {
  book: Book
  relatedBooks: Book[]
}

interface Review {
  id: string
  userName: string
  rating: number
  comment: string
  date: string
  avatar?: string
}

const mockReviews: Review[] = [
  {
    id: "1",
    userName: "Sarah Johnson",
    rating: 5,
    comment:
      "Absolutely loved this book! The character development was incredible and I couldn't put it down. Highly recommend to anyone looking for a thought-provoking read.",
    date: "2024-01-15",
  },
  {
    id: "2",
    userName: "Michael Chen",
    rating: 4,
    comment:
      "Great storytelling and beautiful prose. The ending was a bit rushed but overall a fantastic read that kept me engaged throughout.",
    date: "2024-01-10",
  },
  {
    id: "3",
    userName: "Emma Davis",
    rating: 5,
    comment:
      "This book changed my perspective on so many things. The author's writing style is captivating and the themes are deeply meaningful.",
    date: "2024-01-05",
  },
]

export function BookDetailTabs({ book, relatedBooks }: BookDetailTabsProps) {
  const [activeTab, setActiveTab] = useState("details")

  const tabs = [
    { id: "details", label: "Full Details" },
    { id: "reviews", label: "Reviews" },
    { id: "related", label: "Related Books" },
  ]

  const renderStars = (rating: number) => {
    return Array.from({ length: 5 }, (_, i) => (
      <Star key={i} className={cn("h-4 w-4", i < rating ? "fill-yellow-400 text-yellow-400" : "text-gray-300")} />
    ))
  }

  return (
    <div className="mt-12">
      {/* Tab Navigation */}
      <div className="border-b border-gray-200">
        <nav className="flex space-x-8">
          {tabs.map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={cn(
                "py-4 px-1 border-b-2 font-medium text-sm transition-colors",
                activeTab === tab.id
                  ? "border-modern-teal text-modern-teal"
                  : "border-transparent text-dark-gray/70 hover:text-dark-gray hover:border-gray-300",
              )}
            >
              {tab.label}
            </button>
          ))}
        </nav>
      </div>

      {/* Tab Content */}
      <div className="mt-8">
        {activeTab === "details" && (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            {/* Book Information */}
            <LibraryCard className="p-6">
              <h3 className="text-lg font-semibold text-dark-gray mb-4">Book Information</h3>
              <div className="space-y-4">
                <div className="flex justify-between">
                  <span className="text-dark-gray/70">ISBN:</span>
                  <span className="text-dark-gray font-medium">978-0-123456-78-9</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-dark-gray/70">Publisher:</span>
                  <span className="text-dark-gray font-medium">Penguin Random House</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-dark-gray/70">Publication Date:</span>
                  <span className="text-dark-gray font-medium">March 15, 2023</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-dark-gray/70">Pages:</span>
                  <span className="text-dark-gray font-medium">342</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-dark-gray/70">Language:</span>
                  <span className="text-dark-gray font-medium">English</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-dark-gray/70">Format:</span>
                  <span className="text-dark-gray font-medium">Hardcover, Paperback, E-book</span>
                </div>
              </div>
            </LibraryCard>

            {/* Availability */}
            <LibraryCard className="p-6">
              <h3 className="text-lg font-semibold text-dark-gray mb-4">Availability</h3>
              <div className="space-y-4">
                <div className="flex items-center justify-between">
                  <span className="text-dark-gray/70">Total Copies:</span>
                  <span className="text-dark-gray font-medium">5</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-dark-gray/70">Available:</span>
                  <span className="text-green-600 font-medium">3</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-dark-gray/70">On Loan:</span>
                  <span className="text-red-600 font-medium">2</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-dark-gray/70">Location:</span>
                  <span className="text-dark-gray font-medium">Section A, Shelf 12</span>
                </div>
                {!book.isAvailable && (
                  <div className="mt-4 p-3 bg-warm-coral/10 border border-warm-coral/20 rounded-lg">
                    <p className="text-warm-coral text-sm font-medium">Expected return date: January 25, 2024</p>
                  </div>
                )}
              </div>
            </LibraryCard>
          </div>
        )}

        {activeTab === "reviews" && (
          <div className="space-y-6">
            {/* Reviews Summary */}
            <LibraryCard className="p-6">
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-lg font-semibold text-dark-gray">Reader Reviews</h3>
                <LibraryButton size="sm">Write a Review</LibraryButton>
              </div>
              <div className="flex items-center space-x-6">
                <div className="text-center">
                  <div className="text-3xl font-bold text-dark-gray">{book.rating}</div>
                  <div className="flex items-center justify-center mt-1">{renderStars(Math.floor(book.rating))}</div>
                  <div className="text-sm text-dark-gray/70 mt-1">Based on {mockReviews.length} reviews</div>
                </div>
                <div className="flex-1">
                  {[5, 4, 3, 2, 1].map((stars) => (
                    <div key={stars} className="flex items-center space-x-2 mb-1">
                      <span className="text-sm text-dark-gray/70 w-8">{stars}★</span>
                      <div className="flex-1 bg-gray-200 rounded-full h-2">
                        <div
                          className="bg-yellow-400 h-2 rounded-full"
                          style={{
                            width: `${(mockReviews.filter((r) => Math.floor(r.rating) === stars).length / mockReviews.length) * 100}%`,
                          }}
                        />
                      </div>
                      <span className="text-sm text-dark-gray/70 w-8">
                        {mockReviews.filter((r) => Math.floor(r.rating) === stars).length}
                      </span>
                    </div>
                  ))}
                </div>
              </div>
            </LibraryCard>

            {/* Individual Reviews */}
            <div className="space-y-4">
              {mockReviews.map((review) => (
                <LibraryCard key={review.id} className="p-6">
                  <div className="flex items-start space-x-4">
                    <div className="w-10 h-10 bg-gradient-to-br from-modern-teal to-scholarly-navy rounded-full flex items-center justify-center">
                      <User className="h-5 w-5 text-white" />
                    </div>
                    <div className="flex-1">
                      <div className="flex items-center justify-between mb-2">
                        <div>
                          <h4 className="font-medium text-dark-gray">{review.userName}</h4>
                          <div className="flex items-center space-x-2 mt-1">
                            <div className="flex items-center">{renderStars(review.rating)}</div>
                            <span className="text-sm text-dark-gray/70">
                              {new Date(review.date).toLocaleDateString()}
                            </span>
                          </div>
                        </div>
                      </div>
                      <p className="text-dark-gray/80 leading-relaxed">{review.comment}</p>
                    </div>
                  </div>
                </LibraryCard>
              ))}
            </div>
          </div>
        )}

        {activeTab === "related" && (
          <div>
            <div className="mb-6">
              <h3 className="text-lg font-semibold text-dark-gray mb-2">Books You Might Like</h3>
              <p className="text-dark-gray/70">Based on genre and reader preferences</p>
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
              {relatedBooks.map((relatedBook) => (
                <BookCard key={relatedBook.id} book={relatedBook} />
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
