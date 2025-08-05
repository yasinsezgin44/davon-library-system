"use client";

import type React from "react";

import { useState } from "react";
import { Eye, EyeOff, Mail, Lock, ArrowRight } from "lucide-react";
import { LibraryButton } from "./library-button";

import { useToastHelpers } from "./toast-notification";
import { cn } from "@/lib/utils";

import Link from "next/link";
import { fetchApi } from "@/lib/api";

export function LoginForm() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const { success, error } = useToastHelpers();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      // This is not a secure way to handle login.
      // We are searching for a user with the given email.
      // A proper authentication system should be implemented.
      const users = await fetchApi(`/users/search?q=${email}`);
      if (users && users.length > 0) {
        // Here we should also check the password, but the backend doesn't support it yet.
        success("Welcome back!", "You have successfully logged in.");
        // Redirect logic would go here
      } else {
        error("Login failed", "Please check your credentials and try again.");
      }
    } catch (err) {
      error("Login failed", "An error occurred while trying to log in.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="w-full max-w-md mx-auto">
      {/* Header */}
      <div className="text-center mb-8">
        <h2 className="text-3xl font-bold text-dark-gray mb-2">Welcome Back</h2>
        <p className="text-dark-gray/70">
          Sign in to access your digital library
        </p>
      </div>

      {/* Login Form */}
      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Email Field */}
        <div>
          <label
            htmlFor="email"
            className="block text-sm font-medium text-dark-gray mb-2"
          >
            Email Address
          </label>
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <Mail className="h-5 w-5 text-dark-gray/40" />
            </div>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className={cn(
                "w-full pl-10 pr-4 py-3 rounded-lg border border-gray-300",
                "bg-white/60 backdrop-blur-sm",
                "text-dark-gray placeholder-dark-gray/50",
                "focus:outline-none focus:ring-2 focus:ring-modern-teal focus:border-modern-teal",
                "transition-all duration-200",
                "hover:bg-white/80"
              )}
              placeholder="Enter your email"
              required
            />
          </div>
        </div>

        {/* Password Field */}
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
                "bg-white/60 backdrop-blur-sm",
                "text-dark-gray placeholder-dark-gray/50",
                "focus:outline-none focus:ring-2 focus:ring-modern-teal focus:border-modern-teal",
                "transition-all duration-200",
                "hover:bg-white/80"
              )}
              placeholder="Enter your password"
              required
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute inset-y-0 right-0 pr-3 flex items-center text-dark-gray/40 hover:text-dark-gray transition-colors"
            >
              {showPassword ? (
                <EyeOff className="h-5 w-5" />
              ) : (
                <Eye className="h-5 w-5" />
              )}
            </button>
          </div>
        </div>

        {/* Remember Me & Forgot Password */}
        <div className="flex items-center justify-between">
          <div className="flex items-center">
            <input
              id="remember-me"
              type="checkbox"
              className="h-4 w-4 text-modern-teal focus:ring-modern-teal border-gray-300 rounded"
            />
            <label
              htmlFor="remember-me"
              className="ml-2 block text-sm text-dark-gray/70"
            >
              Remember me
            </label>
          </div>
          <button
            type="button"
            className="text-sm text-modern-teal hover:text-modern-teal/80 font-medium transition-colors"
          >
            Forgot password?
          </button>
        </div>

        {/* Login Button */}
        <LibraryButton
          type="submit"
          className="w-full py-3 text-lg font-semibold"
          disabled={isLoading}
        >
          {isLoading ? (
            <div className="flex items-center justify-center space-x-2">
              <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
              <span>Signing in...</span>
            </div>
          ) : (
            <div className="flex items-center justify-center space-x-2">
              <span>Sign In</span>
              <ArrowRight className="h-5 w-5" />
            </div>
          )}
        </LibraryButton>
      </form>

      <div className="mt-8 text-center">
        <p className="text-dark-gray/70">
          Don't have an account?{" "}
          <Link
            href="/register"
            className="text-modern-teal hover:text-modern-teal/80 font-medium transition-colors"
          >
            Sign up here
          </Link>
        </p>
      </div>
    </div>
  );
}
