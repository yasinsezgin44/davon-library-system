// frontend/components/reading-progress-card.tsx
"use client";

import { LibraryCard } from "./library-card";

export function ReadingProgressCard() {
  return (
    <LibraryCard className="p-6">
      <h2 className="text-xl font-semibold text-dark-gray mb-4">
        Reading Progress
      </h2>
      <div className="text-center">
        <div className="text-4xl font-bold text-modern-teal">78%</div>
        <p className="text-sm text-dark-gray/70">of your yearly reading goal</p>
      </div>
    </LibraryCard>
  );
}
