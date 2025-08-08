"use client";

import { useAuth } from "../../context/AuthContext";
import { useEffect, useState } from "react";
import apiClient from "../../lib/apiClient";

const ProfilePage = () => {
  const { user } = useAuth();
  const [profile, setProfile] = useState<any>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchProfile = async () => {
      if (!user) {
        // No user yet; keep loading until auth context initializes
        setLoading(true);
        return;
      }

      setLoading(true);
      setError(null);
      try {
        const token =
          typeof window !== "undefined" ? localStorage.getItem("token") : null;
        // Trace the request and token presence (do not log full token)
        console.log("[Profile] Preparing GET /api/profile", {
          hasToken: Boolean(token),
          tokenPreview: token ? token.substring(0, 12) + "..." : null,
        });

        const response = await apiClient.get("/profile");
        console.log("[Profile] GET /api/profile response", {
          status: response.status,
        });
        setProfile(response.data);
      } catch (err: any) {
        console.error("Failed to fetch profile:", err);
        setError(
          err?.response?.data?.message ||
            err?.message ||
            "Failed to load profile"
        );
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, [user]);

  if (loading) return <p>Loading...</p>;
  if (error)
    return (
      <div className="container mx-auto py-10">
        <h1 className="text-3xl font-bold mb-6">Profile</h1>
        <div className="bg-red-50 text-red-700 border border-red-200 rounded p-4">
          {error}
        </div>
      </div>
    );
  if (!user || !profile) return <p>Loading...</p>;

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
          {Array.isArray(profile.roles)
            ? profile.roles
                .map((role: any) =>
                  typeof role === "string" ? role : role?.name
                )
                .filter(Boolean)
                .join(", ")
            : ""}
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
