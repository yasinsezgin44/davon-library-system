// frontend/app/profile/page.tsx
"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { LibraryCard } from "@/components/library-card";
import { LibraryHeader } from "@/components/library-header";
import { LibrarySidebar } from "@/components/library-sidebar";
import { LibraryFooter } from "@/components/library-footer";
import { LibraryButton } from "@/components/library-button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { getMyProfile, updateMyProfile } from "@/lib/api";
import { User } from "@/types/user";
import { useToastHelpers } from "@/components/toast-notification";

export default function ProfilePage() {
  const [user, setUser] = useState<User | null>(null);
  const [fullName, setFullName] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const router = useRouter();
  const { success, error } = useToastHelpers();

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      router.push("/member/login");
      return;
    }

    const fetchProfile = async () => {
      try {
        const profileData = await getMyProfile();
        setUser(profileData);
        setFullName(profileData.fullName);
        setPhoneNumber(profileData.phoneNumber);
      } catch (error) {
        console.error("Failed to fetch profile data:", error);
      }
    };
    fetchProfile();
  }, [router]);

  const handleUpdate = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await updateMyProfile({ fullName, phoneNumber });
      success(
        "Profile updated!",
        "Your profile has been successfully updated."
      );
    } catch (err) {
      error("Update failed", "Please try again later.");
    }
  };

  if (!user) {
    return <div>Loading...</div>;
  }

  return (
    <div className="min-h-screen bg-clean-white">
      <LibraryHeader />
      <LibrarySidebar />
      <main className="lg:ml-64 p-4 lg:p-8">
        <LibraryCard className="w-full max-w-2xl mx-auto p-8">
          <h1 className="text-3xl font-bold text-dark-gray mb-8">My Profile</h1>
          <form onSubmit={handleUpdate}>
            <div className="space-y-4">
              <div>
                <Label htmlFor="fullName">Full Name</Label>
                <Input
                  id="fullName"
                  type="text"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                />
              </div>
              <div>
                <Label htmlFor="phoneNumber">Phone Number</Label>
                <Input
                  id="phoneNumber"
                  type="text"
                  value={phoneNumber}
                  onChange={(e) => setPhoneNumber(e.target.value)}
                />
              </div>
            </div>
            <LibraryButton type="submit" className="mt-6">
              Update Profile
            </LibraryButton>
          </form>
        </LibraryCard>
      </main>
      <LibraryFooter />
    </div>
  );
}
