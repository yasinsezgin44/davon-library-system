"use client";

import { useRouter } from "next/navigation";
import { useEffect, useState, ReactNode } from "react";

interface ProtectedUser {
  id: number;
  name: string;
  email: string;
  role: string;
}

export default function AdminRouteProtector({
  children,
}: {
  children: ReactNode;
}) {
  const router = useRouter();
  const [isVerified, setIsVerified] = useState(false); // State to prevent flash of content

  useEffect(() => {
    const storedUser = localStorage.getItem("user");
    let user: ProtectedUser | null = null;

    if (storedUser) {
      try {
        user = JSON.parse(storedUser);
      } catch (e) {
        console.error("Failed to parse user from local storage", e);
        localStorage.removeItem("user");
      }
    }

    // Check if user exists and has the 'Admin' role
    if (!user || user.role !== "Admin") {
      console.log("Access denied. User not admin or not logged in.");
      router.push("/login"); // Redirect non-admins to login
    } else {
      console.log("Admin access granted.");
      setIsVerified(true); // User is verified
    }
  }, [router]);

  // Render children only if verification is complete and successful
  if (!isVerified) {
    // Optionally return a loading spinner or null while checking
    return null; // Or <p>Loading...</p>
  }

  return <>{children}</>; // Render the protected content
}
