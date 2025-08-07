// frontend/app/admin/users/new/page.tsx
"use client";

import { useRouter } from "next/navigation";
import { createUser } from "@/lib/api";
import { User } from "@/types/user";
import { UserForm } from "@/components/forms/UserForm";
import { useToastHelpers } from "@/components/toast-notification";
import withAuth from "@/components/auth/withAuth";
import { AppLayout } from "@/components/layout/AppLayout";
import { SubmitHandler } from "react-hook-form";

function CreateUserPage() {
  const router = useRouter();
  const { success, error } = useToastHelpers();

  const handleCreateUser: SubmitHandler<User> = async (data) => {
    try {
      await createUser(data);
      success("User created!", "The new user has been successfully created.");
      router.push("/admin/dashboard");
    } catch (err) {
      error("Failed to create user", "Please try again later.");
    }
  };

  return (
    <AppLayout>
      <h1 className="text-3xl font-bold text-dark-gray mb-8">Create User</h1>
      <UserForm onSubmit={handleCreateUser} />
    </AppLayout>
  );
}

export default withAuth(CreateUserPage, ["ADMIN"]);
