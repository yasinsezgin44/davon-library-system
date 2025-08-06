// frontend/components/login-form.tsx
'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { Eye, EyeOff, User, Lock, ArrowRight } from 'lucide-react';
import { LibraryButton } from './library-button';
import { useToastHelpers } from './toast-notification';
import { cn } from '@/lib/utils';
import Link from 'next/link';
import { login } from '@/lib/auth';

interface LoginFormProps {
  redirectTo: string;
  title: string;
}

export function LoginForm({ redirectTo, title }: LoginFormProps) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const { success, error } = useToastHelpers();
  const router = useRouter();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      const { token } = await login(username, password);
      localStorage.setItem('token', token);
      success('Welcome back!', 'You have successfully logged in.');
      router.push(redirectTo);
    } catch (err) {
      error('Login failed', 'Please check your credentials and try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="w-full max-w-md mx-auto">
      <div className="text-center mb-8">
        <h2 className="text-3xl font-bold text-dark-gray mb-2">{title}</h2>
        <p className="text-dark-gray/70">Sign in to access your digital library</p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        <div>
          <label
            htmlFor="username"
            className="block text-sm font-medium text-dark-gray mb-2"
          >
            Username
          </label>
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <User className="h-5 w-5 text-dark-gray/40" />
            </div>
            <input
              id="username"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className={cn(
                "w-full pl-10 pr-4 py-3 rounded-lg border border-gray-300",
                "focus:outline-none focus:ring-2 focus:ring-modern-teal"
              )}
              placeholder="Enter your username"
              required
            />
          </div>
        </div>
        
        <div>
          <label
            htmlFor="password"
            className="block text-sm font-medium text-dark-gray mb-2"
          >
            Password
          </label>
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <Lock className="h-5 w-5 text-dark-gray/40" />
            </div>
            <input
              id="password"
              type={showPassword ? "text" : "password"}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className={cn(
                "w-full pl-10 pr-12 py-3 rounded-lg border border-gray-300",
                "focus:outline-none focus:ring-2 focus:ring-modern-teal"
              )}
              placeholder="Enter your password"
              required
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute inset-y-0 right-0 pr-3 flex items-center text-dark-gray/40 hover:text-dark-gray"
            >
              {showPassword ? <EyeOff className="h-5 w-5" /> : <Eye className="h-5 w-5" />}
            </button>
          </div>
        </div>
        
        <LibraryButton
          type="submit"
          className="w-full py-3 text-lg font-semibold"
          disabled={isLoading}
        >
          {isLoading ? 'Signing in...' : 'Sign In'}
        </LibraryButton>
      </form>
    </div>
  );
}
