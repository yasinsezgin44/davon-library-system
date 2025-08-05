"use client"

import type React from "react"

import { BookOpen, Clock, Star, TrendingUp } from "lucide-react"
import { LibraryCard } from "./library-card"
import { cn } from "@/lib/utils"

interface QuickStat {
  label: string
  value: string | number
  icon: React.ElementType
  color: string
  bgColor: string
  change?: string
  changeType?: "positive" | "negative" | "neutral"
}

const quickStats: QuickStat[] = [
  {
    label: "Books Read",
    value: 27,
    icon: BookOpen,
    color: "text-modern-teal",
    bgColor: "bg-modern-teal/10",
    change: "+3 this month",
    changeType: "positive",
  },
  {
    label: "Reading Streak",
    value: "12 days",
    icon: Clock,
    color: "text-warm-coral",
    bgColor: "bg-warm-coral/10",
    change: "Personal best!",
    changeType: "positive",
  },
  {
    label: "Avg Rating Given",
    value: 4.2,
    icon: Star,
    color: "text-yellow-600",
    bgColor: "bg-yellow-100",
    change: "↑ 0.3",
    changeType: "positive",
  },
  {
    label: "Reading Goal",
    value: "40%",
    icon: TrendingUp,
    color: "text-green-600",
    bgColor: "bg-green-100",
    change: "16/40 books",
    changeType: "neutral",
  },
]

export function QuickStatsCard() {
  return (
    <LibraryCard className="p-6">
      <h2 className="text-xl font-semibold text-dark-gray mb-6">Quick Stats</h2>

      <div className="grid grid-cols-2 gap-4">
        {quickStats.map((stat, index) => {
          const Icon = stat.icon

          return (
            <div key={index} className="p-4 bg-gray-50/50 rounded-lg hover:bg-gray-50 transition-colors">
              <div className="flex items-center justify-between mb-2">
                <div className={cn("p-2 rounded-lg", stat.bgColor)}>
                  <Icon className={cn("h-4 w-4", stat.color)} />
                </div>
              </div>

              <div className="space-y-1">
                <div className="text-2xl font-bold text-dark-gray">{stat.value}</div>
                <div className="text-sm text-dark-gray/70">{stat.label}</div>
                {stat.change && (
                  <div
                    className={cn(
                      "text-xs font-medium",
                      stat.changeType === "positive"
                        ? "text-green-600"
                        : stat.changeType === "negative"
                          ? "text-red-600"
                          : "text-dark-gray/60",
                    )}
                  >
                    {stat.change}
                  </div>
                )}
              </div>
            </div>
          )
        })}
      </div>
    </LibraryCard>
  )
}
