// frontend/lib/api.ts
import { Book } from '../types/book';
import { User } from '../types/user';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8081/api';

async function fetchApi(url: string, options: RequestInit = {}) {
  const response = await fetch(`${API_BASE_URL}${url}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
  });

  if (!response.ok) {
    throw new Error(`API call failed with status ${response.status}`);
  }

  return response.json();
}

// Book API
export const getBooks = () => fetchApi('/books');
export const getBookById = (id: number) => fetchApi(`/books/${id}`);
export const createBook = (book: Book) => fetchApi('/books', { method: 'POST', body: JSON.stringify(book) });
export const updateBook = (id: number, book: Book) => fetchApi(`/books/${id}`, { method: 'PUT', body: JSON.stringify(book) });
export const deleteBook = (id: number) => fetchApi(`/books/${id}`, { method: 'DELETE' });
export const searchBooks = (query: string) => fetchApi(`/books/search?q=${query}`);

// User API
export const getUsers = () => fetchApi('/users');
export const getUserById = (id: number) => fetchApi(`/users/${id}`);
export const createUser = (user: User) => fetchApi('/users', { method: 'POST', body: JSON.stringify(user) });
export const updateUser = (id: number, user: User) => fetchApi(`/users/${id}`, { method: 'PUT', body: JSON.stringify(user) });
export const deleteUser = (id: number) => fetchApi(`/users/${id}`, { method: 'DELETE' });
export const searchUsers = (query: string) => fetchApi(`/users/search?q=${query}`);

// Database API
export const getDatabaseStatus = () => fetchApi('/database/status');
