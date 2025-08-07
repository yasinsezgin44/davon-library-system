// frontend/app/profile/page.tsx
"use client";

import { useEffect, useState } from "react";
import { useForm, SubmitHandler } from "react-hook-form";
import { getMyProfile, updateMyProfile } from "@/lib/api";
import { User } from "@/types/user";
import { useToastHelpers } from "@/components/toast-notification";
import withAuth from "@/components/auth/withAuth";
import { AppLayout } from "@/components/layout/AppLayout";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { LibraryButton } from "@/components/library-button";

function ProfilePage() {
  const [user, setUser] = useState<User | null>(null);
  const { register, handleSubmit, reset } = useForm<User>();
  const { success, error } = useToastHelpers();

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const profileData = await getMyProfile();
        setUser(profileData);
        reset(profileData);
      } catch (err) {
        console.error("Failed to fetch profile:", err);
        error(
          "Failed to load profile",
          "There was a problem fetching your profile data."
        );
      }
    };
    fetchProfile();
  }, [reset, error]);

  const handleUpdateProfile: SubmitHandler<User> = async (data) => {
    try {
      await updateMyProfile(data);
      success(
        "Profile updated!",
        "Your profile has been successfully updated."
      );
    } catch (err) {
      error("Failed to update profile", "Please try again later.");
    }
  };

  if (!user) {
    return <div>Loading...</div>;
  }

  return (
    <AppLayout>
      <h1 className="text-3xl font-bold text-dark-gray mb-8">
        Profile Settings
      </h1>
      <form onSubmit={handleSubmit(handleUpdateProfile)}>
        <div className="space-y-4">
          <div>
            <Label htmlFor="fullName">Full Name</Label>
            <Input
              id="fullName"
              type="text"
              {...register("fullName", { required: true })}
            />
          </div>
          <div>
            <Label htmlFor="email">Email</Label>
            <Input
              id="email"
              type="email"
              {...register("email", { required: true })}
            />
          </div>
        </div>
        <LibraryButton type="submit" className="mt-6">
          Update Profile
        </LibraryButton>
      </form>
    </AppLayout>
  );
}

export default withAuth(ProfilePage, ["MEMBER", "ADMIN", "LIBRARIAN"]);
