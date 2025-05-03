"use client";

import Link from "next/link";
import { useAuth } from "./../contexts/AuthContext";
import styles from "./ProfileMenu.module.css";

export default function ProfileMenu() {
  const { user, logout } = useAuth();

  return (
    <div className={styles.profileMenu}>
      <button className={styles.profileButton}>
        {user?.name || "Profile"}
      </button>
      <ul className={styles.dropdownMenu}>
        <li>
          <Link href="/profile">My Profile</Link>
        </li>
        <li>
          <Link href="/settings">Settings</Link>
        </li>
        <li>
          <button onClick={logout}>Log out</button>
        </li>
      </ul>
    </div>
  );
}
