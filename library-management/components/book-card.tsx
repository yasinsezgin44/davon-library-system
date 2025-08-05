"use client"

import { useState } from "react"
import { Star, Heart, BookOpen } from "lucide-react"
import { cn } from "@/lib/utils"
import Link from "next/link"

interface Book {
  id: string
  title: string
  author: string
  cover: string
  genre: string
  rating: number
  price: number
  isAvailable: boolean
  description?: string
}

interface BookCardProps {
  book: Book
  className?: string
}

export function BookCard({ book, className }: BookCardProps) {
  const [isLiked, setIsLiked] = useState(false)
  const [imageLoaded, setImageLoaded] = useState(false)

  return (
    <Link href={`/book/${book.id}`} className="block">
      <div
        className={cn(
          "group relative overflow-hidden rounded-2xl transition-all duration-500 ease-out",
          "bg-white/60 backdrop-blur-sm border border-white/20 shadow-lg",
          "hover:bg-white/80 hover:shadow-2xl hover:border-white/30",
          "hover:transform hover:scale-105 hover:-translate-y-2",
          "cursor-pointer",
          className,
        )}
      >
        {/* Book Cover */}
        <div className="relative aspect-[3/4] overflow-hidden">
          <div
            className={cn(
              "absolute inset-0 bg-gradient-to-br from-gray-200 to-gray-300 animate-pulse",
              imageLoaded && "hidden",
            )}
          />
          <img
            src={book.cover || "/placeholder.svg"}
            alt={book.title}
            className={cn(
              "w-full h-full object-cover transition-all duration-700",
              "group-hover:scale-110",
              !imageLoaded && "opacity-0",
            )}
            onLoad={() => setImageLoaded(true)}
          />

          {/* Overlay gradient */}
          <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300" />

          {/* Status badge */}
          <div className="absolute top-3 left-3">
            <span
              className={cn(
                "inline-flex items-center px-2 py-1 rounded-full text-xs font-medium backdrop-blur-sm",
                book.isAvailable
                  ? "bg-green-500/20 text-green-700 border border-green-500/30"
                  : "bg-red-500/20 text-red-700 border border-red-500/30",
              )}
            >
              <div className={cn("w-1.5 h-1.5 rounded-full mr-1", book.isAvailable ? "bg-green-500" : "bg-red-500")} />
              {book.isAvailable ? "Available" : "On Loan"}
            </span>
          </div>

          {/* Like button */}
          <button
            onClick={(e) => {
              e.preventDefault()
              e.stopPropagation()
              setIsLiked(!isLiked)
            }}
            className="absolute top-3 right-3 p-2 rounded-full bg-white/20 backdrop-blur-sm border border-white/30 opacity-0 group-hover:opacity-100 transition-all duration-300 hover:bg-white/30"
          >
            <Heart className={cn("h-4 w-4 transition-colors", isLiked ? "fill-red-500 text-red-500" : "text-white")} />
          </button>

          {/* Quick action overlay */}
          <div className="absolute inset-x-0 bottom-0 p-4 transform translate-y-full group-hover:translate-y-0 transition-transform duration-300">
            <button
              onClick={(e) => {
                e.preventDefault()
                e.stopPropagation()
              }}
              className="w-full py-2 px-4 bg-white/90 backdrop-blur-sm rounded-lg text-dark-gray font-medium hover:bg-white transition-colors"
            >
              <BookOpen className="h-4 w-4 inline mr-2" />
              {book.isAvailable ? "Borrow Book" : "Join Waitlist"}
            </button>
          </div>
        </div>

        {/* Book Info */}
        <div className="p-4 space-y-3">
          {/* Title and Author */}
          <div>
            <h3 className="font-semibold text-dark-gray line-clamp-2 group-hover:text-modern-teal transition-colors">
              {book.title}
            </h3>
            <p className="text-sm text-dark-gray/70 mt-1">{book.author}</p>
          </div>

          {/* Genre */}
          <div>
            <span className="inline-block px-2 py-1 bg-modern-teal/10 text-modern-teal text-xs font-medium rounded-md">
              {book.genre}
            </span>
          </div>

          {/* Rating and Price */}
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-1">
              <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
              <span className="text-sm font-medium text-dark-gray">{book.rating}</span>
            </div>
            <div className="text-lg font-bold text-dark-gray">${book.price}</div>
          </div>
        </div>

        {/* Hover glow effect */}
        <div className="absolute inset-0 rounded-2xl opacity-0 group-hover:opacity-100 transition-opacity duration-500 pointer-events-none">
          <div className="absolute inset-0 rounded-2xl bg-gradient-to-r from-modern-teal/10 via-transparent to-warm-coral/10 blur-xl" />
        </div>
      </div>
    </Link>
  )
}
