import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import { AuthProvider } from "@/context/AuthContext";
import { cookies } from "next/headers";
import { jwtVerify, importSPKI } from "jose";
import Navbar from "@/components/Navbar";
import Footer from "@/components/Footer";

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "Davon Library",
  description: "A modern library management system.",
};

async function getUser() {
  const cookieStore = await cookies();
  const token = cookieStore.get("token")?.value;
  if (!token) return null;

  const publicKey = `-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA72eygT6HYBeHVfRFXBAH
AgFWGTLNuMWlCcdYdfse/izcLAj/aVu3C/5/cCae4HBBNK2MwaTyhZ+nCpkg2yhi
m5pZB5HVqYKlVARP2Rk0YcKLFJELIpUy7smrrpac1bbgJH/KFuWokigg7+jxzFgg
ubp1hVQbOPT6HgkKlbOAO6HFv5EBQ+BUuYgo2EpcodBgRmzZZi6u1lMWrxMgTP/C
GFj/Ys0V0F4UHFiv1wxjTc7QwfUfKRh6ZI5QLBn/bL5AoH0Mkf0eTymIRTz9wEU5
X0dfznxR35YMGhZNJwMdzUhjDjwYSH9M8kXJNT1EIeSvAS/7uGGHwvtb6XbOY7/A
3QIDAQAB
-----END PUBLIC KEY-----`;

  const rsaPublicKey = await importSPKI(publicKey, "RS256");

  try {
    const { payload } = await jwtVerify(token, rsaPublicKey);
    const roles = payload.groups as string[];
    return {
      username: payload.sub as string,
      roles,
      fullName: payload.fullName as string,
    };
  } catch (e) {
    return null;
  }
}

export default async function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const user = await getUser();

  return (
    <html lang="en">
      <body className={inter.className}>
        <AuthProvider user={user}>
          <div className="flex flex-col min-h-screen">
            <Navbar />
            <main className="flex-grow container mx-auto px-4 sm:px-6 lg:px-8 py-8">
              {children}
            </main>
            <Footer />
          </div>
        </AuthProvider>
      </body>
    </html>
  );
}
