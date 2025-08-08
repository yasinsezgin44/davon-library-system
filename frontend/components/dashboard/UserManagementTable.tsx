"use client";

import { useState, useEffect } from "react";
import apiClient from "../../lib/apiClient";

const UserManagementTable = () => {
  const [users, setUsers] = useState([]);
  // Add states for modals: const [isCreateModalOpen, setCreateModalOpen] = useState(false); etc.

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await apiClient.get("/api/admin/users");
        setUsers(response.data);
      } catch (error) {
        console.error("Failed to fetch users:", error);
      }
    };
    fetchUsers();
  }, []);

  const handleCreate = async (userData) => {
    try {
      const response = await apiClient.post("/admin/users", userData);
      setUsers([...users, response.data]);
    } catch (error) {
      console.error("Failed to create user:", error);
    }
  };

  const handleUpdate = async (id, userData) => {
    try {
      const response = await apiClient.put(`/users/${id}`, userData);
      setUsers(users.map((user) => (user.id === id ? response.data : user)));
    } catch (error) {
      console.error("Failed to update user:", error);
    }
  };

  const handleDelete = async (id) => {
    try {
      await apiClient.delete(`/admin/users/${id}`);
      setUsers(users.filter((user) => user.id !== id));
    } catch (error) {
      console.error("Failed to delete user:", error);
    }
  };

  return (
    <div className="mt-8">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-2xl font-bold">User Management</h2>
        <button className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600">
          Add New User
        </button>
      </div>
      <div className="bg-white shadow-md rounded-lg overflow-x-auto">
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
                  <button className="text-indigo-600 hover:text-indigo-900 mr-4">
                    Edit
                  </button>
                  <button
                    onClick={() => handleDelete(user.id)}
                    className="text-red-600 hover:text-red-900"
                  >
                    Delete
                  </button>
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
