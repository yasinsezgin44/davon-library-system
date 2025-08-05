"use client"

import { useState } from "react"
import { Search, X } from "lucide-react"
import { cn } from "@/lib/utils"

interface MobileSearchModalProps {
  isOpen: boolean
  onClose: () => void
}

export function MobileSearchModal({ isOpen, onClose }: MobileSearchModalProps) {
  const [searchQuery, setSearchQuery] = useState("")

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 z-50 sm:hidden">
      {/* Backdrop */}
      <div className="absolute inset-0 bg-black/50 backdrop-blur-sm" onClick={onClose} />

      {/* Modal */}
      <div className="relative bg-white/95 backdrop-blur-md m-4 mt-20 rounded-lg shadow-xl">
        <div className="flex items-center p-4 border-b border-gray-200/50">
          <div className="flex-1 relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-dark-gray/50" />
            <input
              type="text"
              placeholder="Search for books..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className={cn(
                "w-full pl-10 pr-4 py-3 rounded-lg",
                "bg-white/60 backdrop-blur-sm border border-white/30",
                "text-dark-gray placeholder-dark-gray/50",
                "focus:outline-none focus:ring-2 focus:ring-modern-teal/50 focus:border-modern-teal",
              )}
              autoFocus
            />
          </div>
          <button onClick={onClose} className="ml-3 p-2 rounded-lg hover:bg-gray-100/50 transition-colors">
            <X className="h-5 w-5 text-dark-gray" />
          </button>
        </div>

        {/* Search Results Placeholder */}
        <div className="p-4">
          <p className="text-dark-gray/60 text-sm text-center">Start typing to search for books...</p>
        </div>
      </div>
    </div>
  )
}
