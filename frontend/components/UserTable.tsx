// frontend/components/UserTable.tsx
"use client";

import { User } from "@/types/user";
import Link from "next/link";
import { LibraryButton } from "./library-button";

interface UserTableProps {
  readonly users: User[];
  readonly onDelete: (id: number) => void;
}

export function UserTable({ users, onDelete }: UserTableProps) {
  return (
    <div>
      <div className="flex justify-end mb-4">
        <Link href="/admin/users/new">
          <LibraryButton>Create User</LibraryButton>
        </Link>
      </div>
      <table className="min-w-full bg-white">
        <thead>
          <tr>
            <th className="py-2">Name</th>
            <th className="py-2">Email</th>
            <th className="py-2">Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.id}>
              <td className="border px-4 py-2">{user.fullName}</td>
              <td className="border px-4 py-2">{user.email}</td>
              <td className="border px-4 py-2">
                <Link
                  href={`/admin/users/${user.id}/edit`}
                  className="text-blue-500 hover:underline mr-4"
                >
                  Edit
                </Link>
                <button
                  onClick={() => onDelete(user.id)}
                  className="text-red-500 hover:underline"
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
