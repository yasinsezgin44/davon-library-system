"use client"

import { useEffect, useState } from "react"

interface AnimatedCounterProps {
  end: number
  duration?: number
  start?: number
  suffix?: string
  prefix?: string
}

export function AnimatedCounter({ end, duration = 2000, start = 0, suffix = "", prefix = "" }: AnimatedCounterProps) {
  const [count, setCount] = useState(start)
  const [hasAnimated, setHasAnimated] = useState(false)

  useEffect(() => {
    if (hasAnimated) return

    const startTime = Date.now()
    const startValue = start
    const endValue = end

    const updateCount = () => {
      const now = Date.now()
      const elapsed = now - startTime
      const progress = Math.min(elapsed / duration, 1)

      // Easing function for smooth animation
      const easeOutQuart = 1 - Math.pow(1 - progress, 4)
      const currentCount = Math.floor(startValue + (endValue - startValue) * easeOutQuart)

      setCount(currentCount)

      if (progress < 1) {
        requestAnimationFrame(updateCount)
      } else {
        setHasAnimated(true)
      }
    }

    const timer = setTimeout(() => {
      requestAnimationFrame(updateCount)
    }, 100) // Small delay to ensure component is mounted

    return () => clearTimeout(timer)
  }, [end, duration, start, hasAnimated])

  return (
    <span className="tabular-nums">
      {prefix}
      {count.toLocaleString()}
      {suffix}
    </span>
  )
}
