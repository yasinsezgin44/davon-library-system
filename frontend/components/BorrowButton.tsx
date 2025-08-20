"use client";

import { useState, useEffect } from "react";
import { useAuth } from "@/context/AuthContext";
import { apiClient } from "@/lib/apiClient";
import toast from "react-hot-toast";

type BorrowButtonProps = {
  bookId: number;
  isAvailable: boolean;
  onBorrowSuccess?: () => void;
  className?: string;
};

const BorrowButton = ({ bookId, isAvailable, onBorrowSuccess, className }: BorrowButtonProps) => {
  const { user } = useAuth();
  const [available, setAvailable] = useState<boolean>(isAvailable);
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

  useEffect(() => {
    setAvailable(isAvailable);
  }, [isAvailable]);

  const isMember = !!user && Array.isArray(user.roles) && user.roles.includes("MEMBER");

  const handleBorrow = async () => {
    if (!user) {
      toast.error("You must be logged in to borrow a book.");
      return;
    }

    if (!isMember) {
      toast.error("You do not have permission to borrow books.");
      return;
    }

    if (!available) {
      return;
    }

    try {
      setIsSubmitting(true);
      await apiClient.post(`/loans/borrow?bookId=${bookId}`);
      toast.success("Book borrowed successfully!");
      setAvailable(false);
      onBorrowSuccess?.();
    } catch (error: unknown) {
      const err = error as { response?: { data?: unknown } } | undefined;
      const message = err?.response?.data;
      toast.error(typeof message === "string" ? message : "Failed to borrow book.");
    } finally {
      setIsSubmitting(false);
    }
  };

  const disabled = !isMember || !available || isSubmitting;
  const label = !available
    ? "Borrowed"
    : !user
    ? "Sign in to borrow"
    : !isMember
    ? "Not eligible"
    : isSubmitting
    ? "Borrowing..."
    : "Borrow";

  return (
    <button
      onClick={handleBorrow}
      disabled={disabled}
      className={
        `w-full py-2 rounded-md transition-colors ` +
        (disabled ? "bg-gray-400 text-gray-700 cursor-not-allowed" : "bg-blue-500 text-white hover:bg-blue-600") +
        (className ? ` ${className}` : "")
      }
    >
      {label}
    </button>
  );
};

export default BorrowButton;


