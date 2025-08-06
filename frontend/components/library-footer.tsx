"use client"

import { BookOpen, Facebook, Twitter, Instagram, Linkedin, Mail, Phone, MapPin } from "lucide-react"
import Link from "next/link"
import { cn } from "@/lib/utils"

interface LibraryFooterProps {
  className?: string
  info?: {
    name: string
    address: string
    openingHours: string
  }
}

export function LibraryFooter({ className, info }: LibraryFooterProps) {
  const currentYear = new Date().getFullYear()

  return (
    <footer className={cn("bg-scholarly-navy text-white", className)}>
      <div className="max-w-7xl mx-auto px-4 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          <div className="lg:col-span-1">
            <div className="flex items-center space-x-3 mb-6">
              <div className="p-2 bg-modern-teal rounded-lg">
                <BookOpen className="h-6 w-6 text-white" />
              </div>
              <div>
                <h3 className="text-xl font-bold">{info?.name || 'Davon Library'}</h3>
                <p className="text-white/70 text-sm">Knowledge for Everyone</p>
              </div>
            </div>
            <p className="text-white/80 text-sm leading-relaxed mb-6">
              Discover, learn, and grow with our comprehensive collection of books, digital resources, and community
              programs designed to inspire lifelong learning.
            </p>
            <div className="space-y-2">
              <div className="flex items-center space-x-2 text-sm text-white/70">
                <MapPin className="h-4 w-4" />
                <span>{info?.address || '123 Library Street, City, State 12345'}</span>
              </div>
              <div className="flex items-center space-x-2 text-sm text-white/70">
                <p>{info?.openingHours || 'Mon-Fri: 9am - 9pm, Sat-Sun: 10am - 6pm'}</p>
              </div>
            </div>
          </div>
          {/* ... rest of the footer ... */}
        </div>
      </div>
      <div className="border-t border-white/10">
        <div className="max-w-7xl mx-auto px-4 lg:px-8 py-6">
          <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
            <div className="text-white/70 text-sm">© {currentYear} {info?.name || 'Davon Library'}. All rights reserved.</div>
          </div>
        </div>
      </div>
    </footer>
  )
}
