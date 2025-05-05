// File: davon-library-webui/src/app/components/PrivateRoute.tsx
"use client";

import { useAuth } from "./contexts/AuthContext"; // Adjust path if needed
import { useRouter } from "next/navigation";
import { useEffect } from "react";

export default function PrivateRoute({
  children,
}: {
  children: React.ReactNode;
}) {
  const { user } = useAuth();
  const router = useRouter();

  useEffect(() => {
    // If the check runs and there's no user, redirect to home page
    // You might want to redirect to a specific login page or show a message instead
    if (user === null) {
      console.log("PrivateRoute: No user found, redirecting to /");
      router.replace("/"); // Use replace to avoid adding the protected route to history
    }
  }, [user, router]); // Re-run effect if user state or router changes

  // While checking or if redirecting, don't render children yet
  // You could show a loading spinner here
  if (user === null) {
    return null; // Or <LoadingSpinner />
  }

  // If user exists, render the children components
  return <>{children}</>;
}
