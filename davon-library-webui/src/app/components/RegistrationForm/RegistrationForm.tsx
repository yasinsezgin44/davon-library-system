"use client";

import React, { useState } from "react";
import styles from "./RegistrationForm.module.css";

export default function RegistrationForm() {
  return <div>RegistrationForm</div>;
}

export default function RegistrationForm() {


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

