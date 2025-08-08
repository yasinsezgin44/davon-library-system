"use client";

import { useAuth } from '@/context/AuthContext';
import BorrowedBooks from '@/components/dashboard/BorrowedBooks';
import Reservations from '@/components/dashboard/Reservations';
import ReadingHistory from '@/components/dashboard/ReadingHistory';

const MemberDashboardPage = () => {
  const { user, isAuthenticated, login } = useAuth();

  // Mock user data for demonstration
  const mockUser = {
    id: '1',
    name: 'Yasin Sezgin',
    email: 'yasin.s@example.com',
    roles: ['Member'],
  };

  // Simulate login for demonstration purposes
  const handleLogin = () => {
    login(mockUser);
  };

  if (!isAuthenticated || !user?.roles.includes('Member')) {
    return (
      <div className="text-center py-10">
        <p>You must be a logged-in member to view this page.</p>
        {!isAuthenticated && (
          <button 
            onClick={handleLogin}
            className="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
          >
            Simulate Login as Member
          </button>
        )}
      </div>
    );
  }

  return (
    <div className="container mx-auto py-10">
      <h1 className="text-3xl font-bold mb-6">Member Dashboard</h1>
      <p className="mb-8">Welcome, {user.name}!</p>
      
      <BorrowedBooks />
      <Reservations />
      <ReadingHistory />
    </div>
  );
};

export default MemberDashboardPage;

