"use client";

import { useAuth } from "../../../context/AuthContext";
import BookManagementTable from "../../../components/dashboard/BookManagementTable";
import AuthorManagementTable from "../../../components/dashboard/AuthorManagementTable";
import { useEffect, useState } from "react";

const LibrarianDashboardPage = () => {
  const { user } = useAuth();

  if (!user || !user.roles.includes("LIBRARIAN")) {
    return (
      <div className="text-center py-10">
        <p>You must be a logged-in librarian to view this page.</p>
      </div>
    );
  }

  type ReservationRow = {
    id: number;
    book?: { title?: string };
    member?: { fullName?: string; user?: { fullName?: string } };
    priorityNumber?: number;
    status?: string;
  };

  const ReservationsTable = () => {
    const [reservations, setReservations] = useState<ReservationRow[]>([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
      const fetchReservations = async () => {
        setLoading(true);
        try {
          const resp = await fetch("/api/reservations?scope=admin", {
            cache: "no-store",
          });
          if (resp.ok) {
            setReservations(await resp.json());
          }
        } finally {
          setLoading(false);
        }
      };
      fetchReservations();
    }, []);

    const refresh = async () => {
      setLoading(true);
      try {
        const resp = await fetch("/api/reservations?scope=admin", {
          cache: "no-store",
        });
        if (resp.ok) setReservations(await resp.json());
      } finally {
        setLoading(false);
      }
    };

    const deleteReservation = async (id: number) => {
      const resp = await fetch(`/api/reservations/${id}`, { method: "DELETE" });
      if (resp.ok || resp.status === 204) await refresh();
    };

    const promoteReservation = async (id: number) => {
      const resp = await fetch(`/api/reservations/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ status: "READY_FOR_PICKUP" }),
      });
      if (resp.ok || resp.status === 204) await refresh();
    };

    if (loading) return <div className="p-4">Loading reservations...</div>;

    return (
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Book
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Member
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Queued
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Status
              </th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                Actions
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {reservations.map((r) => (
              <tr key={r.id}>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {r?.book?.title || "-"}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {r?.member?.fullName || r?.member?.user?.fullName || "-"}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {r?.priorityNumber ?? "-"}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {r?.status}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-right text-sm text-gray-900">
                  <div className="space-x-2">
                    <button
                      onClick={() => promoteReservation(r.id)}
                      className="px-3 py-1 rounded bg-indigo-600 text-white"
                    >
                      Mark Ready
                    </button>
                    <button
                      onClick={() => deleteReservation(r.id)}
                      className="px-3 py-1 rounded bg-red-600 text-white"
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
    );
  };

  return (
    <div className="container mx-auto py-10">
      <h1 className="text-3xl font-bold mb-6">Librarian Dashboard</h1>
      <p className="mb-8">Welcome, {user.username}!</p>

      <BookManagementTable />
      <AuthorManagementTable />
      <div className="bg-white shadow-md rounded-lg overflow-hidden">
        <div className="flex justify-between items-center mb-4 px-6 py-4">
          <h2 className="text-2xl font-bold text-gray-800">Reservations</h2>
        </div>
        <ReservationsTable />
      </div>
    </div>
  );
};

export default LibrarianDashboardPage;
