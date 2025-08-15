"use client";

import { useAuth } from "../../../context/AuthContext";
import BookManagementTable from "../../../components/dashboard/BookManagementTable";
import ReservationsManagement from "../../../components/dashboard/ReservationsManagement";

const LibrarianDashboardPage = () => {
  const { user } = useAuth();

  if (!user || !user.roles.includes("LIBRARIAN")) {
    return (
      <div className="text-center py-10">
        <p>You must be a logged-in librarian to view this page.</p>
      </div>
    );
  }

  return (
    <div className="container mx-auto py-10">
      <h1 className="text-3xl font-bold mb-6">Librarian Dashboard</h1>
      <p className="mb-8">Welcome, {user.username}!</p>

      <BookManagementTable />
      <ReservationsManagement />
    </div>
  );
};

export default LibrarianDashboardPage;
