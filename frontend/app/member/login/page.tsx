// frontend/app/member/login/page.tsx
"use client";

import { LibraryHeader } from "@/components/library-header";
import { LibraryFooter } from "@/components/library-footer";
import { LoginForm } from "@/components/login-form";

export default function MemberLoginPage() {
  return (
    <div className="min-h-screen bg-clean-white">
      <LibraryHeader />
      <main className="flex items-center justify-center p-4 lg:p-8">
        <LoginForm redirectTo="/dashboard" title="Member Login" />
      </main>
      <LibraryFooter />
    </div>
  );
}
