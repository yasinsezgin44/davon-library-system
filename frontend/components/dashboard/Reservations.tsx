import React, { useState, useEffect } from "react";
import apiClient from "../../lib/apiClient";

const Reservations = () => {
  const [reservations, setReservations] = useState([]);

  useEffect(() => {
    const fetchReservations = async () => {
      try {
        const response = await apiClient.get("/dashboard/reservations");
        setReservations(response.data);
      } catch (error) {
        console.error("Failed to fetch reservations:", error);
      }
    };
    fetchReservations();
  }, []);

  return (
    <div className="mt-8">
      <h2 className="text-2xl font-bold mb-4">Reservations</h2>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {reservations.map((reservation) => (
          <div key={reservation.id} className="p-4 border rounded-lg shadow-sm">
            <h3 className="font-bold">{reservation.book.title}</h3>
            <p className="text-sm text-gray-600">
              {reservation.book.authorName}
            </p>
            <p className="text-sm mt-2">
              <strong>Status:</strong> {reservation.status}
            </p>
            <button className="mt-4 w-full bg-red-500 text-white py-2 rounded hover:bg-red-600 transition-colors duration-300">
              Cancel
            </button>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Reservations;
