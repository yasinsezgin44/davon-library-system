// frontend/components/layout/AppLayout.tsx
"use client";

import { LibraryHeader } from "@/components/library-header";
import { LibrarySidebar } from "@/components/library-sidebar";
import { LibraryFooter } from "@/components/library-footer";
import { ReactNode } from "react";

interface AppLayoutProps {
  readonly children: ReactNode;
}

export function AppLayout({ children }: AppLayoutProps) {
  return (
    <div className="min-h-screen bg-clean-white">
      <LibraryHeader />
      <LibrarySidebar />
      <main className="lg:ml-64 p-4 lg:p-8">{children}</main>
      <LibraryFooter />
    </div>
  );
}
