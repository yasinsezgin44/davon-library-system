"use client";

import { useAuth } from '@/context/AuthContext';
import { useEffect } from 'react';

const LibrarianDashboardPage = () => {
  const { user, isAuthenticated, login } = useAuth();

  // Mock user data for demonstration
  const mockUser = {
    id: '2',
    name: 'Jane Doe',
    email: 'jane.d@example.com',
    roles: ['Librarian'],
  };

  // Simulate login for demonstration purposes
  const handleLogin = () => {
    login(mockUser);
  };

  if (!isAuthenticated || !user?.roles.includes('Librarian')) {
    return (
      <div className="text-center py-10">
        <p>You must be a logged-in librarian to view this page.</p>
        {!isAuthenticated && (
          <button 
            onClick={handleLogin}
            className="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
          >
            Simulate Login as Librarian
          </button>
        )}
      </div>
    );
  }

  return (
    <div className="container mx-auto py-10">
      <h1 className="text-3xl font-bold mb-6">Librarian Dashboard</h1>
      <p>Welcome, {user.name}!</p>
      {/* Book Management Table will go here */}
    </div>
  );
};

export default LibrarianDashboardPage;

