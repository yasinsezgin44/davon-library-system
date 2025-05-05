"use client"; // <-- Make this a client component to use the protector

import { AdminUI } from "@/app/components/admin/AdminApp";
import AdminRouteProtector from "../components/auth/AdminRouteProtecter"; // Import the protector

// This page component itself can remain a Server Component,
// but it renders the client-side AdminApp.
export default function AdminPage() {
  return (
    <AdminRouteProtector>
      {/* The react-admin UI component */}
      <AdminUI />
    </AdminRouteProtector>
  );
}
