"use client";

import {
  createContext,
  useContext,
  useState,
  useEffect,
  ReactNode,
  useMemo,
} from "react";
import axios from "axios";

interface User {
  id: string;
  name: string;
  email: string;
  // …any other fields
}

interface AuthContextType {
  user: User | null;
  login: (email: string, password: string) => Promise<void>;
  register: (name: string, email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);

  // On mount, check localStorage (or call /api/me)
  useEffect(() => {
    const saved = localStorage.getItem("user");
    if (saved) setUser(JSON.parse(saved));
  }, []);

  const login = async (email: string, password: string) => {
    try {
      const res = await axios.post("/api/login", { email, password });
      setUser(res.data.user);
      localStorage.setItem("user", JSON.stringify(res.data.user));
    } catch (error: any) {
      const apiMessage = error.response?.data?.message;
      if (axios.isAxiosError(error) && apiMessage) {
        throw new Error(apiMessage);
      } else {
        console.error("Login error:", error);
        throw new Error("An unexpected error occurred during login.");
      }
    }
  };

  const register = async (name: string, email: string, password: string) => {
    try {
      const res = await axios.post("/api/register", { name, email, password });
      setUser(res.data.user);
      localStorage.setItem("user", JSON.stringify(res.data.user));
    } catch (error: any) {
      const apiRegisterMessage = error.response?.data?.message;
      if (axios.isAxiosError(error) && apiRegisterMessage) {
        throw new Error(apiRegisterMessage);
      } else {
        console.error("Registration error:", error);
        throw new Error("An unexpected error occurred during registration.");
      }
    }
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
