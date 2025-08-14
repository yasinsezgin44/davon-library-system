import { useState, useEffect } from "react";
import { Book } from "./BookManagementTable";
import apiClient from "../../lib/apiClient";

interface Reservation {
  id: number;
  bookTitle: string;
  userName: string;
  reservationDate: string;
  status: string;
}

interface UpdateReservationModalProps {
  isOpen: boolean;
  onClose: () => void;
  onUpdate: (id: number, data: Partial<Reservation>) => void;
  reservation: Reservation | null;
}

const UpdateReservationModal = ({
  isOpen,
  onClose,
  onUpdate,
  reservation,
}: UpdateReservationModalProps) => {
  const [formData, setFormData] = useState({
    bookTitle: "",
    userName: "",
    reservationDate: "",
    status: "",
  });

  useEffect(() => {
    if (reservation) {
      setFormData({
        bookTitle: reservation.bookTitle,
        userName: reservation.userName,
        reservationDate: new Date(reservation.reservationDate)
          .toISOString()
          .split("T")[0],
        status: reservation.status,
      });
    }
  }, [reservation]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (reservation) {
      onUpdate(reservation.id, formData);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full">
      <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
        <h3 className="text-lg font-medium leading-6 text-gray-900">
          Update Reservation
        </h3>
        <form onSubmit={handleSubmit} className="mt-4 space-y-4">
          <div>
            <label
              htmlFor="status"
              className="block text-sm font-medium text-gray-700"
            >
              Status
            </label>
            <select
              name="status"
              id="status"
              value={formData.status}
              onChange={handleChange}
              className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
            >
              <option>RESERVED</option>
              <option>CANCELLED</option>
              <option>COMPLETED</option>
            </select>
          </div>
          <div className="flex justify-end space-x-2">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 rounded-md font-semibold text-sm bg-gray-300 text-gray-800 hover:bg-gray-400"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 rounded-md font-semibold text-sm bg-indigo-500 text-white hover:bg-indigo-600"
            >
              Update
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default UpdateReservationModal;
