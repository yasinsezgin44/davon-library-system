"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
// AuthContext is no longer used here
import styles from "./AuthForm.module.css";
import { useAuth } from "../contexts/AuthContext";

interface RegistrationFormProps {
  onSuccess?: () => void;
}

export default function RegistrationForm({ onSuccess }: RegistrationFormProps) {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errors, setErrors] = useState<{
    name?: string;
    email?: string;
    password?: string;
    form?: string;
  }>({});
  const [isLoading, setIsLoading] = useState(false);
  const router = useRouter();
  const { register } = useAuth();

  // Validate form fields
  const validateForm = (): boolean => {
    const newErrors: {
      name?: string;
      email?: string;
      password?: string;
    } = {};

    // Name validation
    if (!name.trim()) {
      newErrors.name = "Name is required";
    }

    // Email validation
    if (!email) {
      newErrors.email = "Email is required";
    } else if (!/\S+@\S+\.\S+/.test(email)) {
      newErrors.email = "Please enter a valid email address";
    }

    // Password validation
    if (!password) {
      newErrors.password = "Password is required";
    } else if (password.length < 6) {
      newErrors.password = "Password must be at least 6 characters long";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // Handle field changes with real-time validation
  const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setName(value);

    // Clear error when user starts typing
    if (errors.name) {
      setErrors((prev) => ({ ...prev, name: undefined }));
    }
  };

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

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrors({});
    setIsLoading(true);

    // Validate form before submission
    if (!validateForm()) {
      setIsLoading(false);
      return;
    }

    try {
      // Register via AuthContext, which sets user and localStorage
      await register(name, email, password);

      // Call onSuccess to close modal BEFORE redirecting
      onSuccess?.();

      router.push("/"); // Redirect to home after registration
    } catch (err: any) {
      // Check for specific error messages
      if (err.message?.toLowerCase().includes("email already registered")) {
        setErrors({ email: "Email is already registered" });
      } else {
        setErrors({ form: err.message || "Registration failed" });
      }
      console.error("Registration error:", err);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className={styles.form}>
      {errors.form && <p className={styles.errorMessage}>{errors.form}</p>}
      <div className={styles.formGroup}>
        <label htmlFor="reg-name" className={styles.label}>
          Name
        </label>
        <input
          id="reg-name"
          type="text"
          value={name}
          onChange={handleNameChange}
          required
          className={`${styles.input} ${errors.name ? styles.inputError : ""}`}
        />
        {errors.name && <p className={styles.fieldError}>{errors.name}</p>}
      </div>
      <div className={styles.formGroup}>
        <label htmlFor="reg-email" className={styles.label}>
          Email
        </label>
        <input
          id="reg-email"
          type="email"
          value={email}
          onChange={handleEmailChange}
          required
          className={`${styles.input} ${errors.email ? styles.inputError : ""}`}
        />
        {errors.email && <p className={styles.fieldError}>{errors.email}</p>}
      </div>
      <div className={styles.formGroup}>
        <label htmlFor="reg-password" className={styles.label}>
          Password
        </label>
        <input
          id="reg-password"
          type="password"
          value={password}
          onChange={handlePasswordChange}
          required
          className={`${styles.input} ${
            errors.password ? styles.inputError : ""
          }`}
        />
        {errors.password && (
          <p className={styles.fieldError}>{errors.password}</p>
        )}
      </div>
      <button
        type="submit"
        disabled={isLoading}
        className={styles.submitButton}
      >
        {isLoading ? "Registering…" : "Register"}
      </button>
    </form>
  );
}
