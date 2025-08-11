"use client";

import { useState, useEffect } from "react";
import apiClient from "../../lib/apiClient";

interface Reservation {
  id: number;
  bookTitle: string;
  userName: string;
  reservationDate: string;
  status: string;
}

const ReservationsManagement = () => {
  const [reservations, setReservations] = useState<Reservation[]>([]);

  useEffect(() => {
    const fetchReservations = async () => {
      try {
        const response = await apiClient.get("/reservations");
        setReservations(response.data);
      } catch (error) {
        console.error("Failed to fetch reservations:", error);
      }
    };
    fetchReservations();
  }, []);

  const handleUpdateStatus = async (id: number, status: string) => {
    try {
      await apiClient.put(`/reservations/${id}`, { status });
      setReservations(
        reservations.map((r) => (r.id === id ? { ...r, status } : r))
      );
    } catch (error) {
      console.error("Failed to update reservation status:", error);
    }
  };

  return (
    <div className="mt-8">
      <h2 className="text-2xl font-bold mb-4">Reservations Management</h2>
      <div className="overflow-x-auto">
        <table className="min-w-full bg-white">
          <thead>
            <tr>
              <th className="py-2 px-4 border-b">Book Title</th>
              <th className="py-2 px-4 border-b">User Name</th>
              <th className="py-2 px-4 border-b">Reservation Date</th>
              <th className="py-2 px-4 border-b">Status</th>
              <th className="py-2 px-4 border-b">Actions</th>
            </tr>
          </thead>
          <tbody>
            {reservations.map((reservation) => (
              <tr key={reservation.id}>
                <td className="py-2 px-4 border-b">{reservation.bookTitle}</td>
                <td className="py-2 px-4 border-b">{reservation.userName}</td>
                <td className="py-2 px-4 border-b">
                  {new Date(reservation.reservationDate).toLocaleDateString()}
                </td>
                <td className="py-2 px-4 border-b">{reservation.status}</td>
                <td className="py-2 px-4 border-b">
                  <button
                    onClick={() =>
                      handleUpdateStatus(reservation.id, "COMPLETED")
                    }
                    className="bg-green-500 text-white px-2 py-1 rounded mr-2"
                  >
                    Complete
                  </button>
                  <button
                    onClick={() =>
                      handleUpdateStatus(reservation.id, "CANCELLED")
                    }
                    className="bg-red-500 text-white px-2 py-1 rounded"
                  >
                    Cancel
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default ReservationsManagement;
