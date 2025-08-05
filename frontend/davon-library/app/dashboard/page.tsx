import { LibrarySidebar } from "@/components/library-sidebar"
import { LibraryHeader } from "@/components/library-header"
import { CurrentlyBorrowedCard } from "@/components/currently-borrowed-card"
import { ReservationsCard } from "@/components/reservations-card"
import { ReadingProgressCard } from "@/components/reading-progress-card"
import { QuickStatsCard } from "@/components/quick-stats-card"
import { LibraryButton } from "@/components/library-button"
import { BookOpen, Search, Heart } from "lucide-react"

export default function UserDashboard() {
  return (
    <div className="min-h-screen bg-clean-white">
      <LibraryHeader />
      <LibrarySidebar />

      <main className="lg:ml-64 p-4 lg:p-8">
        <div className="max-w-7xl mx-auto">
          {/* Welcome Header */}
          <div className="mb-8 pt-32 lg:pt-16">
            <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
              <div>
                <h1 className="text-3xl font-bold text-dark-gray mb-2">Welcome back, John!</h1>
                <p className="text-dark-gray/70">Here's what's happening with your reading journey</p>
              </div>

              {/* Quick Actions */}
              <div className="flex items-center gap-3">
                <LibraryButton variant="outline" size="sm">
                  <Search className="h-4 w-4 mr-2" />
                  Browse Books
                </LibraryButton>
                <LibraryButton size="sm">
                  <BookOpen className="h-4 w-4 mr-2" />
                  Continue Reading
                </LibraryButton>
              </div>
            </div>
          </div>

          {/* Dashboard Grid */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">
            {/* Left Column - Main Cards */}
            <div className="lg:col-span-2 space-y-6">
              <CurrentlyBorrowedCard />
              <ReadingProgressCard />
            </div>

            {/* Right Column - Side Cards */}
            <div className="space-y-6">
              <ReservationsCard />
              <QuickStatsCard />
            </div>
          </div>

          {/* Recent Activity */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Recent Reviews */}
            <div className="bg-white/60 backdrop-blur-sm border border-white/20 shadow-lg rounded-2xl p-6">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-semibold text-dark-gray">Recent Reviews</h3>
                <LibraryButton variant="outline" size="sm">
                  View All
                </LibraryButton>
              </div>

              <div className="space-y-3">
                {[
                  { title: "The Midnight Library", rating: 5, date: "2 days ago" },
                  { title: "Atomic Habits", rating: 4, date: "1 week ago" },
                  { title: "Dune", rating: 5, date: "2 weeks ago" },
                ].map((review, index) => (
                  <div key={index} className="flex items-center justify-between p-3 bg-gray-50/50 rounded-lg">
                    <div>
                      <div className="font-medium text-dark-gray text-sm">{review.title}</div>
                      <div className="flex items-center space-x-1 mt-1">
                        {Array.from({ length: 5 }, (_, i) => (
                          <div
                            key={i}
                            className={`w-3 h-3 rounded-full ${i < review.rating ? "bg-yellow-400" : "bg-gray-200"}`}
                          />
                        ))}
                      </div>
                    </div>
                    <span className="text-xs text-dark-gray/60">{review.date}</span>
                  </div>
                ))}
              </div>
            </div>

            {/* Wishlist */}
            <div className="bg-white/60 backdrop-blur-sm border border-white/20 shadow-lg rounded-2xl p-6">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-semibold text-dark-gray">Wishlist</h3>
                <LibraryButton variant="outline" size="sm">
                  <Heart className="h-4 w-4 mr-1" />
                  View All
                </LibraryButton>
              </div>

              <div className="space-y-3">
                {[
                  { title: "The Seven Husbands of Evelyn Hugo", author: "Taylor Jenkins Reid", available: true },
                  { title: "Project Hail Mary", author: "Andy Weir", available: false },
                  { title: "Klara and the Sun", author: "Kazuo Ishiguro", available: true },
                ].map((book, index) => (
                  <div key={index} className="flex items-center justify-between p-3 bg-gray-50/50 rounded-lg">
                    <div>
                      <div className="font-medium text-dark-gray text-sm">{book.title}</div>
                      <div className="text-xs text-dark-gray/70">{book.author}</div>
                    </div>
                    <div className={`w-2 h-2 rounded-full ${book.available ? "bg-green-500" : "bg-red-500"}`} />
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  )
}
