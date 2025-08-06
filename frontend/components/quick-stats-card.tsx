// frontend/components/quick-stats-card.tsx
"use client";

import { LibraryCard } from "./library-card";

export function QuickStatsCard() {
  return (
    <LibraryCard className="p-6">
      <h2 className="text-xl font-semibold text-dark-gray mb-4">Quick Stats</h2>
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-center">
        <div>
          <div className="text-2xl font-bold text-dark-gray">12</div>
          <p className="text-sm text-dark-gray/70">Books Read</p>
        </div>
        <div>
          <div className="text-2xl font-bold text-dark-gray">5</div>
          <p className="text-sm text-dark-gray/70">Fines Paid</p>
        </div>
        <div>
          <div className="text-2xl font-bold text-dark-gray">2</div>
          <p className="text-sm text-dark-gray/70">Active Loans</p>
        </div>
        <div>
          <div className="text-2xl font-bold text-dark-gray">1</div>
          <p className="text-sm text-dark-gray/70">Wishlisted</p>
        </div>
      </div>
    </LibraryCard>
  );
}
