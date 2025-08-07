// frontend/app/unauthorized/page.tsx
"use client";

import { useRouter } from "next/navigation";

export default function UnauthorizedPage() {
  const router = useRouter();

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
      <h1 className="text-4xl font-bold text-red-600">Unauthorized</h1>
      <p className="text-lg text-gray-700 mt-4">
        You do not have permission to view this page.
      </p>
      <button
        onClick={() => router.back()}
        className="mt-8 px-4 py-2 text-white bg-blue-600 rounded-md hover:bg-blue-700"
      >
        Go Back
      </button>
    </div>
  );
}
