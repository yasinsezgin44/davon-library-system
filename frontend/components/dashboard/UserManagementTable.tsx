"use client";

import { useState, useEffect } from "react";
import apiClient from "../../lib/apiClient";
import { useAuth } from "../../context/AuthContext";

type UserRow = {
  id: number;
  fullName: string;
  email: string;
  roles: { name: string }[];
};

const UserManagementTable = () => {
  const [users, setUsers] = useState<UserRow[]>([]);
  const { isAuthReady, user } = useAuth();
  // Add states for modals: const [isCreateModalOpen, setCreateModalOpen] = useState(false); etc.

  useEffect(() => {
    if (!isAuthReady) return;
    if (!user || !user.roles.includes("ADMIN")) return;
    const fetchUsers = async () => {
      try {
        const response = await apiClient.get("/admin/users");
        setUsers(response.data);
      } catch (error) {
        console.error("Failed to fetch users:", error);
      }
    };
    fetchUsers();
  }, [isAuthReady, user]);

  const handleCreate = async (userData: Partial<UserRow>) => {
    try {
      const response = await apiClient.post("/admin/users", userData);
      setUsers([...users, response.data]);
    } catch (error) {
      console.error("Failed to create user:", error);
    }
  };

  const handleUpdate = async (id: number, userData: Partial<UserRow>) => {
    try {
      const response = await apiClient.put(`/users/${id}`, userData);
      setUsers(users.map((user) => (user.id === id ? response.data : user)));
    } catch (error) {
      console.error("Failed to update user:", error);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await apiClient.delete(`/admin/users/${id}`);
      setUsers(users.filter((user) => user.id !== id));
    } catch (error) {
      console.error("Failed to delete user:", error);
    }
  };

  return (
    <div className="bg-white shadow-md rounded-lg overflow-hidden">
      <div className="flex justify-between items-center mb-4 px-6 py-4">
        <h2 className="text-2xl font-bold text-gray-800">User Management</h2>
        <button className="px-4 py-2 rounded-md font-semibold text-sm bg-green-500 text-white hover:bg-green-600">
          Add New User
        </button>
      </div>
      <div className="overflow-x-auto">
        <table className="min-w-full leading-normal">
          <thead>
            <tr>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                Name
              </th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                Email
              </th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                Roles
              </th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                Actions
              </th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id}>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  <p className="text-gray-900 whitespace-no-wrap">
                    {user.fullName}
                  </p>
                </td>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  <p className="text-gray-900 whitespace-no-wrap">
                    {user.email}
                  </p>
                </td>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  <p className="text-gray-900 whitespace-no-wrap">
                    {user.roles.map((role) => role.name).join(", ")}
                  </p>
                </td>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  <div className="flex items-center">
                    <button className="px-4 py-2 rounded-md font-semibold text-sm bg-indigo-500 text-white hover:bg-indigo-600 mr-2">
                      Edit
                    </button>
                    <button
                      onClick={() => handleDelete(user.id)}
                      className="px-4 py-2 rounded-md font-semibold text-sm bg-red-500 text-white hover:bg-red-600"
                    >
                      Delete
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {/* Add Modals for Create, Update, Delete here */}
    </div>
  );
};

export default UserManagementTable;
