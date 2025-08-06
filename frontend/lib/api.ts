// frontend/lib/api.ts
import { Book } from '../types/book';
import { User } from '../types/user';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8082/api';

export async function fetchApi(url: string, options: RequestInit = {}) {
  const token = typeof window !== 'undefined' ? localStorage.getItem('token') : null;
  
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...options.headers,
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE_URL}${url}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    throw new Error(`API call failed with status ${response.status}`);
  }

  return response.json();
}

// Public API
export const getTrendingBooks = () => fetchApi('/books/trending');
export const getGenres = () => fetchApi('/books/genres');
export const getBooksByGenre = (genreId: number) => fetchApi(`/books/genre/${genreId}`);
export const getLibraryInfo = () => fetchApi('/library');

// Book API
export const getBooks = () => fetchApi('/books');
export const getBookById = (id: number) => fetchApi(`/books/${id}`);
export const searchBooks = (query: string) => fetchApi(`/books/search?query=${query}`);

// Admin API (Books)
export const createBook = (book: Book) => fetchApi('/books', { method: 'POST', body: JSON.stringify(book) });
export const updateBook = (id: number, book: Book) => fetchApi(`/books/${id}`, { method: 'PUT', body: JSON.stringify(book) });
export const deleteBook = (id: number) => fetchApi(`/books/${id}`, { method: 'DELETE' });

// Admin API (Users)
export const getAllUsers = () => fetchApi('/admin/users');
export const createUser = (user: any) => fetchApi('/admin/users', { method: 'POST', body: JSON.stringify(user) });
export const deleteUser = (id: number) => fetchApi(`/admin/users/${id}`, { method: 'DELETE' });
export const assignRoleToUser = (userId: number, roleName: string) => fetchApi(`/admin/users/${userId}/roles/${roleName}`, { method: 'POST' });

// Member API
export const reserveBook = (bookId: number) => fetchApi('/reservations', { method: 'POST', body: JSON.stringify({ bookId }) });
export const getMyLoans = () => fetchApi('/dashboard/loans');
export const getMyReservations = () => fetchApi('/dashboard/reservations');
export const getMyProfile = () => fetchApi('/profile');
export const updateMyProfile = (profile: any) => fetchApi('/profile', { method: 'PUT', body: JSON.stringify(profile) });
