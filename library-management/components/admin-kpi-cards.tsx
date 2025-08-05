"use client"

import type React from "react"

import { BookOpen, Users, AlertTriangle, TrendingUp, TrendingDown } from "lucide-react"
import { LibraryCard } from "./library-card"
import { AnimatedCounter } from "./animated-counter"
import { cn } from "@/lib/utils"

interface KPIData {
  title: string
  value: number
  change: number
  changeType: "positive" | "negative" | "neutral"
  icon: React.ElementType
  color: string
  bgColor: string
  suffix?: string
  prefix?: string
}

const kpiData: KPIData[] = [
  {
    title: "Total Books",
    value: 15847,
    change: 12.5,
    changeType: "positive",
    icon: BookOpen,
    color: "text-modern-teal",
    bgColor: "bg-modern-teal/10",
    suffix: "",
  },
  {
    title: "Active Users",
    value: 2341,
    change: 8.2,
    changeType: "positive",
    icon: Users,
    color: "text-scholarly-navy",
    bgColor: "bg-scholarly-navy/10",
    suffix: "",
  },
  {
    title: "Overdue Loans",
    value: 127,
    change: -15.3,
    changeType: "positive", // Negative change is positive for overdue loans
    icon: AlertTriangle,
    color: "text-warm-coral",
    bgColor: "bg-warm-coral/10",
    suffix: "",
  },
]

export function AdminKPICards() {
  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
      {kpiData.map((kpi, index) => {
        const Icon = kpi.icon
        const TrendIcon = kpi.change >= 0 ? TrendingUp : TrendingDown
        const isPositiveTrend =
          (kpi.title === "Overdue Loans" && kpi.change < 0) || (kpi.title !== "Overdue Loans" && kpi.change > 0)

        return (
          <LibraryCard
            key={kpi.title}
            glassmorphism
            className="p-6 hover:shadow-xl transition-all duration-300 hover:scale-105"
          >
            <div className="flex items-center justify-between mb-4">
              <div className={cn("p-3 rounded-xl", kpi.bgColor)}>
                <Icon className={cn("h-6 w-6", kpi.color)} />
              </div>
              <div className="flex items-center space-x-1">
                <TrendIcon className={cn("h-4 w-4", isPositiveTrend ? "text-green-500" : "text-red-500")} />
                <span className={cn("text-sm font-medium", isPositiveTrend ? "text-green-500" : "text-red-500")}>
                  {Math.abs(kpi.change)}%
                </span>
              </div>
            </div>

            <div className="space-y-2">
              <div className="text-3xl font-bold text-dark-gray">
                <AnimatedCounter
                  end={kpi.value}
                  duration={2000 + index * 200}
                  prefix={kpi.prefix}
                  suffix={kpi.suffix}
                />
              </div>
              <div className="text-sm text-dark-gray/70">{kpi.title}</div>
              <div className="text-xs text-dark-gray/60">{isPositiveTrend ? "↗" : "↘"} vs last month</div>
            </div>

            {/* Progress indicator */}
            <div className="mt-4 w-full bg-gray-200 rounded-full h-1">
              <div
                className={cn(
                  "h-1 rounded-full transition-all duration-1000 ease-out",
                  kpi.color.replace("text-", "bg-"),
                )}
                style={{
                  width: `${Math.min((Math.abs(kpi.change) / 20) * 100, 100)}%`,
                  animationDelay: `${index * 200}ms`,
                }}
              />
            </div>
          </LibraryCard>
        )
      })}
    </div>
  )
}
