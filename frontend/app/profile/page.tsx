"use client";

import { useAuth } from "../../context/AuthContext";
import { useEffect, useState } from "react";

interface ProfileRoleString {
  name: string;
}
interface ProfileData {
  fullName: string;
  email: string;
  phoneNumber?: string | null;
  roles: Array<string | ProfileRoleString>;
}

const ProfilePage = () => {
  const { user } = useAuth();
  const [profile, setProfile] = useState<ProfileData | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [isEditing, setIsEditing] = useState<boolean>(false);
  const [saving, setSaving] = useState<boolean>(false);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [form, setForm] = useState<{ fullName: string; phoneNumber: string }>({
    fullName: "",
    phoneNumber: "",
  });

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

        // Use Next.js route to include httpOnly cookie automatically
        const response = await fetch("/api/profile", {
          credentials: "include",
        });
        console.log("[Profile] GET /api/profile response", {
          status: response.status,
        });
        if (!response.ok) {
          const errorText = await response.text();
          throw new Error(errorText || "Failed to load profile");
        }
        const data = await response.json();
        setProfile(data);
        setForm({
          fullName: data?.fullName ?? "",
          phoneNumber: data?.phoneNumber ?? "",
        });
      } catch (err: unknown) {
        console.error("Failed to fetch profile:", err);
        const e = err as {
          response?: { data?: { message?: string } };
          message?: string;
        };
        setError(
          e?.response?.data?.message || e?.message || "Failed to load profile"
        );
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, [user]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const startEditing = () => {
    setSuccessMessage(null);
    setIsEditing(true);
  };

  const cancelEditing = () => {
    if (profile) {
      setForm({
        fullName: profile.fullName ?? "",
        phoneNumber: profile.phoneNumber ?? "",
      });
    }
    setIsEditing(false);
    setSuccessMessage(null);
  };

  const saveProfile = async () => {
    setSaving(true);
    setError(null);
    setSuccessMessage(null);
    try {
      const payload = {
        fullName: form.fullName,
        phoneNumber: form.phoneNumber || null,
      };
      const resp = await fetch("/api/profile", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify(payload),
      });
      if (!resp.ok) {
        const errorText = await resp.text();
        throw new Error(errorText || "Failed to update profile");
      }
      const updated = await resp.json();
      setProfile(updated);
      setIsEditing(false);
      setSuccessMessage("Profile updated successfully.");
    } catch (err: unknown) {
      console.error("Failed to update profile:", err);
      const e = err as {
        response?: { data?: { message?: string } };
        message?: string;
      };
      setError(
        e?.response?.data?.message || e?.message || "Failed to update profile"
      );
    } finally {
      setSaving(false);
    }
  };

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
      <h1 className="text-3xl font-bold mb-6 text-gray-900">Profile</h1>
      <div className="bg-white shadow-md rounded-lg p-6 border border-gray-200 text-gray-900">
        {successMessage && (
          <div className="mb-4 bg-green-50 text-green-700 border border-green-200 rounded p-3">
            {successMessage}
          </div>
        )}

        <div className="mb-4">
          <label
            htmlFor="fullName"
            className="block text-sm font-medium text-gray-900 mb-1"
          >
            Full name
          </label>
          {isEditing ? (
            <input
              type="text"
              name="fullName"
              id="fullName"
              value={form.fullName}
              onChange={handleChange}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              placeholder="Your full name"
            />
          ) : (
            <div>
              <strong className="font-semibold">Name:</strong>{" "}
              {profile.fullName}
            </div>
          )}
        </div>

        <div className="mb-4">
          <label
            htmlFor="email"
            className="block text-sm font-medium text-gray-900 mb-1"
          >
            Email
          </label>
          <div className="text-gray-900">{profile.email}</div>
          <p className="text-xs text-gray-500">Email cannot be changed.</p>
        </div>

        <div className="mb-4">
          <label
            htmlFor="phoneNumber"
            className="block text-sm font-medium text-gray-900 mb-1"
          >
            Phone number
          </label>
          {isEditing ? (
            <input
              type="tel"
              name="phoneNumber"
              id="phoneNumber"
              value={form.phoneNumber}
              onChange={handleChange}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              placeholder="Optional"
            />
          ) : (
            <div>
              <strong className="font-semibold">Phone:</strong>{" "}
              {profile.phoneNumber || "-"}
            </div>
          )}
        </div>

        <div className="mb-6">
          <strong className="font-semibold">Roles:</strong>{" "}
          {Array.isArray(profile.roles)
            ? profile.roles
                .map((role) => (typeof role === "string" ? role : role?.name))
                .filter(Boolean)
                .join(", ")
            : ""}
        </div>

        {!isEditing ? (
          <button
            onClick={startEditing}
            className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none"
          >
            Edit profile
          </button>
        ) : (
          <div className="flex gap-3">
            <button
              onClick={saveProfile}
              disabled={saving || !form.fullName.trim()}
              className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-green-600 hover:bg-green-700 disabled:opacity-50 focus:outline-none"
            >
              {saving ? "Saving..." : "Save changes"}
            </button>
            <button
              onClick={cancelEditing}
              disabled={saving}
              className="inline-flex items-center px-4 py-2 border text-sm font-medium rounded-md shadow-sm bg-white text-gray-700 border-gray-300 hover:bg-gray-50 focus:outline-none"
            >
              Cancel
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProfilePage;
