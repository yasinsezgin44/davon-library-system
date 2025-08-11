"use client";

import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  useMemo,
  ReactNode,
} from "react";
import apiClient from "../lib/apiClient";
import { jwtDecode, JwtPayload } from "jwt-decode";

interface User {
  username: string;
  roles: string[];
}

interface AuthContextType {
  user: User | null;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  isAuthReady: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isAuthReady, setIsAuthReady] = useState(false);

  useEffect(() => {
    const token =
      typeof window !== "undefined" ? localStorage.getItem("token") : null;
    if (token) {
      const decoded = jwtDecode<JwtPayload & { groups: string[] }>(token);
      setUser({ username: decoded.sub ?? "", roles: decoded.groups });
    }
    // eslint-disable-next-line no-console
    console.log("[AuthContext] initialized", { hasToken: Boolean(token) });
    setIsAuthReady(true);
  }, []);

  const login = async (username: string, password: string) => {
    const response = await apiClient.post("/auth/login", {
      username,
      password,
    });
    const { token, role } = response.data;
    localStorage.setItem("token", token);
    const decoded = jwtDecode<JwtPayload & { groups: string[] }>(token);
    const roles = decoded.groups || (role ? [role] : []);
    setUser({ username: decoded.sub ?? "", roles });
    // eslint-disable-next-line no-console
    console.log("[AuthContext] login success", { roles });
  };

  const logout = () => {
    localStorage.removeItem("token");
    setUser(null);
    // eslint-disable-next-line no-console
    console.log("[AuthContext] logout");
  };

  const contextValue = useMemo<AuthContextType>(
    () => ({ user, login, logout, isAuthReady }),
    [user, isAuthReady]
  );

  return (
    <AuthContext.Provider value={contextValue}>{children}</AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return ctx;
};
