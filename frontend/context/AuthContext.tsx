"use client";

import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  useMemo,
  useCallback,
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
    // eslint-disable-next-line no-console
    console.log("[AuthContext] Initializing auth state...");
    const token =
      typeof window !== "undefined" ? localStorage.getItem("token") : null;
    if (token) {
      try {
        // eslint-disable-next-line no-console
        console.log("[AuthContext] Token found, decoding...");
        const decoded = jwtDecode<
          JwtPayload & { upn: string; groups: string[] }
        >(token);
        setUser({ username: decoded.upn, roles: decoded.groups });
      } catch (e) {
        // eslint-disable-next-line no-console
        console.error("[AuthContext] Failed to decode token on init", e);
        localStorage.removeItem("token");
      }
    }
    // eslint-disable-next-line no-console
    console.log("[AuthContext] Auth state ready", { hasToken: !!token });
    setIsAuthReady(true);
  }, []);

  const login = useCallback(async (username: string, password: string) => {
    // eslint-disable-next-line no-console
    console.log("[AuthContext] Attempting login...");
    const response = await apiClient.post("/auth/login", {
      username,
      password,
    });
    const { token } = response.data;
    localStorage.setItem("token", token);
    const decoded = jwtDecode<JwtPayload & { upn: string; groups: string[] }>(
      token
    );
    const roles = decoded.groups ?? [];
    setUser({ username: decoded.upn, roles });
    // eslint-disable-next-line no-console
    console.log("[AuthContext] Login successful", { username: decoded.upn });
  }, []);

  const logout = useCallback(() => {
    // eslint-disable-next-line no-console
    console.log("[AuthContext] Logging out...");
    setUser(null);
    localStorage.removeItem("token");
    // eslint-disable-next-line no-console
    console.log("[AuthContext] Logout complete");
  }, []);

  const contextValue = useMemo<AuthContextType>(
    () => ({
      user,
      login,
      logout,
      isAuthReady,
    }),
    [user, login, logout, isAuthReady]
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
