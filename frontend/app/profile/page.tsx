"use client";

import { useAuth } from '../../context/AuthContext';
import { useEffect, useState } from "react";
import apiClient from '../../lib/apiClient';

const ProfilePage = () => {
  const { user } = useAuth();
  const [profile, setProfile] = useState(null);

  useEffect(() => {
    const fetchProfile = async () => {
      if (user) {
        try {
          const response = await apiClient.get('/profile');
          setProfile(response.data);
        } catch (error) {
          console.error('Failed to fetch profile:', error);
        }
      }
    };
    fetchProfile();
  }, [user]);

  if (!user) {
    return <p>Loading...</p>;
  }

  if (!profile) {
    return <p>Please log in to view your profile.</p>;
  }

  return (
    <div className="container mx-auto py-10">
      <h1 className="text-3xl font-bold mb-6">Profile</h1>
      <div className="bg-white shadow-md rounded-lg p-6">
        <div className="mb-4">
          <strong className="font-semibold">Name:</strong> {profile.fullName}
        </div>
        <div className="mb-4">
          <strong className="font-semibold">Email:</strong> {profile.email}
        </div>
        <div>
          <strong className="font-semibold">Roles:</strong>{" "}
          {profile.roles.map(role => role.name).join(", ")}
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;


