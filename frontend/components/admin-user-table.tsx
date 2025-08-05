"use client"

import { useState, useMemo } from "react"
import { Search, Filter, MoreHorizontal, Edit, Trash2, UserPlus, Download, RefreshCw } from "lucide-react"
import { LibraryCard } from "./library-card"
import { LibraryButton } from "./library-button"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { cn } from "@/lib/utils"

interface User {
  id: string
  name: string
  email: string
  joinDate: string
  status: "active" | "inactive" | "suspended"
  booksLoaned: number
  overdue: number
  avatar?: string
}

const mockUsers: User[] = [
  {
    id: "1",
    name: "Sarah Johnson",
    email: "sarah.johnson@email.com",
    joinDate: "2023-01-15",
    status: "active",
    booksLoaned: 3,
    overdue: 0,
  },
  {
    id: "2",
    name: "Michael Chen",
    email: "michael.chen@email.com",
    joinDate: "2023-02-20",
    status: "active",
    booksLoaned: 2,
    overdue: 1,
  },
  {
    id: "3",
    name: "Emma Davis",
    email: "emma.davis@email.com",
    joinDate: "2023-03-10",
    status: "inactive",
    booksLoaned: 0,
    overdue: 0,
  },
  {
    id: "4",
    name: "James Wilson",
    email: "james.wilson@email.com",
    joinDate: "2023-04-05",
    status: "active",
    booksLoaned: 5,
    overdue: 2,
  },
  {
    id: "5",
    name: "Lisa Anderson",
    email: "lisa.anderson@email.com",
    joinDate: "2023-05-12",
    status: "suspended",
    booksLoaned: 1,
    overdue: 3,
  },
  {
    id: "6",
    name: "David Brown",
    email: "david.brown@email.com",
    joinDate: "2023-06-18",
    status: "active",
    booksLoaned: 4,
    overdue: 0,
  },
  {
    id: "7",
    name: "Jennifer Taylor",
    email: "jennifer.taylor@email.com",
    joinDate: "2023-07-22",
    status: "active",
    booksLoaned: 2,
    overdue: 0,
  },
  {
    id: "8",
    name: "Robert Martinez",
    email: "robert.martinez@email.com",
    joinDate: "2023-08-30",
    status: "active",
    booksLoaned: 1,
    overdue: 0,
  },
]

export function AdminUserTable() {
  const [searchQuery, setSearchQuery] = useState("")
  const [statusFilter, setStatusFilter] = useState<string>("all")
  const [sortBy, setSortBy] = useState<string>("name")
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc")
  const [currentPage, setCurrentPage] = useState(1)
  const itemsPerPage = 10

  const filteredAndSortedUsers = useMemo(() => {
    const filtered = mockUsers.filter((user) => {
      const matchesSearch =
        user.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        user.email.toLowerCase().includes(searchQuery.toLowerCase())

      const matchesStatus = statusFilter === "all" || user.status === statusFilter

      return matchesSearch && matchesStatus
    })

    // Sort users
    filtered.sort((a, b) => {
      let aValue: any = a[sortBy as keyof User]
      let bValue: any = b[sortBy as keyof User]

      if (sortBy === "joinDate") {
        aValue = new Date(aValue).getTime()
        bValue = new Date(bValue).getTime()
      }

      if (typeof aValue === "string") {
        aValue = aValue.toLowerCase()
        bValue = bValue.toLowerCase()
      }

      if (sortOrder === "asc") {
        return aValue < bValue ? -1 : aValue > bValue ? 1 : 0
      } else {
        return aValue > bValue ? -1 : aValue < bValue ? 1 : 0
      }
    })

    return filtered
  }, [searchQuery, statusFilter, sortBy, sortOrder])

  const paginatedUsers = useMemo(() => {
    const startIndex = (currentPage - 1) * itemsPerPage
    return filteredAndSortedUsers.slice(startIndex, startIndex + itemsPerPage)
  }, [filteredAndSortedUsers, currentPage])

  const totalPages = Math.ceil(filteredAndSortedUsers.length / itemsPerPage)

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString("en-US", {
      year: "numeric",
      month: "short",
      day: "numeric",
    })
  }

  const getStatusBadge = (status: string) => {
    const statusConfig = {
      active: { color: "text-green-700", bg: "bg-green-50", border: "border-green-200" },
      inactive: { color: "text-gray-700", bg: "bg-gray-50", border: "border-gray-200" },
      suspended: { color: "text-red-700", bg: "bg-red-50", border: "border-red-200" },
    }

    const config = statusConfig[status as keyof typeof statusConfig]

    return (
      <span
        className={cn(
          "inline-flex items-center px-2 py-1 rounded-full text-xs font-medium border",
          config.color,
          config.bg,
          config.border,
        )}
      >
        <div className={cn("w-1.5 h-1.5 rounded-full mr-1", config.color.replace("text-", "bg-"))} />
        {status.charAt(0).toUpperCase() + status.slice(1)}
      </span>
    )
  }

  const handleSort = (column: string) => {
    if (sortBy === column) {
      setSortOrder(sortOrder === "asc" ? "desc" : "asc")
    } else {
      setSortBy(column)
      setSortOrder("asc")
    }
  }

  return (
    <LibraryCard className="p-6">
      {/* Table Header */}
      <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4 mb-6">
        <div>
          <h2 className="text-xl font-semibold text-dark-gray">User Management</h2>
          <p className="text-sm text-dark-gray/70 mt-1">
            Showing {filteredAndSortedUsers.length} of {mockUsers.length} users
          </p>
        </div>

        <div className="flex items-center gap-3">
          <LibraryButton size="sm">
            <UserPlus className="h-4 w-4 mr-2" />
            Add User
          </LibraryButton>
          <LibraryButton variant="outline" size="sm">
            <Download className="h-4 w-4 mr-2" />
            Export
          </LibraryButton>
          <LibraryButton variant="outline" size="sm">
            <RefreshCw className="h-4 w-4" />
          </LibraryButton>
        </div>
      </div>

      {/* Search and Filter Controls */}
      <div className="flex flex-col lg:flex-row gap-4 mb-6">
        {/* Search */}
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-dark-gray/50" />
          <input
            type="text"
            placeholder="Search users by name or email..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-10 pr-4 py-2 rounded-lg bg-white/60 backdrop-blur-sm border border-white/30 text-dark-gray placeholder-dark-gray/50 focus:outline-none focus:ring-2 focus:ring-modern-teal/50 focus:border-modern-teal transition-all duration-200"
          />
        </div>

        {/* Filters */}
        <div className="flex items-center gap-3">
          <Select value={statusFilter} onValueChange={setStatusFilter}>
            <SelectTrigger className="w-40">
              <Filter className="h-4 w-4 mr-2" />
              <SelectValue placeholder="Status" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Status</SelectItem>
              <SelectItem value="active">Active</SelectItem>
              <SelectItem value="inactive">Inactive</SelectItem>
              <SelectItem value="suspended">Suspended</SelectItem>
            </SelectContent>
          </Select>

          <Select
            value={`${sortBy}-${sortOrder}`}
            onValueChange={(value) => {
              const [column, order] = value.split("-")
              setSortBy(column)
              setSortOrder(order as "asc" | "desc")
            }}
          >
            <SelectTrigger className="w-40">
              <SelectValue placeholder="Sort by" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="name-asc">Name A-Z</SelectItem>
              <SelectItem value="name-desc">Name Z-A</SelectItem>
              <SelectItem value="joinDate-desc">Newest First</SelectItem>
              <SelectItem value="joinDate-asc">Oldest First</SelectItem>
              <SelectItem value="booksLoaned-desc">Most Books</SelectItem>
              <SelectItem value="overdue-desc">Most Overdue</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </div>

      {/* Data Table */}
      <div className="overflow-x-auto">
        <table className="w-full">
          <thead>
            <tr className="border-b border-gray-200">
              <th
                className="text-left py-3 px-4 font-medium text-dark-gray cursor-pointer hover:text-modern-teal transition-colors"
                onClick={() => handleSort("name")}
              >
                <div className="flex items-center space-x-1">
                  <span>User</span>
                  {sortBy === "name" && <span className="text-modern-teal">{sortOrder === "asc" ? "↑" : "↓"}</span>}
                </div>
              </th>
              <th
                className="text-left py-3 px-4 font-medium text-dark-gray cursor-pointer hover:text-modern-teal transition-colors"
                onClick={() => handleSort("email")}
              >
                <div className="flex items-center space-x-1">
                  <span>Email</span>
                  {sortBy === "email" && <span className="text-modern-teal">{sortOrder === "asc" ? "↑" : "↓"}</span>}
                </div>
              </th>
              <th
                className="text-left py-3 px-4 font-medium text-dark-gray cursor-pointer hover:text-modern-teal transition-colors"
                onClick={() => handleSort("joinDate")}
              >
                <div className="flex items-center space-x-1">
                  <span>Join Date</span>
                  {sortBy === "joinDate" && <span className="text-modern-teal">{sortOrder === "asc" ? "↑" : "↓"}</span>}
                </div>
              </th>
              <th className="text-left py-3 px-4 font-medium text-dark-gray">Status</th>
              <th className="text-left py-3 px-4 font-medium text-dark-gray">Books</th>
              <th className="text-center py-3 px-4 font-medium text-dark-gray">Actions</th>
            </tr>
          </thead>
          <tbody>
            {paginatedUsers.map((user, index) => (
              <tr
                key={user.id}
                className={cn(
                  "border-b border-gray-100 hover:bg-gray-50/50 transition-colors",
                  index % 2 === 0 && "bg-gray-25",
                )}
              >
                <td className="py-4 px-4">
                  <div className="flex items-center space-x-3">
                    <div className="w-8 h-8 bg-gradient-to-br from-modern-teal to-scholarly-navy rounded-full flex items-center justify-center">
                      <span className="text-white text-sm font-medium">
                        {user.name
                          .split(" ")
                          .map((n) => n[0])
                          .join("")}
                      </span>
                    </div>
                    <div>
                      <div className="font-medium text-dark-gray">{user.name}</div>
                      <div className="text-sm text-dark-gray/70">ID: {user.id}</div>
                    </div>
                  </div>
                </td>
                <td className="py-4 px-4">
                  <div className="text-dark-gray">{user.email}</div>
                </td>
                <td className="py-4 px-4">
                  <div className="text-dark-gray">{formatDate(user.joinDate)}</div>
                </td>
                <td className="py-4 px-4">{getStatusBadge(user.status)}</td>
                <td className="py-4 px-4">
                  <div className="text-dark-gray">
                    {user.booksLoaned} loaned
                    {user.overdue > 0 && <div className="text-xs text-red-600 font-medium">{user.overdue} overdue</div>}
                  </div>
                </td>
                <td className="py-4 px-4">
                  <div className="flex items-center justify-center">
                    <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <button className="p-2 hover:bg-gray-100 rounded-lg transition-colors">
                          <MoreHorizontal className="h-4 w-4 text-dark-gray" />
                        </button>
                      </DropdownMenuTrigger>
                      <DropdownMenuContent align="end" className="w-48">
                        <DropdownMenuItem className="cursor-pointer">
                          <Edit className="h-4 w-4 mr-2" />
                          Edit User
                        </DropdownMenuItem>
                        <DropdownMenuItem className="cursor-pointer">View Details</DropdownMenuItem>
                        <DropdownMenuItem className="cursor-pointer">Send Message</DropdownMenuItem>
                        <DropdownMenuItem className="cursor-pointer text-red-600 hover:text-red-700">
                          <Trash2 className="h-4 w-4 mr-2" />
                          Delete User
                        </DropdownMenuItem>
                      </DropdownMenuContent>
                    </DropdownMenu>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex items-center justify-between mt-6 pt-4 border-t border-gray-200">
          <div className="text-sm text-dark-gray/70">
            Showing {(currentPage - 1) * itemsPerPage + 1} to{" "}
            {Math.min(currentPage * itemsPerPage, filteredAndSortedUsers.length)} of {filteredAndSortedUsers.length}{" "}
            results
          </div>

          <div className="flex items-center space-x-2">
            <LibraryButton
              variant="outline"
              size="sm"
              onClick={() => setCurrentPage(Math.max(1, currentPage - 1))}
              disabled={currentPage === 1}
            >
              Previous
            </LibraryButton>

            {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
              const pageNum = i + 1
              return (
                <button
                  key={pageNum}
                  onClick={() => setCurrentPage(pageNum)}
                  className={cn(
                    "px-3 py-1 rounded-md text-sm font-medium transition-colors",
                    currentPage === pageNum ? "bg-modern-teal text-white" : "text-dark-gray hover:bg-gray-100",
                  )}
                >
                  {pageNum}
                </button>
              )
            })}

            <LibraryButton
              variant="outline"
              size="sm"
              onClick={() => setCurrentPage(Math.min(totalPages, currentPage + 1))}
              disabled={currentPage === totalPages}
            >
              Next
            </LibraryButton>
          </div>
        </div>
      )}
    </LibraryCard>
  )
}
