"use client"

import type React from "react"

import { useState, useEffect, createContext, useContext, useCallback } from "react"
import { X, CheckCircle, AlertCircle, Info, AlertTriangle } from "lucide-react"
import { cn } from "@/lib/utils"

export type ToastType = "success" | "error" | "info" | "warning"

export interface Toast {
  id: string
  type: ToastType
  title: string
  message?: string
  duration?: number
  action?: {
    label: string
    onClick: () => void
  }
}

interface ToastContextType {
  toasts: Toast[]
  addToast: (toast: Omit<Toast, "id">) => void
  removeToast: (id: string) => void
  clearAllToasts: () => void
}

const ToastContext = createContext<ToastContextType | undefined>(undefined)

export function useToast() {
  const context = useContext(ToastContext)
  if (!context) {
    throw new Error("useToast must be used within a ToastProvider")
  }
  return context
}

interface ToastProviderProps {
  children: React.ReactNode
}

export function ToastProvider({ children }: ToastProviderProps) {
  const [toasts, setToasts] = useState<Toast[]>([])

  const addToast = useCallback((toast: Omit<Toast, "id">) => {
    const id = Math.random().toString(36).substr(2, 9)
    const newToast: Toast = {
      ...toast,
      id,
      duration: toast.duration ?? 5000,
    }

    setToasts((prev) => [...prev, newToast])

    // Auto remove toast after duration
    if (newToast.duration > 0) {
      setTimeout(() => {
        removeToast(id)
      }, newToast.duration)
    }
  }, [])

  const removeToast = useCallback((id: string) => {
    setToasts((prev) => prev.filter((toast) => toast.id !== id))
  }, [])

  const clearAllToasts = useCallback(() => {
    setToasts([])
  }, [])

  return (
    <ToastContext.Provider value={{ toasts, addToast, removeToast, clearAllToasts }}>
      {children}
      <ToastContainer />
    </ToastContext.Provider>
  )
}

function ToastContainer() {
  const { toasts } = useToast()

  return (
    <div className="fixed top-4 right-4 z-50 space-y-2 max-w-sm w-full">
      {toasts.map((toast) => (
        <ToastItem key={toast.id} toast={toast} />
      ))}
    </div>
  )
}

interface ToastItemProps {
  toast: Toast
}

function ToastItem({ toast }: ToastItemProps) {
  const { removeToast } = useToast()
  const [isVisible, setIsVisible] = useState(false)
  const [isLeaving, setIsLeaving] = useState(false)

  useEffect(() => {
    // Trigger entrance animation
    const timer = setTimeout(() => setIsVisible(true), 10)
    return () => clearTimeout(timer)
  }, [])

  const handleRemove = () => {
    setIsLeaving(true)
    setTimeout(() => {
      removeToast(toast.id)
    }, 300) // Match the exit animation duration
  }

  const getToastStyles = (type: ToastType) => {
    const styles = {
      success: {
        bg: "bg-green-50 border-green-200",
        icon: CheckCircle,
        iconColor: "text-green-500",
        titleColor: "text-green-800",
        messageColor: "text-green-700",
        closeColor: "text-green-500 hover:text-green-700",
      },
      error: {
        bg: "bg-red-50 border-red-200",
        icon: AlertCircle,
        iconColor: "text-red-500",
        titleColor: "text-red-800",
        messageColor: "text-red-700",
        closeColor: "text-red-500 hover:text-red-700",
      },
      info: {
        bg: "bg-blue-50 border-blue-200",
        icon: Info,
        iconColor: "text-blue-500",
        titleColor: "text-blue-800",
        messageColor: "text-blue-700",
        closeColor: "text-blue-500 hover:text-blue-700",
      },
      warning: {
        bg: "bg-yellow-50 border-yellow-200",
        icon: AlertTriangle,
        iconColor: "text-yellow-500",
        titleColor: "text-yellow-800",
        messageColor: "text-yellow-700",
        closeColor: "text-yellow-500 hover:text-yellow-700",
      },
    }
    return styles[type]
  }

  const styles = getToastStyles(toast.type)
  const Icon = styles.icon

  return (
    <div
      className={cn(
        "relative overflow-hidden rounded-lg border shadow-lg backdrop-blur-sm transition-all duration-300 ease-out",
        styles.bg,
        isVisible && !isLeaving
          ? "transform translate-x-0 opacity-100 scale-100"
          : "transform translate-x-full opacity-0 scale-95",
        isLeaving && "transform translate-x-full opacity-0 scale-95",
      )}
    >
      {/* Progress bar for timed toasts */}
      {toast.duration && toast.duration > 0 && (
        <div className="absolute top-0 left-0 h-1 bg-current opacity-20 animate-[shrink_5s_linear_forwards]" />
      )}

      <div className="p-4">
        <div className="flex items-start space-x-3">
          {/* Icon */}
          <div className="flex-shrink-0">
            <Icon className={cn("h-5 w-5", styles.iconColor)} />
          </div>

          {/* Content */}
          <div className="flex-1 min-w-0">
            <div className={cn("text-sm font-medium", styles.titleColor)}>{toast.title}</div>
            {toast.message && <div className={cn("mt-1 text-sm", styles.messageColor)}>{toast.message}</div>}

            {/* Action Button */}
            {toast.action && (
              <div className="mt-3">
                <button
                  onClick={toast.action.onClick}
                  className={cn(
                    "text-sm font-medium underline hover:no-underline transition-all duration-200",
                    styles.iconColor,
                  )}
                >
                  {toast.action.label}
                </button>
              </div>
            )}
          </div>

          {/* Close Button */}
          <div className="flex-shrink-0">
            <button
              onClick={handleRemove}
              className={cn(
                "inline-flex rounded-md p-1.5 transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2",
                styles.closeColor,
              )}
            >
              <X className="h-4 w-4" />
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

// Convenience hook for common toast types
export function useToastHelpers() {
  const { addToast } = useToast()

  const success = useCallback(
    (title: string, message?: string, options?: Partial<Toast>) => {
      addToast({ type: "success", title, message, ...options })
    },
    [addToast],
  )

  const error = useCallback(
    (title: string, message?: string, options?: Partial<Toast>) => {
      addToast({ type: "error", title, message, ...options })
    },
    [addToast],
  )

  const info = useCallback(
    (title: string, message?: string, options?: Partial<Toast>) => {
      addToast({ type: "info", title, message, ...options })
    },
    [addToast],
  )

  const warning = useCallback(
    (title: string, message?: string, options?: Partial<Toast>) => {
      addToast({ type: "warning", title, message, ...options })
    },
    [addToast],
  )

  return { success, error, info, warning }
}
