// frontend/components/registration-form.tsx
"use client";

import type React from "react";

import { useState } from "react";
import { Eye, EyeOff, Mail, Lock, ArrowRight, User } from "lucide-react";
import { LibraryButton } from "./library-button";
import { useToastHelpers } from "./toast-notification";
import { cn } from "@/lib/utils";
import Link from "next/link";
import { fetchApi } from "@/lib/api";

export function RegistrationForm() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const { success, error } = useToastHelpers();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      await fetchApi("/users", {
        method: "POST",
        body: JSON.stringify({ name, email, password, role: "MEMBER" }),
      });
      success(
        "Registration successful!",
        "You can now log in with your new account."
      );
      // Redirect logic to login page would go here
    } catch (err) {
      error(
        "Registration failed",
        "An error occurred while trying to register."
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="w-full max-w-md mx-auto">
      {/* Header */}
      <div className="text-center mb-8">
        <h2 className="text-3xl font-bold text-dark-gray mb-2">
          Create Account
        </h2>
        <p className="text-dark-gray/70">Join our digital library today</p>
      </div>

      {/* Registration Form */}
      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Name Field */}
        <div>
          <label
            htmlFor="name"
            className="block text-sm font-medium text-dark-gray mb-2"
          >
            Full Name
          </label>
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <User className="h-5 w-5 text-dark-gray/40" />
            </div>
            <input
              id="name"
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              className={cn(
                "w-full pl-10 pr-4 py-3 rounded-lg border border-gray-300",
                "bg-white/60 backdrop-blur-sm",
                "text-dark-gray placeholder-dark-gray/50",
                "focus:outline-none focus:ring-2 focus:ring-modern-teal focus:border-modern-teal",
                "transition-all duration-200",
                "hover:bg-white/80"
              )}
              placeholder="Enter your full name"
              required
            />
          </div>
        </div>

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

        {/* Sign Up Button */}
        <LibraryButton
          type="submit"
          className="w-full py-3 text-lg font-semibold"
          disabled={isLoading}
        >
          {isLoading ? (
            <div className="flex items-center justify-center space-x-2">
              <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
              <span>Creating account...</span>
            </div>
          ) : (
            <div className="flex items-center justify-center space-x-2">
              <span>Sign Up</span>
              <ArrowRight className="h-5 w-5" />
            </div>
          )}
        </LibraryButton>
      </form>

      <div className="mt-8 text-center">
        <p className="text-dark-gray/70">
          Already have an account?{" "}
          <Link
            href="/login"
            className="text-modern-teal hover:text-modern-teal/80 font-medium transition-colors"
          >
            Sign in here
          </Link>
        </p>
      </div>
    </div>
  );
}
