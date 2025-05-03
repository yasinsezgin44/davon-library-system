"use client";

import React, { useState } from "react";
import styles from "./LoginForm.module.css";

export default function LoginForm() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError(null);
    setIsLoading(true);

    // --- Placeholder for actual login logic ---
    console.log("Login attempt with:", { email, password });
    // Simulate API call
    await new Promise((resolve) => setTimeout(resolve, 1500));
    // Example: Replace with your actual API call
    // try {
    //   const response = await fetch('/api/login', {
    //     method: 'POST',
    //     headers: { 'Content-Type': 'application/json' },
    //     body: JSON.stringify({ email, password }),
    //   });
    //   if (!response.ok) {
    //     const errorData = await response.json();
    //     throw new Error(errorData.message || 'Login failed');
    //   }
    //   // Handle successful login (e.g., redirect, update auth state)
    //   console.log('Login successful');
    // } catch (err: any) {
    //   setError(err.message);
    // } finally {
    //   setIsLoading(false);
    // }
    // --- End Placeholder ---

    // Reset loading state after placeholder simulation
    setIsLoading(false);
    // Example failure
    if (password !== "password") {
      // Dummy check
      setError("Invalid email or password.");
    } else {
      alert("Login Successful! (Placeholder)");
      // Here you would typically close the modal and update global state
      // Example: props.onLoginSuccess(); // Call a function passed from Header to close modal
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
        {/* Optional: Add a "Forgot Password?" link here */}
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
