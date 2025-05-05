"use client";

import React, { useState } from "react";
import { useRouter } from "next/navigation";
import styles from "./AuthForm.module.css";
import { dataProvider, AdminUser } from "@/app/components/admin/AdminApp";

export default function LoginForm() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const router = useRouter();

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError(null);
    setIsLoading(true);

    try {
      const { data: users } = await dataProvider.getList<AdminUser>("users", {
        filter: { email: email },
        pagination: { page: 1, perPage: 1 },
        sort: { field: "id", order: "ASC" },
      });

      if (users.length === 0) {
        throw new Error("Invalid email or password.");
      }

      const user = users[0];

      if (user.password !== password) {
        throw new Error("Invalid email or password.");
      }

      console.log("Simulated login successful:", user);

      localStorage.setItem(
        "user",
        JSON.stringify({
          id: user.id,
          name: user.name,
          email: user.email,
          role: user.role,
        })
      );

      if (user.role === "Admin") {
        router.push("/admin");
      } else {
        router.push("/");
      }
    } catch (err: any) {
      setError(err.message || "Login failed");
      console.error("Simulated login error:", err);
      localStorage.removeItem("user");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className={styles.form}>
      {error && <p className={styles.errorMessage}>{error}</p>}
      <div className={styles.formGroup}>
        <label htmlFor="login-email" className={styles.label}>
          Email Address
        </label>
        <input
          type="email"
          id="login-email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          className={styles.input}
          autoComplete="email"
        />
      </div>
      <div className={styles.formGroup}>
        <label htmlFor="login-password" className={styles.label}>
          Password
        </label>
        <input
          type="password"
          id="login-password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          className={styles.input}
          autoComplete="current-password"
        />
      </div>
      <button
        type="submit"
        className={styles.submitButton}
        disabled={isLoading}
      >
        {isLoading ? "Logging in..." : "Log In"}
      </button>
    </form>
  );
}
