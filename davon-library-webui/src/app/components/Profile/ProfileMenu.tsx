"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "./../contexts/AuthContext";
import styles from "./ProfileMenu.module.css";

export default function ProfileMenu() {
  const { user, logout } = useAuth();
  const router = useRouter();

  const handleLogout = () => {
    logout();
    router.push("/"); // Redirect to homepage after logout
  };

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
          <button onClick={handleLogout}>Log out</button>
        </li>
      </ul>
    </div>
  );
}
