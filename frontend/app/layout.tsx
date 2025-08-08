import type { Metadata } from "next";
import { Geist_Sans } from "geist/font/sans";
import "./globals.css";
import Layout from "@/components/Layout";
import { AuthProvider } from "@/context/AuthContext";

export const metadata: Metadata = {
  title: "Davon Library",
  description: "A modern library management system.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className={Geist_Sans.className}>
        <AuthProvider>
          <Layout>{children}</Layout>
        </AuthProvider>
      </body>
    </html>
  );
}
