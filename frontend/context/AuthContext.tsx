"use client";

import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  ReactNode,
} from "react";
import apiClient from "../lib/apiClient.ts";
import { jwtDecode, JwtPayload } from "jwt-decode";

interface User {
  username: string;
  roles: string[];
}

interface AuthContextType {
  user: User | null;
  login: (username, password) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      const decoded = jwtDecode<JwtPayload & { groups: string[] }>(token);
      setUser({ username: decoded.sub ?? "", roles: decoded.groups });
    }
  }, []);

  const login = async (username, password) => {
    const response = await apiClient.post("/auth/login", {
      username,
      password,
    });
    const { token } = response.data;
    localStorage.setItem("token", token);
    apiClient.defaults.headers.common["Authorization"] = `Bearer ${token}`;
    const decoded = jwtDecode<JwtPayload & { groups: string[] }>(token);
    setUser({ username: decoded.sub ?? "", roles: decoded.groups });
  };

  const logout = () => {
    localStorage.removeItem("token");
    delete apiClient.defaults.headers.common["Authorization"];
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
