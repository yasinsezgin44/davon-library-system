"use client"

import { useState } from "react"
import { Search, Bell, User, Settings, LogOut, BookOpen } from "lucide-react"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { cn } from "@/lib/utils"

interface LibraryHeaderProps {
  className?: string
}

export function LibraryHeader({ className }: LibraryHeaderProps) {
  const [searchQuery, setSearchQuery] = useState("")
  const [hasNotifications, setHasNotifications] = useState(true)

  return (
    <header
      className={cn(
        "fixed top-0 left-0 right-0 z-30 h-16 lg:ml-64",
        "bg-white/80 backdrop-blur-md border-b border-white/20",
        "shadow-sm transition-all duration-300",
        className,
      )}
    >
      <div className="flex items-center justify-between h-full px-4 lg:px-8">
        {/* Logo - Hidden on larger screens since sidebar has it */}
        <div className="flex items-center space-x-3 lg:hidden">
          <BookOpen className="h-6 w-6 text-scholarly-navy" />
          <span className="font-bold text-lg text-scholarly-navy">Davon Library</span>
        </div>

        {/* Logo for larger screens - minimal version */}
        <div className="hidden lg:flex items-center">
          <span className="font-semibold text-dark-gray">Dashboard</span>
        </div>

        {/* Search Bar - Centered */}
        <div className="hidden sm:flex flex-1 max-w-md mx-4 lg:mx-8">
          <div className="relative w-full">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <Search className="h-4 w-4 text-dark-gray/50" />
            </div>
            <input
              type="text"
              placeholder="Search for books..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className={cn(
                "w-full pl-10 pr-4 py-2 rounded-lg",
                "bg-white/60 backdrop-blur-sm border border-white/30",
                "text-dark-gray placeholder-dark-gray/50",
                "focus:outline-none focus:ring-2 focus:ring-modern-teal/50 focus:border-modern-teal",
                "transition-all duration-200",
                "hover:bg-white/80",
              )}
            />
          </div>
        </div>

        {/* Right Side - Notifications and User */}
        <div className="flex items-center space-x-3">
          {/* Mobile Search Button */}
          <button className="sm:hidden p-2 rounded-lg hover:bg-white/50 transition-colors">
            <Search className="h-5 w-5 text-dark-gray" />
          </button>

          {/* Notifications */}
          <div className="relative">
            <button
              className={cn(
                "p-2 rounded-lg transition-all duration-200",
                "hover:bg-white/50 active:bg-white/70",
                "focus:outline-none focus:ring-2 focus:ring-modern-teal/50",
              )}
              onClick={() => setHasNotifications(false)}
            >
              <Bell className="h-5 w-5 text-dark-gray" />
              {hasNotifications && (
                <span className="absolute -top-1 -right-1 h-3 w-3 bg-warm-coral rounded-full border-2 border-white animate-pulse" />
              )}
            </button>
          </div>

          {/* User Avatar Dropdown */}
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <button
                className={cn(
                  "flex items-center space-x-2 p-1 rounded-lg transition-all duration-200",
                  "hover:bg-white/50 active:bg-white/70",
                  "focus:outline-none focus:ring-2 focus:ring-modern-teal/50",
                )}
              >
                <div className="relative">
                  <div className="h-8 w-8 rounded-full bg-gradient-to-br from-modern-teal to-scholarly-navy flex items-center justify-center">
                    <User className="h-4 w-4 text-white" />
                  </div>
                  <div className="absolute -bottom-0.5 -right-0.5 h-3 w-3 bg-green-400 rounded-full border-2 border-white" />
                </div>
                <span className="hidden md:block text-sm font-medium text-dark-gray">John Doe</span>
              </button>
            </DropdownMenuTrigger>
            <DropdownMenuContent
              align="end"
              className={cn("w-56 mt-2", "bg-white/95 backdrop-blur-md border border-white/20", "shadow-lg rounded-lg")}
            >
              <DropdownMenuLabel className="text-dark-gray">
                <div className="flex flex-col space-y-1">
                  <p className="text-sm font-medium">John Doe</p>
                  <p className="text-xs text-dark-gray/60">librarian@davonlibrary.com</p>
                </div>
              </DropdownMenuLabel>
              <DropdownMenuSeparator className="bg-gray-200/50" />
              <DropdownMenuItem className="text-dark-gray hover:bg-modern-teal/10 hover:text-modern-teal cursor-pointer">
                <User className="mr-2 h-4 w-4" />
                <span>Profile</span>
              </DropdownMenuItem>
              <DropdownMenuItem className="text-dark-gray hover:bg-modern-teal/10 hover:text-modern-teal cursor-pointer">
                <Settings className="mr-2 h-4 w-4" />
                <span>Settings</span>
              </DropdownMenuItem>
              <DropdownMenuItem className="text-dark-gray hover:bg-modern-teal/10 hover:text-modern-teal cursor-pointer">
                <Bell className="mr-2 h-4 w-4" />
                <span>Notifications</span>
              </DropdownMenuItem>
              <DropdownMenuSeparator className="bg-gray-200/50" />
              <DropdownMenuItem className="text-warm-coral hover:bg-warm-coral/10 cursor-pointer">
                <LogOut className="mr-2 h-4 w-4" />
                <span>Log out</span>
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </div>
    </header>
  )
}
