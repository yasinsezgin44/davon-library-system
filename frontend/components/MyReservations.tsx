// frontend/components/MyReservations.tsx
"use client";

import { Reservation } from "@/types/reservation";
import { LibraryButton } from "./library-button";

interface MyReservationsProps {
  readonly reservations: Reservation[];
  readonly onCancel: (id: number) => void;
}

export function MyReservations({
  reservations,
  onCancel,
}: MyReservationsProps) {
  return (
    <table className="min-w-full bg-white">
      <thead>
        <tr>
          <th className="py-2">Book</th>
          <th className="py-2">Status</th>
          <th className="py-2">Actions</th>
        </tr>
      </thead>
      <tbody>
        {reservations.map((reservation) => (
          <tr key={reservation.id}>
            <td className="border px-4 py-2">{reservation.book.title}</td>
            <td className="border px-4 py-2">{reservation.status}</td>
            <td className="border px-4 py-2">
              <LibraryButton onClick={() => onCancel(reservation.id)}>
                Cancel
              </LibraryButton>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
