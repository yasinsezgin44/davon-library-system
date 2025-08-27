## React Component Hierarchy and State Management

### App structure (Next.js app router)

- `app/layout.tsx` → wraps pages; includes global styles and shared layout
  - `components/ClientLayout.tsx`
    - `components/Navbar.tsx`
    - `components/Footer.tsx`

- Top-level pages
  - `app/page.tsx` (landing) → `HeroSection`, `FeaturesSection`, `TrendingBooks`
  - `app/catalog/page.tsx` → uses `BookCard`, `CategoryScroller`
  - `app/search/page.tsx` → query-driven listing with `BookCard`
  - `app/books/[id]/page.tsx` → details; may use `BorrowButton`
  - `app/profile/page.tsx`
  - Dashboard
    - `app/dashboard/admin/page.tsx`
    - `app/dashboard/librarian/page.tsx`
    - `app/dashboard/member/page.tsx`

### Dashboard components

- `components/dashboard/BookManagementTable.tsx`
  - `CreateBookModal`
  - `UpdateBookModal`
  - `DeleteConfirmationModal`

- `components/dashboard/AuthorManagementTable.tsx`
  - `CreateAuthorModal`
  - `UpdateAuthorModal`
  - `DeleteConfirmationModal`

- `components/dashboard/UserManagementTable.tsx`
  - `CreateUserModal`
  - `UpdateUserModal`
  - `DeleteConfirmationModal`

- Member views: `BorrowedBooks`, `ReadingHistory`

### Shared components

- `Navbar`, `Footer`, `HeroSection`, `FeaturesSection`, `TrendingBooks`, `BookCard`, `CategoryScroller`, `BorrowButton`

### State management

- Global auth state: `context/AuthContext.tsx`
  - Stores `user`, `isAuthenticated`, `isAuthReady`
  - Actions: `login()`, `logout()`
  - On mount: fetches `/api/auth/me` to hydrate state
  - Navigation on login by role: `ADMIN` → `/dashboard/admin`, `LIBRARIAN` → `/dashboard/librarian`, else `/dashboard/member`

- Local UI state in components
  - Tables: pagination state (`currentPage`), modal visibility, selected item
  - Navbar: search query, dropdown open/close

### Data fetching patterns

- API routes under `app/api/**` provide a proxy to backend (Quarkus) endpoints.
- Client-side fetch via `fetch(...)` with `cache: 'no-store'` for fresh data in dashboards, plus `apiClient` (Axios) where needed.
- Auth token management via `lib/apiClient.ts` using `js-cookie` and Axios interceptors (adds Authorization header if token exists).

### Notes

- Components are client components (`"use client"`) where interactivity is needed.
- Modals receive `isOpen`, `onClose`, and operation callbacks to keep parent state as the single source of truth.


