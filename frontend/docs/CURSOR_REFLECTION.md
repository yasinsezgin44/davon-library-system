## Reflection: Using Cursor IDE for React Development

### What worked well

- Inline context: Quick file previews and edits across `app/` and `components/` made cross-referencing straightforward.
- AI-assisted scaffolding: Generating docs and boilerplate (e.g., tables, modals, context) sped up delivery.
- Terminal integration: Running scripts and validating API flows without context switching.

### Workflow highlights

- Component-first iteration: Built interactive client components (`"use client"`) and wired them to API routes under `app/api/**`.
- Centralized API client: `lib/apiClient.ts` with Axios interceptors simplified authenticated calls.
- Context-driven auth: `AuthContext` for global user state and role-aware navigation.

### Challenges and mitigations

- Type drift from backend DTOs: Normalized API responses in components (e.g., adapting `publisher`/`category` shapes) to keep UI stable.
- Caching nuances in App Router: Used `cache: 'no-store'` for dashboards to ensure fresh data.
- Modal orchestration: Kept parent components as single source of truth for `isOpen`/selection to avoid inconsistent UI.

### Tips for future work

- Add validation and error toasts to replace alerts.
- Introduce React Query or SWR for declarative data fetching, caching, and revalidation.
- Consider server components for read-only pages to reduce client JS and improve TTFB.
- Add route-level guards (middleware) for role-protected pages.


