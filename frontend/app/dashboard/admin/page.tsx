"use client";

import { useAuth } from "../../../context/AuthContext";
import BookManagementTable from "../../../components/dashboard/BookManagementTable";
import UserManagementTable from "../../../components/dashboard/UserManagementTable";

const AdminDashboardPage = () => {
  const { user } = useAuth();

  if (!user || !user.roles.includes("ADMIN")) {
    return (
      <div className="text-center py-10">
        <p>You must be a logged-in admin to view this page.</p>
      </div>
    );
  }

  return (
    <div className="container mx-auto py-10">
      <h1 className="text-3xl font-bold mb-6">Admin Dashboard</h1>
      <p className="mb-8">Welcome, {user.username}!</p>

      <BookManagementTable />
      <UserManagementTable />
    </div>
  );
};

export default AdminDashboardPage;
