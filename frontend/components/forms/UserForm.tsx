// frontend/components/forms/UserForm.tsx
"use client";

import { User } from "@/types/user";
import { useForm, SubmitHandler } from "react-hook-form";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { LibraryButton } from "@/components/library-button";

interface UserFormProps {
  readonly onSubmit: SubmitHandler<User>;
  readonly defaultValues?: User;
}

export function UserForm({ onSubmit, defaultValues }: UserFormProps) {
  const { register, handleSubmit } = useForm<User>({ defaultValues });

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
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
        <div>
          <Label htmlFor="username">Username</Label>
          <Input
            id="username"
            type="text"
            {...register("username", { required: true })}
          />
        </div>
        {!defaultValues && (
          <div>
            <Label htmlFor="password">Password</Label>
            <Input
              id="password"
              type="password"
              {...register("password", { required: true })}
            />
          </div>
        )}
      </div>
      <LibraryButton type="submit" className="mt-6">
        Submit
      </LibraryButton>
    </form>
  );
}
