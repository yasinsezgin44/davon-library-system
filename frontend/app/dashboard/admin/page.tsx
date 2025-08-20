"use client";

import { useAuth } from "../../../context/AuthContext";
import BookManagementTable from "../../../components/dashboard/BookManagementTable";
import UserManagementTable from "../../../components/dashboard/UserManagementTable";
import AuthorManagementTable from "../../../components/dashboard/AuthorManagementTable";
import { useEffect, useState } from "react";
import Link from "next/link";

const AdminDashboardPage = () => {
  const { user } = useAuth();
  type Fine = {
    id: number;
    member?: { fullName?: string; user?: { fullName?: string } };
    loan?: { book?: { title?: string } };
    amount: number | string;
    reason?: string;
    status?: string;
  };
  const [fines, setFines] = useState<Fine[]>([]);
  const [loadingFines, setLoadingFines] = useState(false);

  useEffect(() => {
    const fetchFines = async () => {
      if (!user || !user.roles.includes("ADMIN")) return;
      setLoadingFines(true);
      try {
        const resp = await fetch("/api/fines?scope=admin", {
          cache: "no-store",
        });
        if (resp.ok) {
          const data: Fine[] = await resp.json();
          setFines(Array.isArray(data) ? data : []);
        }
      } finally {
        setLoadingFines(false);
      }
    };
    fetchFines();
  }, [user]);

  if (!user || !user.roles.includes("ADMIN")) {
    return (
      <div className="text-center py-10">
        <p>You must be a logged-in admin to view this page.</p>
      </div>
    );
  }

  type ActiveLoan = {
    id: number;
    checkoutDate: string;
    dueDate: string;
    member?: { user?: { fullName?: string }; fullName?: string };
    bookCopy?: { book?: { title?: string } };
    book?: { title?: string };
  };
  const AdminActiveLoansTable = () => {
    const [loans, setLoans] = useState<ActiveLoan[]>([]);
    const [loading, setLoading] = useState(false);
    useEffect(() => {
      const fetchLoans = async () => {
        setLoading(true);
        try {
          const resp = await fetch("/api/loans?scope=admin-active", {
            cache: "no-store",
          });
          if (resp.ok) {
            const data = await resp.json();
            setLoans(Array.isArray(data) ? data : []);
          }
        } finally {
          setLoading(false);
        }
      };
      fetchLoans();
    }, []);
    if (loading) return <div className="p-4">Loading loans...</div>;
    return (
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Book
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Borrower
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Checkout
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Due
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {loans.map((loan) => (
              <tr key={loan.id}>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {loan?.bookCopy?.book?.title || loan?.book?.title || "-"}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {loan?.member?.user?.fullName ||
                    loan?.member?.fullName ||
                    "-"}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {loan.checkoutDate}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {loan.dueDate}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    );
  };

  return (
    <div className="container mx-auto py-10">
      <h1 className="text-3xl font-bold mb-6">Admin Dashboard</h1>
      <p className="mb-8">Welcome, {user.fullName}!</p>

      <div className="space-y-12">
        <BookManagementTable />
        <AuthorManagementTable />
        <UserManagementTable />
        <div className="bg-white shadow-md rounded-lg overflow-hidden">
          <div className="flex justify-between items-center mb-4 px-6 py-4">
            <h2 className="text-2xl font-bold text-gray-800">Active Loans</h2>
            <Link href="#" className="text-sm text-indigo-600 hover:underline">
              Refresh
            </Link>
          </div>
          <AdminActiveLoansTable />
        </div>
        <div className="bg-white shadow-md rounded-lg overflow-hidden">
          <div className="flex justify-between items-center mb-4 px-6 py-4">
            <h2 className="text-2xl font-bold text-gray-800">Fines</h2>
          </div>
          <div className="overflow-x-auto">
            {loadingFines ? (
              <div className="p-4">Loading fines...</div>
            ) : (
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Member
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Book
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Amount
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Reason
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Status
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {fines.map((fine) => (
                    <tr key={fine.id}>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {fine?.member?.fullName ||
                          fine?.member?.user?.fullName ||
                          "-"}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {fine?.loan?.book?.title || "-"}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {fine.amount}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {fine.reason}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {fine.status}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboardPage;
