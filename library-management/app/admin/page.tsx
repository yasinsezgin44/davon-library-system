import { LibrarySidebar } from "@/components/library-sidebar"
import { LibraryHeader } from "@/components/library-header"
import { AdminKPICards } from "@/components/admin-kpi-cards"
import { AdminUserTable } from "@/components/admin-user-table"
import { LibraryButton } from "@/components/library-button"
import { Settings, Bell, Download, BarChart3 } from "lucide-react"

export default function AdminDashboard() {
  return (
    <div className="min-h-screen bg-clean-white">
      <LibraryHeader />
      <LibrarySidebar />

      <main className="lg:ml-64 p-4 lg:p-8">
        <div className="max-w-7xl mx-auto">
          {/* Admin Header */}
          <div className="mb-8 pt-32 lg:pt-16">
            <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
              <div>
                <h1 className="text-3xl font-bold text-dark-gray mb-2">Admin Dashboard</h1>
                <p className="text-dark-gray/70">Manage your library operations and monitor key metrics</p>
              </div>

              {/* Admin Quick Actions */}
              <div className="flex items-center gap-3">
                <LibraryButton variant="outline" size="sm">
                  <BarChart3 className="h-4 w-4 mr-2" />
                  Analytics
                </LibraryButton>
                <LibraryButton variant="outline" size="sm">
                  <Download className="h-4 w-4 mr-2" />
                  Reports
                </LibraryButton>
                <LibraryButton variant="outline" size="sm">
                  <Bell className="h-4 w-4 mr-2" />
                  Alerts
                </LibraryButton>
                <LibraryButton size="sm">
                  <Settings className="h-4 w-4 mr-2" />
                  Settings
                </LibraryButton>
              </div>
            </div>
          </div>

          {/* KPI Cards */}
          <AdminKPICards />

          {/* User Management Table */}
          <AdminUserTable />
        </div>
      </main>
    </div>
  )
}
