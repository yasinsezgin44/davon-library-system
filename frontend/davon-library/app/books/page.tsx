import { LibraryCard } from "@/components/library-card"
import { LibraryButton } from "@/components/library-button"
import { LibrarySidebar } from "@/components/library-sidebar"
import { LibraryHeader } from "@/components/library-header"

export default function BooksPage() {
  return (
    <div className="min-h-screen bg-clean-white">
      <LibraryHeader />
      <LibrarySidebar />

      <main className="lg:ml-64 p-4 lg:p-8">
        <div className="max-w-7xl mx-auto">
          <div className="mb-8 pt-32 lg:pt-16">
            <h1 className="text-3xl font-bold text-dark-gray mb-2">Books</h1>
            <p className="text-dark-gray/70">Manage your library's book collection</p>
          </div>

          <LibraryCard className="p-8">
            <h2 className="text-xl font-semibold text-dark-gray mb-4">Book Management</h2>
            <p className="text-dark-gray/70 mb-4">
              This is the books page. You can add book listings, search functionality, and book management features
              here.
            </p>
            <LibraryButton>Add New Book</LibraryButton>
          </LibraryCard>
        </div>
      </main>
    </div>
  )
}
