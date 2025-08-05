import { cn } from "@/lib/utils";
import { type ButtonHTMLAttributes, forwardRef } from "react";

interface LibraryButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "primary" | "secondary" | "outline" | "danger";
  size?: "sm" | "md" | "lg";
}

export const LibraryButton = forwardRef<HTMLButtonElement, LibraryButtonProps>(
  ({ className, variant = "primary", size = "md", ...props }, ref) => {
    return (
      <button
        className={cn(
          "inline-flex items-center justify-center rounded-lg font-medium transition-all duration-200 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-modern-teal focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50",
          {
            "bg-modern-teal text-white hover:bg-modern-teal/90 active:bg-modern-teal/80":
              variant === "primary",
            "bg-warm-coral text-white hover:bg-warm-coral/90 active:bg-warm-coral/80":
              variant === "secondary",
            "border border-modern-teal text-modern-teal hover:bg-modern-teal hover:text-white":
              variant === "outline",
            "bg-red-600 text-white hover:bg-red-700 active:bg-red-800":
              variant === "danger",
          },
          {
            "h-8 px-3 text-sm": size === "sm",
            "h-10 px-4 py-2": size === "md",
            "h-12 px-6 text-lg": size === "lg",
          },
          className
        )}
        ref={ref}
        {...props}
      />
    );
  }
);
LibraryButton.displayName = "LibraryButton";
