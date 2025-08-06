// frontend/app/page.tsx
import { LibraryCard } from "@/components/library-card";
import { LibraryButton } from "@/components/library-button";
import { LibrarySidebar } from "@/components/library-sidebar";
import { LibraryHeader } from "@/components/library-header";
import { LibraryFooter } from "@/components/library-footer";
import { HeroSection } from "@/components/hero-section";
import { ToastDemo } from "@/components/toast-demo";

export default function HomePage() {
  return (
    <div className="min-h-screen bg-clean-white">
      <LibraryHeader />
      <LibrarySidebar />

      {/* Main content */}
      <main className="lg:ml-64 p-4 lg:p-8">
        <div className="max-w-7xl mx-auto">
          {/* Hero Section */}
          <HeroSection />

          {/* Toast Demo */}
          <div className="mb-8">
            <ToastDemo />
          </div>

          {/* Quick Actions */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
            <LibraryCard className="p-8">
              <h2 className="text-2xl font-bold text-dark-gray mb-4">
                Quick Actions
              </h2>
              <div className="space-y-4">
                <div className="flex flex-col sm:flex-row gap-3">
                  <LibraryButton className="flex-1">Add New Book</LibraryButton>
                  <LibraryButton variant="outline" className="flex-1">
                    Register Member
                  </LibraryButton>
                </div>
                <div className="flex flex-col sm:flex-row gap-3">
                  <LibraryButton variant="secondary" className="flex-1">
                    Issue Book
                  </LibraryButton>
                  <LibraryButton variant="outline" className="flex-1">
                    Return Book
                  </LibraryButton>
                </div>
              </div>
            </LibraryCard>

            <LibraryCard className="p-8">
              <h2 className="text-2xl font-bold text-dark-gray mb-4">
                Recent Activity
              </h2>
              <div className="space-y-4">
                <div className="flex items-center space-x-3 p-3 bg-gray-50 rounded-lg">
                  <div className="w-2 h-2 bg-modern-teal rounded-full"></div>
                  <div className="flex-1">
                    <p className="text-sm font-medium text-dark-gray">
                      "The Great Gatsby" returned by John Doe
                    </p>
                    <p className="text-xs text-dark-gray/60">2 hours ago</p>
                  </div>
                </div>
                <div className="flex items-center space-x-3 p-3 bg-gray-50 rounded-lg">
                  <div className="w-2 h-2 bg-warm-coral rounded-full"></div>
                  <div className="flex-1">
                    <p className="text-sm font-medium text-dark-gray">
                      New member "Jane Smith" registered
                    </p>
                    <p className="text-xs text-dark-gray/60">4 hours ago</p>
                  </div>
                </div>
                <div className="flex items-center space-x-3 p-3 bg-gray-50 rounded-lg">
                  <div className="w-2 h-2 bg-modern-teal rounded-full"></div>
                  <div className="flex-1">
                    <p className="text-sm font-medium text-dark-gray">
                      "1984" issued to Mike Johnson
                    </p>
                    <p className="text-xs text-dark-gray/60">6 hours ago</p>
                  </div>
                </div>
              </div>
            </LibraryCard>
          </div>

          {/* Featured Section */}
          <LibraryCard glassmorphism className="p-8">
            <div className="text-center">
              <h2 className="text-3xl font-bold text-dark-gray mb-4">
                Modern Library Management
              </h2>
              <p className="text-dark-gray/70 mb-6 max-w-2xl mx-auto">
                Experience the future of library management with our intuitive
                interface, powerful features, and seamless user experience
                designed for modern libraries.
              </p>
              <div className="flex flex-col sm:flex-row gap-4 justify-center">
                <LibraryButton size="lg">Explore Features</LibraryButton>
                <LibraryButton variant="outline" size="lg">
                  View Documentation
                </LibraryButton>
              </div>
            </div>
          </LibraryCard>
        </div>
      </main>

      {/* Footer */}
      <LibraryFooter />
    </div>
  );
}
