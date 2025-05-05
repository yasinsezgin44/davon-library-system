"use client"; // <-- Important: Mark this as a Client Component

import { Admin, Resource, ListGuesser, EditGuesser } from "react-admin";
import simpleRestProvider from "ra-data-simple-rest";
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

// Use simple REST provider to connect to Next.js API
export const dataProvider = simpleRestProvider("/api");

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
