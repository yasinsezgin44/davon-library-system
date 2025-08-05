import type React from "react"
import { cn } from "@/lib/utils"

interface LibraryCardProps {
  children: React.ReactNode
  className?: string
  glassmorphism?: boolean
}

export function LibraryCard({ children, className, glassmorphism = false }: LibraryCardProps) {
  return (
    <div
      className={cn(
        "rounded-xl shadow-lg transition-all duration-300 hover:shadow-xl",
        glassmorphism ? "bg-white/80 backdrop-blur-sm border border-white/20" : "bg-clean-white border border-gray-100",
        className,
      )}
    >
      {children}
    </div>
  )
}
