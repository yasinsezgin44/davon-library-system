"use client"; // Needed for hooks like useAuth

import PrivateRoute from "./../components/PrivateRoute"; // Adjust path if needed
import { useAuth } from "./../components/contexts/AuthContext"; // Adjust path if needed
import styles from "./ProfilePage.module.css"; // Create this CSS Module

export default function ProfilePage() {
  const { user } = useAuth(); // Get user data from context

  return (
    <PrivateRoute>
      <div className={styles.profileContainer}>
        <h1 className={styles.profileTitle}>My Profile</h1>
        {user ? ( // Check if user data is available before accessing its properties
          <div className={styles.profileDetails}>
            <p>
              <strong>Name:</strong> {user.name}
            </p>
            <p>
              <strong>Email:</strong> {user.email}
            </p>
            <p>
              <strong>User ID:</strong> {user.id}
            </p>
            {/* Add other profile information or actions here */}
            {/* Example: <button>Edit Profile</button> */}
          </div>
        ) : (
          <p>Loading profile information...</p> // Show a loading state
        )}
      </div>
    </PrivateRoute>
  );
}
