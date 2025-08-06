// frontend/components/reservations-card.tsx
"use client";

import { LibraryCard } from "./library-card";

export function ReservationsCard() {
  return (
    <LibraryCard className="p-6">
      <h2 className="text-xl font-semibold text-dark-gray mb-4">
        Reservations
      </h2>
      <div className="space-y-2">
        <p className="text-dark-gray">
          "The Midnight Library" - Ready for pickup
        </p>
        <p className="text-dark-gray">"Dune" - Position #3</p>
      </div>
    </LibraryCard>
  );
}
