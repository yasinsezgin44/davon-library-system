"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
// import { useAuth } from "./../contexts/AuthContext"; // Temporarily disable AuthContext usage for registration
import styles from "./AuthForm.module.css";
import { dataProvider, AdminUser } from "@/app/components/admin/AdminApp"; // Import the provider and type

export default function RegistrationForm() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  // const { register } = useAuth(); // Temporarily disabled
  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setIsLoading(true);
    try {
      // --- Simulate registration using the fake data provider ---
      // 1. Check if email already exists (basic check)
      const { data: existingUsers } = await dataProvider.getList<AdminUser>(
        "users",
        {
          filter: { email: email },
          pagination: { page: 1, perPage: 1 },
          sort: { field: "id", order: "ASC" },
        }
      );

      if (existingUsers.length > 0) {
        throw new Error("Email already registered.");
      }

      // 2. Create the new user object (generate ID - simple approach)
      const { data: allUsers } = await dataProvider.getList<AdminUser>(
        "users",
        {
          filter: {},
          pagination: { page: 1, perPage: 1000 }, // Get all users to find max ID
          sort: { field: "id", order: "DESC" },
        }
      );
      const nextId =
        allUsers.length > 0 ? Math.max(...allUsers.map((u) => u.id)) + 1 : 1;

      const newUser: AdminUser = {
        id: nextId,
        name: name,
        email: email,
        // HASH PASSWORDS in real life! Storing plain text is insecure.
        password: password, // Storing for demo login purposes ONLY
        role: "Member", // Default role
        createdAt: new Date(),
      };

      // 3. Add the user using the dataProvider's create method
      await dataProvider.create<AdminUser>("users", { data: newUser });
      console.log("User added to fake data provider:", newUser);
      // --- End simulation ---

      // await register(name, email, password); // Original AuthContext call (disabled)

      router.push("/login"); // Redirect to login after simulated registration
    } catch (err: any) {
      setError(err.message || "Registration failed");
      console.error("Simulated registration error:", err);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className={styles.form}>
      {error && <p className={styles.errorMessage}>{error}</p>}
      <div className={styles.formGroup}>
        <label htmlFor="reg-name" className={styles.label}>
          Name
        </label>
        <input
          id="reg-name"
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
          className={styles.input}
        />
      </div>
      <div className={styles.formGroup}>
        <label htmlFor="reg-email" className={styles.label}>
          Email
        </label>
        <input
          id="reg-email"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          className={styles.input}
        />
      </div>
      <div className={styles.formGroup}>
        <label htmlFor="reg-password" className={styles.label}>
          Password
        </label>
        <input
          id="reg-password"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          className={styles.input}
        />
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
