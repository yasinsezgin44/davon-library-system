"use client";

import {
  createContext,
  useContext,
  useState,
  useEffect,
  ReactNode,
  useMemo,
} from "react";

interface User {
  id: string;
  name: string;
  email: string;
  role: string;
  // …any other fields
}

interface AuthContextType {
  user: User | null;
  login: (email: string, password: string) => Promise<User>;
  register: (name: string, email: string, password: string) => Promise<User>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  // Initialize user state from localStorage immediately
  const [user, setUser] = useState<User | null>(() => {
    if (typeof window !== "undefined") {
      const saved = localStorage.getItem("user");
      return saved ? JSON.parse(saved) : null;
    }
    return null;
  });

  const login = async (email: string, password: string) => {
    // authenticate against our users API
    // fetch all users and match credentials
    const res = await fetch("/api/users");
    if (!res.ok) throw new Error("Network error during login");
    const users: User[] = await res.json();
    const found = users.find(
      (u) => u.email === email && (u as any).password === password
    );
    if (!found) throw new Error("Invalid email or password");
    setUser(found);
    localStorage.setItem("user", JSON.stringify(found));
    return found;
  };

  const register = async (name: string, email: string, password: string) => {
    // create new user via our users API
    const res = await fetch("/api/users", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name, email, password, role: "Member" }),
    });
    if (!res.ok) {
      const text = await res.text();
      throw new Error(text || "Registration failed");
    }
    const newUser: User = await res.json();
    setUser(newUser);
    localStorage.setItem("user", JSON.stringify(newUser));
    return newUser;
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem("user");
  };

  // Memoize the context value
  const contextValue = useMemo(
    () => ({
      user,
      login,
      register,
      logout,
    }),
    [user]
  ); // Only recreate value when user state changes

  return (
    <AuthContext.Provider value={contextValue}>{children}</AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be inside AuthProvider");
  return ctx;
}
