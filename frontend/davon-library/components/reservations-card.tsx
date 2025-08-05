"use client"

import { Users, Clock, CheckCircle } from "lucide-react"
import { LibraryCard } from "./library-card"
import { LibraryButton } from "./library-button"
import { cn } from "@/lib/utils"

interface Reservation {
  id: string
  title: string
  author: string
  cover: string
  queuePosition: number
  totalInQueue: number
  estimatedAvailableDate: string
  status: "waiting" | "ready" | "notified"
}

const mockReservations: Reservation[] = [
  {
    id: "1",
    title: "The Seven Husbands of Evelyn Hugo",
    author: "Taylor Jenkins Reid",
    cover: "/placeholder.svg?height=120&width=80",
    queuePosition: 2,
    totalInQueue: 8,
    estimatedAvailableDate: "2024-02-15",
    status: "waiting",
  },
  {
    id: "2",
    title: "Project Hail Mary",
    author: "Andy Weir",
    cover: "/placeholder.svg?height=120&width=80",
    queuePosition: 1,
    totalInQueue: 3,
    estimatedAvailableDate: "2024-01-28",
    status: "ready",
  },
]

export function ReservationsCard() {
  const formatEstimatedDate = (dateString: string) => {
    const date = new Date(dateString)
    return date.toLocaleDateString("en-US", { month: "short", day: "numeric" })
  }

  const getStatusInfo = (status: string, queuePosition: number) => {
    switch (status) {
      case "ready":
        return {
          text: "Ready for pickup",
          color: "text-green-600",
          bgColor: "bg-green-50",
          borderColor: "border-green-200",
          icon: CheckCircle,
        }
      case "notified":
        return {
          text: "Notification sent",
          color: "text-blue-600",
          bgColor: "bg-blue-50",
          borderColor: "border-blue-200",
          icon: CheckCircle,
        }
      default:
        return {
          text: `#${queuePosition} in queue`,
          color: "text-warm-coral",
          bgColor: "bg-warm-coral/10",
          borderColor: "border-warm-coral/20",
          icon: Users,
        }
    }
  }

  return (
    <LibraryCard className="p-6">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-semibold text-dark-gray">My Reservations</h2>
        <span className="text-sm text-dark-gray/70">{mockReservations.length} active</span>
      </div>

      <div className="space-y-4">
        {mockReservations.map((reservation) => {
          const statusInfo = getStatusInfo(reservation.status, reservation.queuePosition)
          const StatusIcon = statusInfo.icon

          return (
            <div
              key={reservation.id}
              className="flex items-center space-x-4 p-4 bg-gray-50/50 rounded-lg hover:bg-gray-50 transition-colors"
            >
              {/* Book Cover */}
              <div className="flex-shrink-0">
                <img
                  src={reservation.cover || "/placeholder.svg"}
                  alt={reservation.title}
                  className="w-12 h-16 object-cover rounded-md shadow-sm"
                />
              </div>

              {/* Book Info */}
              <div className="flex-1 min-w-0">
                <h3 className="font-medium text-dark-gray truncate">{reservation.title}</h3>
                <p className="text-sm text-dark-gray/70 truncate">{reservation.author}</p>
                <div className="flex items-center mt-2 space-x-4">
                  <div className="flex items-center space-x-1 text-xs text-dark-gray/60">
                    <Users className="h-3 w-3" />
                    <span>{reservation.totalInQueue} in queue</span>
                  </div>
                  <div className="flex items-center space-x-1 text-xs text-dark-gray/60">
                    <Clock className="h-3 w-3" />
                    <span>Est. {formatEstimatedDate(reservation.estimatedAvailableDate)}</span>
                  </div>
                </div>
              </div>

              {/* Status Badge */}
              <div
                className={cn(
                  "flex items-center space-x-1 px-2 py-1 rounded-full text-xs font-medium",
                  statusInfo.bgColor,
                  statusInfo.borderColor,
                  "border",
                )}
              >
                <StatusIcon className={cn("h-3 w-3", statusInfo.color)} />
                <span className={statusInfo.color}>{statusInfo.text}</span>
              </div>
            </div>
          )
        })}
      </div>

      {/* Action Buttons */}
      <div className="mt-6 pt-4 border-t border-gray-200 flex space-x-3">
        <LibraryButton variant="outline" size="sm" className="flex-1">
          Manage Queue
        </LibraryButton>
        <LibraryButton size="sm" className="flex-1">
          Add Reservation
        </LibraryButton>
      </div>
    </LibraryCard>
  )
}
