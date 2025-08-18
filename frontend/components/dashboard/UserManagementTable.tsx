"use client";

import { useState, useEffect } from "react";
import apiClient from "../../lib/apiClient";
import CreateUserModal from "./CreateUserModal";
import DeleteConfirmationModal from "./DeleteConfirmationModal";

type Role = {
  id: number;
  name: string;
  description: string;
};

type UserRow = {
  id: number;
  fullName: string;
  email: string;
  phoneNumber: string;
  active: boolean;
  status: string;
  roles: Role[];
};

const UserManagementTable = () => {
  const [users, setUsers] = useState<UserRow[]>([]);
  const [loading, setLoading] = useState(true);
  const [isCreateModalOpen, setCreateModalOpen] = useState(false);
  const [isDeleteModalOpen, setDeleteModalOpen] = useState(false);
  const [selectedUser, setSelectedUser] = useState<UserRow | null>(null);

  useEffect(() => {
    const fetchUsers = async () => {
      setLoading(true);
      try {
        const response = await apiClient.get("/users");
        setUsers(response.data);
      } catch (error) {
        console.error("Failed to fetch users:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchUsers();
  }, []);

  const handleCreate = async (
    userData: Omit<UserRow, "id" | "roles"> & {
      password: string;
      username: string;
      phoneNumber: string;
      active: boolean;
      status: string;
      roleIds: number[];
    }
  ) => {
    try {
      const response = await apiClient.post("/users", userData);
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
      await apiClient.delete(`/users/${id}`);
      setUsers(users.filter((user) => user.id !== id));
      setDeleteModalOpen(false);
      setSelectedUser(null);
    } catch (error) {
      console.error("Failed to delete user:", error);
    }
  };

  const openDeleteModal = (user: UserRow) => {
    setSelectedUser(user);
    setDeleteModalOpen(true);
  };

  if (loading) {
    return (
      <div className="text-center py-10">
        <p>Loading users...</p>
      </div>
    );
  }

  return (
    <div className="bg-white shadow-md rounded-lg overflow-hidden">
      <div className="flex justify-between items-center mb-4 px-6 py-4">
        <h2 className="text-2xl font-bold text-gray-800">User Management</h2>
        <button
          onClick={() => setCreateModalOpen(true)}
          className="px-4 py-2 rounded-md font-semibold text-sm bg-green-500 text-white hover:bg-green-600"
        >
          Add New User
        </button>
      </div>
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Name
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Email
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Roles
              </th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                Actions
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {users.map((user) => (
              <tr key={user.id}>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                  {user.fullName}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {user.email}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {user.roles.map((role) => role.name).join(", ")}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <div className="flex justify-end items-center space-x-2">
                    <button className="px-4 py-2 rounded-md font-semibold text-sm bg-indigo-500 text-white hover:bg-indigo-600">
                      Edit
                    </button>
                    <button
                      onClick={() => openDeleteModal(user)}
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
      <CreateUserModal
        isOpen={isCreateModalOpen}
        onClose={() => setCreateModalOpen(false)}
        onCreate={handleCreate}
      />
      <DeleteConfirmationModal
        isOpen={isDeleteModalOpen}
        onClose={() => {
          setDeleteModalOpen(false);
          setSelectedUser(null);
        }}
        onConfirm={() => {
          if (selectedUser) {
            handleDelete(selectedUser.id);
          }
        }}
        itemName={selectedUser ? selectedUser.fullName : ""}
      />
    </div>
  );
};

export default UserManagementTable;
