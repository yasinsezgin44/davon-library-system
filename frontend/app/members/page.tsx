import { LibraryCard } from "@/components/library-card";
import { LibraryButton } from "@/components/library-button";
import { LibrarySidebar } from "@/components/library-sidebar";
import { LibraryHeader } from "@/components/library-header";
import { getUsers } from "@/lib/api";
import Link from "next/link";
import { User } from "@/types/user";

export default async function MembersPage() {
  const users: User[] = await getUsers();

  return (
    <div className="min-h-screen bg-clean-white">
      <LibraryHeader />
      <LibrarySidebar />

      <main className="lg:ml-64 p-4 lg:p-8">
        <div className="max-w-7xl mx-auto">
          <div className="mb-8 pt-32 lg:pt-16">
            <h1 className="text-3xl font-bold text-dark-gray mb-2">Members</h1>
            <p className="text-dark-gray/70">Manage your library's members</p>
          </div>

          <LibraryCard className="p-8">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-semibold text-dark-gray">
                Member Management
              </h2>
              <Link href="/register">
                <LibraryButton>Register New Member</LibraryButton>
              </Link>
            </div>
            <ul className="space-y-4">
              {users.map((user) => (
                <li
                  key={user.id}
                  className="p-4 border rounded-lg hover:bg-gray-50"
                >
                  <Link href={`/members/${user.id}`}>
                    <h3 className="text-lg font-semibold">{user.name}</h3>
                    <p className="text-sm text-gray-600">{user.email}</p>
                  </Link>
                </li>
              ))}
            </ul>
          </LibraryCard>
        </div>
      </main>
    </div>
  );
}
