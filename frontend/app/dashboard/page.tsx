// frontend/app/dashboard/page.tsx
import { LibrarySidebar } from "@/components/library-sidebar";
import { LibraryHeader } from "@/components/library-header";
import { CurrentlyBorrowedCard } from "@/components/currently-borrowed-card";
import { ReadingProgressCard } from "@/components/reading-progress-card";
import { ReservationsCard } from "@/components/reservations-card";
import { QuickStatsCard } from "@/components/quick-stats-card";

export default function DashboardPage() {
  return (
    <div className="min-h-screen bg-clean-white">
      <LibraryHeader />
      <LibrarySidebar />

      <main className="lg:ml-64 p-4 lg:p-8">
        <div className="max-w-7xl mx-auto">
          <div className="mb-8 pt-32 lg:pt-16">
            <h1 className="text-3xl font-bold text-dark-gray mb-2">
              My Dashboard
            </h1>
            <p className="text-dark-gray/70">Welcome back, [User Name]!</p>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <div className="lg:col-span-2">
              <CurrentlyBorrowedCard />
            </div>
            <div className="space-y-8">
              <ReadingProgressCard />
              <ReservationsCard />
            </div>
          </div>
          <div className="mt-8">
            <QuickStatsCard />
          </div>
        </div>
      </main>
    </div>
  );
}
