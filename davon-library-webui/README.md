# Davon Library Management System

A Next.js application for managing the Davon Library, featuring user management, authentication, and an admin panel.

## Features

- **User Authentication**: Secure login and registration system
- **Admin Panel**: Dedicated admin interface for user management
- **User Management**: CRUD operations for user accounts
- **Role-Based Access Control**: Different permissions for admin and regular users
- **Responsive Design**: Optimized for all device sizes
- **Accessibility**: ARIA attributes and keyboard navigation support

## Project Structure

```
davon-library-webui/
├── src/
│   ├── app/                 # Main application code
│   │   ├── api/             # API routes for authentication and data
│   │   │   ├── users/       # User management endpoints
│   │   │   └── auth/        # Authentication endpoints
│   │   ├── components/      # Reusable React components
│   │   │   ├── admin/       # Admin-specific components
│   │   │   ├── AuthForm/    # Login and registration forms
│   │   │   ├── Modal/       # Modal dialog components
│   │   │   ├── Profile/     # User profile components
│   │   │   └── contexts/    # React context providers
│   │   ├── admin/           # Admin pages
│   │   └── profile/         # User profile pages
│   ├── styles/              # Global styles and theme
│   └── types/               # TypeScript type definitions
├── public/                  # Static assets
├── users.json               # Database file for users
└── package.json             # Project dependencies and scripts
```

## Component Hierarchy

```
App
├── Layout                   # Main layout with navigation
│   ├── AuthContext          # Authentication state provider
│   ├── Header               # Navigation and auth controls
│   │   └── ProfileMenu      # User profile and logout
│   └── Footer               # Site footer
├── HomePage                 # Landing page
├── AdminPage                # Admin dashboard (protected)
│   ├── AdminRouteProtector  # Auth guard for admin routes
│   └── AdminApp             # Admin application
│       ├── UserList         # List of all users
│       └── UserCreate       # Create new user form
├── AuthForms                # Authentication components
│   ├── Modal                # Modal dialog container
│   ├── LoginForm            # User login form
│   └── RegistrationForm     # New user registration
└── ProfilePage              # User profile (protected)
```

## State Management Approach

The application uses a combination of:

1. **React Context API**:

   - `AuthContext` manages global authentication state
   - Stores current user details and provides auth methods

2. **Local State**:

   - Component-level state using React `useState` hook
   - Form state and UI interactions

3. **Server State**:

   - Data persistence through Next.js API routes
   - User data stored in JSON file

4. **URL State**:
   - Routing handled by Next.js router
   - Protected routes based on authentication status

## Data Flow

1. **Authentication Flow**:

   - User submits credentials via login/register forms
   - Request sent to API endpoint
   - On success, user data stored in AuthContext and localStorage
   - UI updates to reflect authenticated state

2. **Admin Operations**:
   - Admin-specific operations protected by role checks
   - CRUD operations performed through API endpoints
   - Real-time UI updates after successful operations

## Getting Started

### Prerequisites

- Node.js 18.x or higher
- npm or yarn

### Installation

1. Clone the repository

   ```bash
   git clone https://github.com/your-username/davon-library-system.git
   cd davon-library-webui
   ```

2. Install dependencies

   ```bash
   npm install
   # or
   yarn install
   ```

3. Run the development server

   ```bash
   npm run dev
   # or
   yarn dev
   ```

4. Open [http://localhost:3000](http://localhost:3000) in your browser

### Testing

- Login as admin:

  - Email: admin@davonlibrary.com
  - Password: vnd

- Register a new user:
  - Use the registration form to create a standard user account

### Building for Production

```bash
npm run build
npm start
# or
yarn build
yarn start
```

## Development Guidelines

- **Git Flow**:

  - Use feature branches for new functionality
  - Merge to develop for integration
  - Create release branches for versioned releases
  - Hotfixes directly to main branch for critical issues

- **Code Style**:
  - Follow ESLint rules in the project
  - Use TypeScript for type safety

## Accessibility Features

- ARIA attributes for screen readers
- Keyboard navigation support
- Focus management for modals
- Semantic HTML structure
- Responsive design for all devices
- Proper color contrast
