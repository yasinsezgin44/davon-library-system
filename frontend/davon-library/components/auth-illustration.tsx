"use client"

import { BookOpen, Cpu, Wifi, Cloud, Zap, Globe } from "lucide-react"

export function AuthIllustration() {
  return (
    <div className="relative w-full h-full overflow-hidden">
      {/* Background Gradient */}
      <div className="absolute inset-0 bg-gradient-to-br from-scholarly-navy via-modern-teal to-warm-coral" />

      {/* Animated Background Elements */}
      <div className="absolute inset-0 opacity-10">
        <div className="absolute top-20 left-20 w-32 h-32 rounded-full bg-white/20 blur-xl animate-pulse" />
        <div className="absolute top-40 right-32 w-24 h-24 rounded-full bg-white/15 blur-lg animate-pulse delay-1000" />
        <div className="absolute bottom-32 left-1/3 w-40 h-40 rounded-full bg-white/10 blur-2xl animate-pulse delay-2000" />
        <div className="absolute bottom-20 right-20 w-28 h-28 rounded-full bg-white/25 blur-xl animate-pulse delay-500" />
      </div>

      {/* Main Illustration Content */}
      <div className="relative z-10 flex flex-col items-center justify-center h-full p-8 text-white">
        {/* Central Book Icon with Tech Elements */}
        <div className="relative mb-8">
          {/* Main Book Icon */}
          <div className="relative z-10 p-8 bg-white/20 backdrop-blur-sm rounded-3xl border border-white/30 shadow-2xl">
            <BookOpen className="h-24 w-24 text-white" />
          </div>

          {/* Floating Tech Icons */}
          <div className="absolute -top-4 -right-4 p-3 bg-modern-teal/80 backdrop-blur-sm rounded-xl animate-bounce delay-300">
            <Cpu className="h-6 w-6 text-white" />
          </div>
          <div className="absolute -bottom-4 -left-4 p-3 bg-warm-coral/80 backdrop-blur-sm rounded-xl animate-bounce delay-700">
            <Wifi className="h-6 w-6 text-white" />
          </div>
          <div className="absolute top-1/2 -left-8 p-3 bg-white/20 backdrop-blur-sm rounded-xl animate-bounce delay-1000">
            <Cloud className="h-6 w-6 text-white" />
          </div>
          <div className="absolute top-1/2 -right-8 p-3 bg-white/20 backdrop-blur-sm rounded-xl animate-bounce delay-500">
            <Globe className="h-6 w-6 text-white" />
          </div>

          {/* Connecting Lines */}
          <div className="absolute inset-0 pointer-events-none">
            <svg className="w-full h-full" viewBox="0 0 200 200">
              <defs>
                <linearGradient id="lineGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stopColor="rgba(255,255,255,0.3)" />
                  <stop offset="100%" stopColor="rgba(255,255,255,0.1)" />
                </linearGradient>
              </defs>
              <path
                d="M100,100 L160,60 M100,100 L160,140 M100,100 L40,60 M100,100 L40,140"
                stroke="url(#lineGradient)"
                strokeWidth="2"
                strokeDasharray="5,5"
                className="animate-pulse"
              />
            </svg>
          </div>
        </div>

        {/* Text Content */}
        <div className="text-center max-w-md">
          <h1 className="text-4xl font-bold mb-4 bg-gradient-to-r from-white to-white/80 bg-clip-text text-transparent">
            Digital Library
          </h1>
          <p className="text-xl text-white/90 mb-6 leading-relaxed">Where Knowledge Meets Technology</p>
          <p className="text-white/70 leading-relaxed">
            Access thousands of books, manage your reading journey, and connect with a community of learners in our
            modern digital library platform.
          </p>
        </div>

        {/* Floating Elements */}
        <div className="absolute top-1/4 left-1/4 opacity-60">
          <div className="flex items-center space-x-2 p-3 bg-white/10 backdrop-blur-sm rounded-lg animate-float">
            <Zap className="h-5 w-5 text-yellow-300" />
            <span className="text-sm font-medium">Fast Search</span>
          </div>
        </div>

        <div className="absolute top-3/4 right-1/4 opacity-60">
          <div className="flex items-center space-x-2 p-3 bg-white/10 backdrop-blur-sm rounded-lg animate-float delay-1000">
            <BookOpen className="h-5 w-5 text-green-300" />
            <span className="text-sm font-medium">15K+ Books</span>
          </div>
        </div>

        {/* Decorative Grid */}
        <div className="absolute inset-0 opacity-5">
          <div className="grid grid-cols-8 grid-rows-8 h-full w-full">
            {Array.from({ length: 64 }).map((_, i) => (
              <div key={i} className="border border-white/20" />
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
