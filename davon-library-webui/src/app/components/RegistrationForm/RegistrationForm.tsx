"use client";

import React, { useState } from "react";
import styles from "./RegistrationForm.module.css";

export default function RegistrationForm() {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError(null);
    setIsLoading(true);

    // --- Placeholder for actual registration logic ---
    console.log("Registration attempt with:", { username, email, password });
    // Simulate API call
    await new Promise((resolve) => setTimeout(resolve, 1500));
    // Example: Replace with your actual API call
    // try {
    //   const response = await fetch('/api/register', {
    //     method: 'POST',
    //     headers: { 'Content-Type': 'application/json' },
    //     body: JSON.stringify({ username, email, password }),
    //   });
    //   if (!response.ok) {
    //     const errorData = await response.json();
    //     throw new Error(errorData.message || 'Registration failed');
    //   }
    //   // Handle successful registration (e.g., redirect, update auth state)
    //   console.log('Registration successful');
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
      alert("Registration Successful! (Placeholder)");
      // Here you would typically close the modal and update global state
      // Example: props.onRegistrationSuccess(); // Call a function passed from Header to close modal
    }
  };

  return (
    <form onSubmit={handleSubmit} className={styles.form}>
      {error && <p className={styles.errorMessage}>{error}</p>}
      <div className={styles.formGroup}>
        <label htmlFor="registration-username" className={styles.label}>
          Username
        </label>
        <input
          type="username"
          id="registration-username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
          className={styles.input}
          autoComplete="username"
        />
      </div>
      <div className={styles.formGroup}>
        <label htmlFor="registration-email" className={styles.label}>
          Email Address
        </label>
        <input
          type="email"
          id="registration-email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          className={styles.input}
          autoComplete="email"
        />
      </div>
      <div className={styles.formGroup}>
        <label htmlFor="registration-password" className={styles.label}>
          Password
        </label>
        <input
          type="password"
          id="registration-password"
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
        {isLoading ? "Registering..." : "Register"}
      </button>
    </form>
  );
}
