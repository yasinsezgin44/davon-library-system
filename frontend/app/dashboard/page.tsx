// frontend/app/dashboard/page.tsx
"use client";

import { useEffect, useState } from "react";
import { LibraryCard } from "@/components/library-card";
import {
  getMyLoans,
  getMyReservations,
  returnLoan,
  cancelReservation,
} from "@/lib/api";
import { Loan } from "@/types/loan";
import { Reservation } from "@/types/reservation";
import withAuth from "@/components/auth/withAuth";
import { AppLayout } from "@/components/layout/AppLayout";
import { MyLoans } from "@/components/MyLoans";
import { MyReservations } from "@/components/MyReservations";
import { useToastHelpers } from "@/components/toast-notification";

function MemberDashboardPage() {
  const [loans, setLoans] = useState<Loan[]>([]);
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const { success, error } = useToastHelpers();

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [loansData, reservationsData] = await Promise.all([
        getMyLoans(),
        getMyReservations(),
      ]);
      setLoans(loansData);
      setReservations(reservationsData);
    } catch (error) {
      console.error("Failed to fetch dashboard data:", error);
    }
  };

  const handleReturnLoan = async (loanId: number) => {
    try {
      await returnLoan(loanId);
      fetchData();
      success("Book returned!", "The book has been successfully returned.");
    } catch (err) {
      error("Failed to return book", "Please try again later.");
    }
  };

  const handleCancelReservation = async (reservationId: number) => {
    try {
      await cancelReservation(reservationId);
      fetchData();
      success(
        "Reservation cancelled!",
        "The reservation has been successfully cancelled."
      );
    } catch (err) {
      error("Failed to cancel reservation", "Please try again later.");
    }
  };

  return (
    <AppLayout>
      <h1 className="text-3xl font-bold text-dark-gray mb-8">
        Member Dashboard
      </h1>
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <LibraryCard>
          <h2 className="text-2xl font-bold text-dark-gray mb-4">My Loans</h2>
          <MyLoans loans={loans} onReturn={handleReturnLoan} />
        </LibraryCard>
        <LibraryCard>
          <h2 className="text-2xl font-bold text-dark-gray mb-4">
            My Reservations
          </h2>
          <MyReservations
            reservations={reservations}
            onCancel={handleCancelReservation}
          />
        </LibraryCard>
      </div>
    </AppLayout>
  );
}

export default withAuth(MemberDashboardPage, ["MEMBER"]);
