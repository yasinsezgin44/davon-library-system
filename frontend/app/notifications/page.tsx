// frontend/app/notifications/page.tsx
"use client";

import { useEffect, useState } from "react";
import { getNotifications } from "@/lib/api";
import { Notification } from "@/types/notification";
import withAuth from "@/components/auth/withAuth";
import { AppLayout } from "@/components/layout/AppLayout";

function NotificationsPage() {
  const [notifications, setNotifications] = useState<Notification[]>([]);

  useEffect(() => {
    const fetchNotifications = async () => {
      try {
        const notificationsData = await getNotifications();
        setNotifications(notificationsData);
      } catch (error) {
        console.error("Failed to fetch notifications:", error);
      }
    };
    fetchNotifications();
  }, []);

  return (
    <AppLayout>
      <h1 className="text-3xl font-bold text-dark-gray mb-8">Notifications</h1>
      <div className="space-y-4">
        {notifications.map((notification) => (
          <div
            key={notification.id}
            className={`p-4 rounded-md ${
              notification.read ? "bg-gray-100" : "bg-blue-100"
            }`}
          >
            <p>{notification.message}</p>
            <p className="text-sm text-gray-500">
              {new Date(notification.createdAt).toLocaleString()}
            </p>
          </div>
        ))}
      </div>
    </AppLayout>
  );
}

export default withAuth(NotificationsPage, ["MEMBER", "ADMIN", "LIBRARIAN"]);
