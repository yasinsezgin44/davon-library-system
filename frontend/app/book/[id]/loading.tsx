import { LibrarySidebar } from "@/components/library-sidebar"
import { LibraryHeader } from "@/components/library-header"

export default function Loading() {
  return (
    <div className="min-h-screen bg-clean-white">
      <LibraryHeader />
      <LibrarySidebar />

      <main className="lg:ml-64 p-4 lg:p-8">
        <div className="max-w-7xl mx-auto">
          <div className="mb-8 pt-32 lg:pt-16">
            <div className="h-6 w-32 bg-gray-200 rounded animate-pulse mb-6" />
          </div>

          {/* Loading skeleton for two-column layout */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 mb-12">
            {/* Book cover skeleton */}
            <div className="flex justify-center lg:justify-start">
              <div className="w-80 h-[480px] bg-gray-200 rounded-2xl animate-pulse" />
            </div>

            {/* Book info skeleton */}
            <div className="space-y-6">
              <div>
                <div className="h-10 w-3/4 bg-gray-200 rounded animate-pulse mb-3" />
                <div className="h-6 w-1/2 bg-gray-200 rounded animate-pulse mb-4" />
                <div className="h-6 w-20 bg-gray-200 rounded-full animate-pulse" />
              </div>

              <div className="flex items-center space-x-3">
                <div className="h-5 w-32 bg-gray-200 rounded animate-pulse" />
              </div>

              <div className="h-8 w-24 bg-gray-200 rounded animate-pulse" />

              <div className="space-y-2">
                <div className="h-4 w-full bg-gray-200 rounded animate-pulse" />
                <div className="h-4 w-full bg-gray-200 rounded animate-pulse" />
                <div className="h-4 w-3/4 bg-gray-200 rounded animate-pulse" />
              </div>

              <div className="h-12 w-full bg-gray-200 rounded animate-pulse" />
            </div>
          </div>
        </div>
      </main>
    </div>
  )
}
