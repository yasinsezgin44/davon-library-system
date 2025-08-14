"use client";

import { useState, useEffect } from "react";
import apiClient from "../../lib/apiClient";
import DeleteConfirmationModal from "./DeleteConfirmationModal";
import UpdateReservationModal from "./UpdateReservationModal";

interface Reservation {
  id: number;
  bookTitle: string;
  userName: string;
  reservationDate: string;
  status: string;
}

const ReservationsOverview = () => {
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [loading, setLoading] = useState(true);
  const [isUpdateModalOpen, setUpdateModalOpen] = useState(false);
  const [isDeleteModalOpen, setDeleteModalOpen] = useState(false);
  const [selectedReservation, setSelectedReservation] =
    useState<Reservation | null>(null);

  useEffect(() => {
    const fetchReservations = async () => {
      setLoading(true);
      try {
        const response = await apiClient.get("/reservations");
        setReservations(response.data);
      } catch (error) {
        console.error("Failed to fetch reservations:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchReservations();
  }, []);

  const handleUpdate = async (
    id: number,
    reservationData: Partial<Reservation>
  ) => {
    try {
      const response = await apiClient.put(
        `/reservations/${id}`,
        reservationData
      );
      setReservations(
        reservations.map((r) => (r.id === id ? response.data : r))
      );
      setUpdateModalOpen(false);
      setSelectedReservation(null);
    } catch (error) {
      console.error("Failed to update reservation:", error);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await apiClient.delete(`/reservations/${id}`);
      setReservations(reservations.filter((r) => r.id !== id));
      setDeleteModalOpen(false);
      setSelectedReservation(null);
    } catch (error) {
      console.error("Failed to delete reservation:", error);
    }
  };

  const openUpdateModal = (reservation: Reservation) => {
    setSelectedReservation(reservation);
    setUpdateModalOpen(true);
  };

  const openDeleteModal = (reservation: Reservation) => {
    setSelectedReservation(reservation);
    setDeleteModalOpen(true);
  };

  if (loading) {
    return (
      <div className="text-center py-10">
        <p>Loading reservations...</p>
      </div>
    );
  }

  return (
    <div className="bg-white shadow-md rounded-lg overflow-hidden">
      <div className="flex justify-between items-center mb-4 px-6 py-4">
        <h2 className="text-2xl font-bold text-gray-800">
          Reservations Overview
        </h2>
      </div>
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Book Title
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                User Name
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Reservation Date
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
            {reservations.map((reservation) => (
              <tr key={reservation.id}>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                  {reservation.bookTitle}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {reservation.userName}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {new Date(reservation.reservationDate).toLocaleDateString()}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {reservation.status}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <div className="flex justify-end items-center space-x-2">
                    <button
                      onClick={() => openUpdateModal(reservation)}
                      className="px-4 py-2 rounded-md font-semibold text-sm bg-indigo-500 text-white hover:bg-indigo-600"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => openDeleteModal(reservation)}
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
      <UpdateReservationModal
        isOpen={isUpdateModalOpen}
        onClose={() => {
          setUpdateModalOpen(false);
          setSelectedReservation(null);
        }}
        onUpdate={handleUpdate}
        reservation={selectedReservation}
      />
      <DeleteConfirmationModal
        isOpen={isDeleteModalOpen}
        onClose={() => {
          setDeleteModalOpen(false);
          setSelectedReservation(null);
        }}
        onConfirm={() => {
          if (selectedReservation) {
            handleDelete(selectedReservation.id);
          }
        }}
        itemName={selectedReservation ? selectedReservation.bookTitle : ""}
      />
    </div>
  );
};

export default ReservationsOverview;
