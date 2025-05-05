"use client"; // <-- Important: Mark this as a Client Component

import { Admin, Resource, ListGuesser, EditGuesser } from "react-admin";
import fakeDataProvider from "ra-data-fakerest";
import { UserList } from "@/app/components/admin/UserList"; // Use alias path
import { UserCreate } from "@/app/components/admin/UserCreate"; // <-- Remove .tsx extension

// Define the User type (consider moving to a shared types file)
export interface AdminUser {
  // Renamed to avoid conflict if User exists elsewhere
  id: number; // fakerest often prefers numbers for default IDs
  name: string;
  email: string;
  role: string;
  createdAt: Date;
  // Add password hash/storage if needed for simulation,
  // but fakerest doesn't inherently handle auth logic
  password?: string; // STORE HASHED PASSWORDS in real apps!
}

// --- Initialize the dataProvider ONCE, outside the component ---
const initialUsers: AdminUser[] = [
  {
    id: 1,
    name: "Admin User",
    email: "admin@library.com",
    role: "Admin",
    createdAt: new Date("2023-10-01"),
    password: "adminpassword",
  },
  {
    id: 2,
    name: "Jane Doe",
    email: "jane.d@library.com",
    role: "Member",
    createdAt: new Date("2023-10-15"),
    password: "password123",
  },
  {
    id: 3,
    name: "John Smith",
    email: "john.s@library.com",
    role: "Member",
    createdAt: new Date("2023-11-01"),
    password: "password456",
  },
];

export const dataProvider = fakeDataProvider(
  {
    // If localStorage is empty for 'users', it will use this initial data.
    // If localStorage HAS data for 'users', it will use THAT data instead.
    users: initialUsers,
    // books: []
  },
  true // Log calls
);
// --- End dataProvider initialization ---

export const AdminUI = () => (
  <Admin dataProvider={dataProvider}>
    <Resource
      name="users" // This should match the key in fakeDataProvider and your future API endpoint
      list={UserList} // Use our custom list component
      edit={EditGuesser} // Use React Admin's guesser for Edit view for now
      create={UserCreate} // <-- Use UserCreate component here
      // recordRepresentation="name" // Optional: How to represent a user record in links/titles
    />
    {/* Add other Resource components here (e.g., for books) */}
    {/* <Resource name="books" list={ListGuesser} /> */}
  </Admin>
);
