"use client";

import { useState, useEffect } from "react";
import apiClient from "../../../lib/apiClient";

interface Reservation {
  id: number;
  bookTitle: string;
  userName: string;
  reservationDate: string;
  status: string;
}

const ReservationsOverview = () => {
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

  return (
    <div className="mt-8">
      <h2 className="text-2xl font-bold mb-4">Reservations Overview</h2>
      <div className="overflow-x-auto">
        <table className="min-w-full bg-white">
          <thead>
            <tr>
              <th className="py-2 px-4 border-b">Book Title</th>
              <th className="py-2 px-4 border-b">User Name</th>
              <th className="py-2 px-4 border-b">Reservation Date</th>
              <th className="py-2 px-4 border-b">Status</th>
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
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default ReservationsOverview;
