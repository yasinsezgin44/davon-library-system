"use client";

import React, { useState } from "react";
import { useRouter } from "next/navigation";
import styles from "./AuthForm.module.css";
import { useAuth } from "../contexts/AuthContext";

interface LoginFormProps {
  onSuccess?: () => void;
}

export default function LoginForm({ onSuccess }: LoginFormProps) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errors, setErrors] = useState<{
    email?: string;
    password?: string;
    form?: string;
  }>({});
  const [isLoading, setIsLoading] = useState(false);
  const router = useRouter();
  const { login } = useAuth();

  // Validate form fields
  const validateForm = (): boolean => {
    const newErrors: {
      email?: string;
      password?: string;
    } = {};

    // Email validation
    if (!email) {
      newErrors.email = "Email is required";
    } else if (!/\S+@\S+\.\S+/.test(email)) {
      newErrors.email = "Please enter a valid email address";
    }

    // Password validation
    if (!password) {
      newErrors.password = "Password is required";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // Handle field change with real-time validation
  const handleEmailChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setEmail(value);

    // Clear error when user starts typing
    if (errors.email) {
      setErrors((prev) => ({ ...prev, email: undefined }));
    }
  };

  const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setPassword(value);

    // Clear error when user starts typing
    if (errors.password) {
      setErrors((prev) => ({ ...prev, password: undefined }));
    }
  };

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setErrors({});
    setIsLoading(true);

    // Validate form before submission
    if (!validateForm()) {
      setIsLoading(false);
      return;
    }

    try {
      // Use AuthContext login for proper state update
      const user = await login(email, password);

      // Call onSuccess to close modal BEFORE redirecting
      onSuccess?.();

      // Redirect based on role
      if (user.role === "Admin") {
        router.push("/admin");
      } else {
        router.push("/");
      }
    } catch (err: any) {
      setErrors({ form: err.message || "Login failed" });
      console.error("Login error:", err);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className={styles.form}>
      {errors.form && <p className={styles.errorMessage}>{errors.form}</p>}
      <div className={styles.formGroup}>
        <label htmlFor="login-email" className={styles.label}>
          Email Address
        </label>
        <input
          type="email"
          id="login-email"
          value={email}
          onChange={handleEmailChange}
          required
          className={`${styles.input} ${errors.email ? styles.inputError : ""}`}
          autoComplete="email"
        />
        {errors.email && <p className={styles.fieldError}>{errors.email}</p>}
      </div>
      <div className={styles.formGroup}>
        <label htmlFor="login-password" className={styles.label}>
          Password
        </label>
        <input
          type="password"
          id="login-password"
          value={password}
          onChange={handlePasswordChange}
          required
          className={`${styles.input} ${
            errors.password ? styles.inputError : ""
          }`}
          autoComplete="current-password"
        />
        {errors.password && (
          <p className={styles.fieldError}>{errors.password}</p>
        )}
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
