"use client";

import { useAuth } from "@/context/AuthContext";
import BookManagementTable from "@/components/dashboard/BookManagementTable";
import UserManagementTable from "@/components/dashboard/UserManagementTable";

const AdminDashboardPage = () => {
  const { user, isAuthenticated, login } = useAuth();

  // Mock user data for demonstration
  const mockUser = {
    id: "3",
    name: "Admin User",
    email: "admin@example.com",
    roles: ["Admin"],
  };

  // Simulate login for demonstration purposes
  const handleLogin = () => {
    login(mockUser);
  };

  if (!isAuthenticated || !user?.roles.includes("Admin")) {
    return (
      <div className="text-center py-10">
        <p>You must be a logged-in admin to view this page.</p>
        {!isAuthenticated && (
          <button
            onClick={handleLogin}
            className="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
          >
            Simulate Login as Admin
          </button>
        )}
      </div>
    );
  }

  return (
    <div className="container mx-auto py-10">
      <h1 className="text-3xl font-bold mb-6">Admin Dashboard</h1>
      <p className="mb-8">Welcome, {user.name}!</p>

      <BookManagementTable />
      <UserManagementTable />
    </div>
  );
};

export default AdminDashboardPage;
