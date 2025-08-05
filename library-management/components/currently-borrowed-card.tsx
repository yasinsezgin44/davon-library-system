"use client"

import { Calendar, Clock, AlertTriangle } from "lucide-react"
import { LibraryCard } from "./library-card"
import { LibraryButton } from "./library-button"
import { cn } from "@/lib/utils"

interface BorrowedBook {
  id: string
  title: string
  author: string
  cover: string
  dueDate: string
  daysLeft: number
  isOverdue: boolean
}

const mockBorrowedBooks: BorrowedBook[] = [
  {
    id: "1",
    title: "The Midnight Library",
    author: "Matt Haig",
    cover: "/placeholder.svg?height=120&width=80",
    dueDate: "2024-01-25",
    daysLeft: 3,
    isOverdue: false,
  },
  {
    id: "2",
    title: "Atomic Habits",
    author: "James Clear",
    cover: "/placeholder.svg?height=120&width=80",
    dueDate: "2024-01-20",
    daysLeft: -2,
    isOverdue: true,
  },
  {
    id: "3",
    title: "Dune",
    author: "Frank Herbert",
    cover: "/placeholder.svg?height=120&width=80",
    dueDate: "2024-01-30",
    daysLeft: 8,
    isOverdue: false,
  },
]

export function CurrentlyBorrowedCard() {
  const formatDueDate = (dateString: string) => {
    const date = new Date(dateString)
    return date.toLocaleDateString("en-US", { month: "short", day: "numeric" })
  }

  const getDueDateStatus = (daysLeft: number, isOverdue: boolean) => {
    if (isOverdue) {
      return {
        text: `${Math.abs(daysLeft)} days overdue`,
        color: "text-red-600",
        bgColor: "bg-red-50",
        borderColor: "border-red-200",
        icon: AlertTriangle,
      }
    } else if (daysLeft <= 3) {
      return {
        text: `${daysLeft} days left`,
        color: "text-warm-coral",
        bgColor: "bg-warm-coral/10",
        borderColor: "border-warm-coral/20",
        icon: Clock,
      }
    } else {
      return {
        text: `${daysLeft} days left`,
        color: "text-green-600",
        bgColor: "bg-green-50",
        borderColor: "border-green-200",
        icon: Calendar,
      }
    }
  }

  return (
    <LibraryCard className="p-6">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-semibold text-dark-gray">Currently Borrowed</h2>
        <span className="text-sm text-dark-gray/70">{mockBorrowedBooks.length} books</span>
      </div>

      <div className="space-y-4">
        {mockBorrowedBooks.map((book) => {
          const status = getDueDateStatus(book.daysLeft, book.isOverdue)
          const StatusIcon = status.icon

          return (
            <div
              key={book.id}
              className="flex items-center space-x-4 p-4 bg-gray-50/50 rounded-lg hover:bg-gray-50 transition-colors"
            >
              {/* Book Cover */}
              <div className="flex-shrink-0">
                <img
                  src={book.cover || "/placeholder.svg"}
                  alt={book.title}
                  className="w-12 h-16 object-cover rounded-md shadow-sm"
                />
              </div>

              {/* Book Info */}
              <div className="flex-1 min-w-0">
                <h3 className="font-medium text-dark-gray truncate">{book.title}</h3>
                <p className="text-sm text-dark-gray/70 truncate">{book.author}</p>
                <div className="flex items-center mt-2">
                  <span className="text-xs text-dark-gray/60">Due: {formatDueDate(book.dueDate)}</span>
                </div>
              </div>

              {/* Status Badge */}
              <div
                className={cn(
                  "flex items-center space-x-1 px-2 py-1 rounded-full text-xs font-medium",
                  status.bgColor,
                  status.borderColor,
                  "border",
                )}
              >
                <StatusIcon className={cn("h-3 w-3", status.color)} />
                <span className={status.color}>{status.text}</span>
              </div>
            </div>
          )
        })}
      </div>

      {/* Action Buttons */}
      <div className="mt-6 pt-4 border-t border-gray-200 flex space-x-3">
        <LibraryButton variant="outline" size="sm" className="flex-1">
          Renew All
        </LibraryButton>
        <LibraryButton size="sm" className="flex-1">
          View History
        </LibraryButton>
      </div>
    </LibraryCard>
  )
}
