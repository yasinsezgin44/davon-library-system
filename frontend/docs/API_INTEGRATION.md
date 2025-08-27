## API Integration (Frontend)

### Overview

- Backend base URL: `NEXT_PUBLIC_API_BASE_URL` (defaults to `http://localhost:8083/api`).
- Two clients:
  - `publicApiClient`: Axios without auth.
  - `apiClient`: Axios with auth (adds `Authorization: Bearer <token>` if cookie `token` exists).

### HTTP client

File: `frontend/lib/apiClient.ts`

- Axios instance with `withCredentials: true` and JSON headers.
- Request interceptor: reads `token` from cookies on the browser and sets `Authorization`.
- Response interceptor: returns responses or rejects errors unchanged (surface to callers).

### Next.js API routes

Located under `frontend/app/api/**`. These route handlers act as a BFF/proxy layer to the Java backend. Examples include:

- `app/api/books/route.ts` and `app/api/books/[id]/route.ts`
- `app/api/auth/login/route.ts`, `app/api/auth/me/route.ts`, `app/api/auth/logout/route.ts`, `app/api/auth/register/route.ts`
- `app/api/loans/*`, `app/api/reservations/*`, `app/api/users/*`

Typical flow:
1. Client component issues `fetch('/api/books')` or uses `apiClient.get('/books')`.
2. The API route proxies to the backend real endpoint at `${NEXT_PUBLIC_API_BASE_URL}/books` and returns JSON.
3. Errors propagate with appropriate status codes for components to handle.

### In-component usage patterns

- `BookManagementTable.tsx` uses `fetch('/api/books', { cache: 'no-store' })` for latest data.
- Create/Update/Delete operations call `POST /api/books`, `PUT /api/books/:id`, `DELETE /api/books/:id` respectively.
- Auth context calls `GET /api/auth/me` and `POST /api/auth/logout` to manage session state.

### Auth handling

- `AuthContext` hydrates user state on mount via `/api/auth/me`.
- When using Axios (`apiClient`), JWT token in cookie is attached automatically.
- Dashboard routes use role checks client-side for navigation; server-side protections can be added via middleware if needed.

### Error handling

- Components inspect `response.ok` and show alerts/logs on error.
- Interceptors pass errors through; caller decides UX.

### Caching & revalidation

- Explicit `cache: 'no-store'` used in dashboard fetches to avoid stale data.
- For landing/catalog pages, default caching can be used or switched to `force-cache` depending on needs.


