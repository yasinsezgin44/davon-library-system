"use client"

import { TrendingUp, Target } from "lucide-react"
import { LibraryCard } from "./library-card"
import { cn } from "@/lib/utils"

interface MonthlyProgress {
  month: string
  booksRead: number
  goal: number
}

const mockReadingProgress: MonthlyProgress[] = [
  { month: "Aug", booksRead: 4, goal: 5 },
  { month: "Sep", booksRead: 6, goal: 5 },
  { month: "Oct", booksRead: 3, goal: 5 },
  { month: "Nov", booksRead: 7, goal: 5 },
  { month: "Dec", booksRead: 5, goal: 5 },
  { month: "Jan", booksRead: 2, goal: 5 },
]

export function ReadingProgressCard() {
  const currentMonth = mockReadingProgress[mockReadingProgress.length - 1]
  const totalBooksThisYear = mockReadingProgress.reduce((sum, month) => sum + month.booksRead, 0)
  const averagePerMonth = Math.round((totalBooksThisYear / mockReadingProgress.length) * 10) / 10

  const maxBooks = Math.max(...mockReadingProgress.map((m) => Math.max(m.booksRead, m.goal)))

  return (
    <LibraryCard className="p-6">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-semibold text-dark-gray">Monthly Reading Progress</h2>
        <div className="flex items-center space-x-1 text-sm text-green-600">
          <TrendingUp className="h-4 w-4" />
          <span>On track</span>
        </div>
      </div>

      {/* Stats Row */}
      <div className="grid grid-cols-3 gap-4 mb-6">
        <div className="text-center">
          <div className="text-2xl font-bold text-dark-gray">{currentMonth.booksRead}</div>
          <div className="text-xs text-dark-gray/70">This Month</div>
        </div>
        <div className="text-center">
          <div className="text-2xl font-bold text-modern-teal">{totalBooksThisYear}</div>
          <div className="text-xs text-dark-gray/70">This Year</div>
        </div>
        <div className="text-center">
          <div className="text-2xl font-bold text-warm-coral">{averagePerMonth}</div>
          <div className="text-xs text-dark-gray/70">Avg/Month</div>
        </div>
      </div>

      {/* Bar Chart */}
      <div className="space-y-3">
        {mockReadingProgress.map((month, index) => {
          const progressPercentage = (month.booksRead / maxBooks) * 100
          const goalPercentage = (month.goal / maxBooks) * 100
          const isCurrentMonth = index === mockReadingProgress.length - 1

          return (
            <div key={month.month} className="space-y-1">
              <div className="flex items-center justify-between text-sm">
                <span className={cn("font-medium", isCurrentMonth ? "text-modern-teal" : "text-dark-gray/70")}>
                  {month.month}
                </span>
                <span className="text-dark-gray/60">
                  {month.booksRead}/{month.goal}
                </span>
              </div>
              <div className="relative">
                {/* Goal line */}
                <div className="h-6 bg-gray-100 rounded-full overflow-hidden">
                  <div className="h-full bg-gray-200 rounded-full" style={{ width: `${goalPercentage}%` }} />
                </div>
                {/* Progress bar */}
                <div className="absolute inset-0">
                  <div className="h-6 bg-gray-100 rounded-full overflow-hidden">
                    <div
                      className={cn(
                        "h-full rounded-full transition-all duration-500 ease-out",
                        month.booksRead >= month.goal
                          ? "bg-gradient-to-r from-green-400 to-green-500"
                          : isCurrentMonth
                            ? "bg-gradient-to-r from-modern-teal to-modern-teal/80"
                            : "bg-gradient-to-r from-warm-coral to-warm-coral/80",
                      )}
                      style={{ width: `${progressPercentage}%` }}
                    />
                  </div>
                </div>
                {/* Books read indicator */}
                <div className="absolute inset-y-0 left-2 flex items-center">
                  <span className="text-xs font-medium text-white drop-shadow-sm">
                    {month.booksRead > 0 && month.booksRead}
                  </span>
                </div>
              </div>
            </div>
          )
        })}
      </div>

      {/* Goal Progress */}
      <div className="mt-6 pt-4 border-t border-gray-200">
        <div className="flex items-center justify-between mb-2">
          <div className="flex items-center space-x-2">
            <Target className="h-4 w-4 text-modern-teal" />
            <span className="text-sm font-medium text-dark-gray">Monthly Goal Progress</span>
          </div>
          <span className="text-sm text-dark-gray/70">
            {currentMonth.booksRead}/{currentMonth.goal}
          </span>
        </div>
        <div className="w-full bg-gray-200 rounded-full h-2">
          <div
            className={cn(
              "h-2 rounded-full transition-all duration-500",
              currentMonth.booksRead >= currentMonth.goal
                ? "bg-gradient-to-r from-green-400 to-green-500"
                : "bg-gradient-to-r from-modern-teal to-modern-teal/80",
            )}
            style={{ width: `${Math.min((currentMonth.booksRead / currentMonth.goal) * 100, 100)}%` }}
          />
        </div>
        <div className="flex justify-between text-xs text-dark-gray/60 mt-1">
          <span>0</span>
          <span>{currentMonth.goal} books</span>
        </div>
      </div>
    </LibraryCard>
  )
}
