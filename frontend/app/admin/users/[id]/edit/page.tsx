// frontend/app/admin/users/[id]/edit/page.tsx
"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { getUserById, updateUser } from "@/lib/api";
import { User } from "@/types/user";
import { UserForm } from "@/components/forms/UserForm";
import { useToastHelpers } from "@/components/toast-notification";
import withAuth from "@/components/auth/withAuth";
import { AppLayout } from "@/components/layout/AppLayout";
import { SubmitHandler } from "react-hook-form";

function EditUserPage() {
  const [user, setUser] = useState<User | null>(null);
  const params = useParams();
  const router = useRouter();
  const { success, error } = useToastHelpers();
  const id = Number(params.id);

  useEffect(() => {
    if (id) {
      const fetchUser = async () => {
        try {
          const userData = await getUserById(id);
          setUser(userData);
        } catch (err) {
          console.error("Failed to fetch user:", err);
          error(
            "Failed to load user",
            "There was a problem fetching the user data."
          );
        }
      };
      fetchUser();
    }
  }, [id, error]);

  const handleUpdateUser: SubmitHandler<User> = async (data) => {
    try {
      await updateUser(id, data);
      success("User updated!", "The user has been successfully updated.");
      router.push("/admin/dashboard");
    } catch (err) {
      error("Failed to update user", "Please try again later.");
    }
  };

  if (!user) {
    return <div>Loading...</div>;
  }

  return (
    <AppLayout>
      <h1 className="text-3xl font-bold text-dark-gray mb-8">Edit User</h1>
      <UserForm onSubmit={handleUpdateUser} defaultValues={user} />
    </AppLayout>
  );
}

export default withAuth(EditUserPage, ["ADMIN"]);
