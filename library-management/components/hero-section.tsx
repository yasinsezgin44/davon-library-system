"use client"

import { BookOpen, Users, TrendingUp } from "lucide-react"
import { AnimatedCounter } from "./animated-counter"
import { cn } from "@/lib/utils"

const stats = [
  {
    title: "Books Available",
    value: 15847,
    icon: BookOpen,
    color: "modern-teal",
    bgColor: "bg-modern-teal/10",
    iconColor: "text-modern-teal",
  },
  {
    title: "Active Members",
    value: 2341,
    icon: Users,
    color: "warm-coral",
    bgColor: "bg-warm-coral/10",
    iconColor: "text-warm-coral",
  },
  {
    title: "Loans this Month",
    value: 1256,
    icon: TrendingUp,
    color: "scholarly-navy",
    bgColor: "bg-scholarly-navy/10",
    iconColor: "text-scholarly-navy",
  },
]

export function HeroSection() {
  return (
    <section className="relative overflow-hidden">
      {/* Background with gradient */}
      <div className="absolute inset-0 bg-gradient-to-br from-scholarly-navy via-scholarly-navy/90 to-modern-teal" />

      {/* Decorative elements */}
      <div className="absolute inset-0 opacity-10">
        <div className="absolute top-10 left-10 w-32 h-32 rounded-full bg-white/20 blur-xl" />
        <div className="absolute top-32 right-20 w-24 h-24 rounded-full bg-white/15 blur-lg" />
        <div className="absolute bottom-20 left-1/3 w-40 h-40 rounded-full bg-white/10 blur-2xl" />
      </div>

      {/* Content */}
      <div className="relative px-4 lg:px-8 py-16 lg:py-24">
        <div className="max-w-7xl mx-auto text-center">
          {/* Main headline */}
          <div className="mb-12">
            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-white mb-6 leading-tight">
              Discover Your Next
              <br />
              <span className="bg-gradient-to-r from-white to-white/80 bg-clip-text text-transparent">
                Favorite Book
              </span>
            </h1>
            <p className="text-lg md:text-xl text-white/80 max-w-2xl mx-auto leading-relaxed">
              Explore our vast collection, connect with fellow readers, and embark on literary adventures that will
              expand your horizons.
            </p>
          </div>

          {/* Stats cards */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 lg:gap-8 max-w-4xl mx-auto">
            {stats.map((stat, index) => (
              <div
                key={stat.title}
                className={cn(
                  "group relative overflow-hidden",
                  "bg-white/10 backdrop-blur-md border border-white/20",
                  "rounded-2xl p-6 lg:p-8",
                  "transition-all duration-500 ease-out",
                  "hover:bg-white/15 hover:border-white/30",
                  "hover:transform hover:scale-105 hover:shadow-2xl",
                  "cursor-pointer",
                )}
                style={{
                  animationDelay: `${index * 150}ms`,
                }}
              >
                {/* Hover glow effect */}
                <div className="absolute inset-0 opacity-0 group-hover:opacity-100 transition-opacity duration-500">
                  <div
                    className={cn(
                      "absolute inset-0 rounded-2xl blur-xl",
                      stat.color === "modern-teal" && "bg-modern-teal/20",
                      stat.color === "warm-coral" && "bg-warm-coral/20",
                      stat.color === "scholarly-navy" && "bg-white/20",
                    )}
                  />
                </div>

                {/* Card content */}
                <div className="relative z-10">
                  {/* Icon */}
                  <div
                    className={cn(
                      "inline-flex items-center justify-center w-12 h-12 lg:w-16 lg:h-16 rounded-xl mb-4",
                      "bg-white/20 backdrop-blur-sm",
                      "group-hover:bg-white/30 transition-all duration-300",
                    )}
                  >
                    <stat.icon className="w-6 h-6 lg:w-8 lg:h-8 text-white" />
                  </div>

                  {/* Value */}
                  <div className="mb-2">
                    <div className="text-3xl lg:text-4xl font-bold text-white">
                      <AnimatedCounter end={stat.value} duration={2000 + index * 200} />
                    </div>
                  </div>

                  {/* Title */}
                  <div className="text-white/80 font-medium text-sm lg:text-base">{stat.title}</div>

                  {/* Animated border */}
                  <div className="absolute inset-0 rounded-2xl border-2 border-transparent group-hover:border-white/30 transition-all duration-300" />
                </div>

                {/* Floating particles effect */}
                <div className="absolute inset-0 opacity-0 group-hover:opacity-100 transition-opacity duration-700">
                  <div
                    className="absolute top-4 right-4 w-1 h-1 bg-white/60 rounded-full animate-ping"
                    style={{ animationDelay: "0s" }}
                  />
                  <div
                    className="absolute bottom-6 left-6 w-1 h-1 bg-white/40 rounded-full animate-ping"
                    style={{ animationDelay: "0.5s" }}
                  />
                  <div
                    className="absolute top-1/2 right-8 w-0.5 h-0.5 bg-white/50 rounded-full animate-ping"
                    style={{ animationDelay: "1s" }}
                  />
                </div>
              </div>
            ))}
          </div>

          {/* Call to action */}
          <div className="mt-12">
            <button
              className={cn(
                "inline-flex items-center px-8 py-4 rounded-xl",
                "bg-white/20 backdrop-blur-sm border border-white/30",
                "text-white font-semibold text-lg",
                "hover:bg-white/30 hover:border-white/40",
                "transition-all duration-300",
                "focus:outline-none focus:ring-2 focus:ring-white/50 focus:ring-offset-2 focus:ring-offset-transparent",
              )}
            >
              <BookOpen className="w-5 h-5 mr-2" />
              Start Exploring
            </button>
          </div>
        </div>
      </div>
    </section>
  )
}
