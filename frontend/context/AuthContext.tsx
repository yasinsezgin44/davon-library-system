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
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    const token =
      typeof window !== "undefined" ? localStorage.getItem("token") : null;
    if (token) {
      apiClient.defaults.headers.common["Authorization"] = `Bearer ${token}`;
      const decoded = jwtDecode<JwtPayload & { groups: string[] }>(token);
      setUser({ username: decoded.sub ?? "", roles: decoded.groups });
    }
  }, []);

  const login = async (username: string, password: string) => {
    const response = await apiClient.post("/auth/login", {
      username,
      password,
    });
    const { token, role } = response.data;
    localStorage.setItem("token", token);
    apiClient.defaults.headers.common["Authorization"] = `Bearer ${token}`;
    const decoded = jwtDecode<JwtPayload & { groups: string[] }>(token);
    const roles = decoded.groups || (role ? [role] : []);
    setUser({ username: decoded.sub ?? "", roles });
  };

  const logout = () => {
    localStorage.removeItem("token");
    delete apiClient.defaults.headers.common["Authorization"];
    setUser(null);
  };

  const contextValue = useMemo<AuthContextType>(
    () => ({ user, login, logout }),
    [user]
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
