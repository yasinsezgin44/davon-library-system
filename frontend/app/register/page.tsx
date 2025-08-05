// frontend/app/register/page.tsx
import { RegistrationForm } from "@/components/registration-form";

export default function RegisterPage() {
  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100">
      <RegistrationForm />
    </div>
  );
}
