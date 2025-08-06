// frontend/app/login/page.tsx
'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { LibraryCard } from "@/components/library-card";
import { LibraryButton } from "@/components/library-button";
import { LibraryHeader } from "@/components/library-header";
import { LibraryFooter } from "@/components/library-footer";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { login } from '../lib/auth'; // Will create this auth library

export default function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const router = useRouter();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    try {
      const { token } = await login(username, password);
      localStorage.setItem('token', token);
      router.push('/admin/dashboard');
    } catch (err) {
      setError('Failed to login. Please check your credentials.');
    }
  };

  return (
    <div className="min-h-screen bg-clean-white">
      <LibraryHeader />
      <main className="flex items-center justify-center p-4 lg:p-8">
        <LibraryCard className="w-full max-w-md p-8">
          <h2 className="text-2xl font-bold text-dark-gray mb-4 text-center">Admin Login</h2>
          <form onSubmit={handleLogin}>
            <div className="space-y-4">
              <div>
                <Label htmlFor="username">Username</Label>
                <Input
                  id="username"
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                />
              </div>
              <div>
                <Label htmlFor="password">Password</Label>
                <Input
                  id="password"
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
              </div>
            </div>
            {error && <p className="text-red-500 text-sm mt-4">{error}</p>}
            <LibraryButton type="submit" className="w-full mt-6">
              Login
            </LibraryButton>
          </form>
        </LibraryCard>
      </main>
      <LibraryFooter />
    </div>
  );
}
