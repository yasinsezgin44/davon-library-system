import type { Metadata } from "next";
import { Inter } from "next/font/google";
import Header from "./components/Header/Header";
import "./globals.css";

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "Davon Library System",
  description: "Welcome to the Davon Library System Web UI",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <main>{children}</main>
      </body>
    </html>
  );
}
