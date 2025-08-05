"use client"

import { LibraryButton } from "./library-button"
import { LibraryCard } from "./library-card"
import { useToastHelpers } from "./toast-notification"

export function ToastDemo() {
  const { success, error, info, warning } = useToastHelpers()

  const showSuccessToast = () => {
    success("Book borrowed successfully!", "You can keep it for 14 days.", {
      action: {
        label: "View Details",
        onClick: () => console.log("View details clicked"),
      },
    })
  }

  const showErrorToast = () => {
    error("Failed to borrow book", "This book is currently unavailable. Please try again later.")
  }

  const showInfoToast = () => {
    info("New books available", "Check out our latest arrivals in the fiction section.", {
      duration: 7000,
    })
  }

  const showWarningToast = () => {
    warning("Book due soon", "Your borrowed book is due in 2 days. Don't forget to return it!")
  }

  return (
    <LibraryCard className="p-6">
      <h3 className="text-lg font-semibold text-dark-gray mb-4">Toast Notifications Demo</h3>
      <div className="grid grid-cols-2 gap-3">
        <LibraryButton onClick={showSuccessToast} className="bg-green-600 hover:bg-green-700">
          Success Toast
        </LibraryButton>
        <LibraryButton onClick={showErrorToast} className="bg-red-600 hover:bg-red-700">
          Error Toast
        </LibraryButton>
        <LibraryButton onClick={showInfoToast} className="bg-blue-600 hover:bg-blue-700">
          Info Toast
        </LibraryButton>
        <LibraryButton onClick={showWarningToast} className="bg-yellow-600 hover:bg-yellow-700">
          Warning Toast
        </LibraryButton>
      </div>
    </LibraryCard>
  )
}
