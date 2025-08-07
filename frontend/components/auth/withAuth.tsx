// frontend/components/auth/withAuth.tsx
"use client";

import { useAuth } from "@/contexts/AuthContext";
import { useRouter } from "next/navigation";
import { useEffect } from "react";

const withAuth = <P extends object>(
  WrappedComponent: React.ComponentType<P>,
  allowedRoles: string[] = []
) => {
  const AuthComponent = (props: P) => {
    const { user, isLoading, token } = useAuth();
    const router = useRouter();

    useEffect(() => {
      if (!isLoading && !token) {
        router.push("/login");
      }
    }, [isLoading, token, router]);

    useEffect(() => {
      if (!isLoading && user && allowedRoles.length > 0) {
        const hasRole = user.roles.some((role) => allowedRoles.includes(role));
        if (!hasRole) {
          router.push("/unauthorized"); // Or some other page
        }
      }
    }, [isLoading, user, allowedRoles, router]);

    if (isLoading || !user) {
      return <div>Loading...</div>; // Or a spinner component
    }

    return <WrappedComponent {...props} />;
  };

  return AuthComponent;
};

export default withAuth;
