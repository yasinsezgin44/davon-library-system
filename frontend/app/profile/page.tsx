"use client";

import { useAuth } from "@/context/AuthContext";
import { useEffect } from "react";

const ProfilePage = () => {
  const { user, isAuthenticated, login } = useAuth();

  // Mock user data for demonstration
  const mockUser = {
    id: "1",
    name: "Yasin Sezgin",
    email: "yasin.s@example.com",
    roles: ["Member", "Admin"],
  };

  // Simulate login for demonstration purposes
  const handleLogin = () => {
    login(mockUser);
  };

  if (!isAuthenticated) {
    return (
      <div className="text-center py-10">
        <p>Please log in to view your profile.</p>
        <button
          onClick={handleLogin}
          className="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
        >
          Simulate Login
        </button>
      </div>
    );
  }

  return (
    <div className="container mx-auto py-10">
      <h1 className="text-3xl font-bold mb-6">Profile</h1>
      <div className="bg-white shadow-md rounded-lg p-6">
        <div className="mb-4">
          <strong className="font-semibold">Name:</strong> {user?.name}
        </div>
        <div className="mb-4">
          <strong className="font-semibold">Email:</strong> {user?.email}
        </div>
        <div>
          <strong className="font-semibold">Roles:</strong>{" "}
          {user?.roles.join(", ")}
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
