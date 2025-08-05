"use client"

import { BookOpen, Users, TrendingUp } from "lucide-react"
import { AnimatedCounter } from "./animated-counter"
import { cn } from "@/lib/utils"

const stats = [
  {
    title: "Books Available",
    value: 15847,
    icon: BookOpen,
  },
  {
    title: "Active Members",
    value: 2341,
    icon: Users,
  },
  {
    title: "Loans this Month",
    value: 1256,
    icon: TrendingUp,
  },
]

interface HeroSectionCompactProps {
  title?: string
  subtitle?: string
}

export function HeroSectionCompact({
  title = "Dashboard Overview",
  subtitle = "Your library management at a glance",
}: HeroSectionCompactProps) {
  return (
    <section className="relative overflow-hidden mb-8">
      {/* Background with gradient */}
      <div className="absolute inset-0 bg-gradient-to-r from-scholarly-navy/5 via-modern-teal/5 to-warm-coral/5" />

      {/* Content */}
      <div className="relative px-6 py-8">
        <div className="max-w-7xl mx-auto">
          {/* Title */}
          <div className="text-center mb-8">
            <h2 className="text-2xl lg:text-3xl font-bold text-dark-gray mb-2">{title}</h2>
            <p className="text-dark-gray/70">{subtitle}</p>
          </div>

          {/* Stats cards */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 lg:gap-6">
            {stats.map((stat, index) => (
              <div
                key={stat.title}
                className={cn(
                  "group relative overflow-hidden",
                  "bg-white border border-gray-100",
                  "rounded-xl p-6",
                  "transition-all duration-300 ease-out",
                  "hover:shadow-lg hover:border-modern-teal/20",
                  "hover:transform hover:scale-105",
                  "cursor-pointer",
                )}
              >
                {/* Card content */}
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-dark-gray/60 text-sm font-medium mb-1">{stat.title}</p>
                    <p className="text-2xl font-bold text-dark-gray">
                      <AnimatedCounter end={stat.value} duration={1500 + index * 200} />
                    </p>
                  </div>
                  <div className="p-3 bg-modern-teal/10 rounded-lg group-hover:bg-modern-teal/20 transition-colors">
                    <stat.icon className="h-6 w-6 text-modern-teal" />
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </section>
  )
}
