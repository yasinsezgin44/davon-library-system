"use client";

import { useState, useEffect } from "react";
import apiClient from "../../lib/apiClient";
import { useAuth } from "../../context/AuthContext";

interface Reservation {
  id: number;
  bookTitle: string;
  userName: string;
  reservationDate: string;
  status: string;
}

const ReservationsOverview = () => {
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const { isAuthReady } = useAuth();

  useEffect(() => {
    if (!isAuthReady) return;
    const fetchReservations = async () => {
      try {
        const response = await apiClient.get("/reservations");
        setReservations(response.data);
      } catch (error) {
        console.error("Failed to fetch reservations:", error);
      }
    };
    fetchReservations();
  }, [isAuthReady]);

  return (
    <div className="bg-white shadow-md rounded-lg overflow-hidden">
      <div className="flex justify-between items-center mb-4 px-6 py-4">
        <h2 className="text-2xl font-bold text-gray-800">
          Reservations Overview
        </h2>
      </div>
      <div className="overflow-x-auto">
        <table className="min-w-full leading-normal">
          <thead>
            <tr>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                Book Title
              </th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                User Name
              </th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                Reservation Date
              </th>
              <th className="px-5 py-3 border-b-2 border-gray-200 bg-gray-100 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                Status
              </th>
            </tr>
          </thead>
          <tbody>
            {reservations.map((reservation) => (
              <tr key={reservation.id}>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  {reservation.bookTitle}
                </td>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  {reservation.userName}
                </td>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  {new Date(reservation.reservationDate).toLocaleDateString()}
                </td>
                <td className="px-5 py-5 border-b border-gray-200 bg-white text-sm">
                  {reservation.status}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default ReservationsOverview;
