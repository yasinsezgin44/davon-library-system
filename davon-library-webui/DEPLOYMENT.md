# Deployment Guide - Davon Library System

This document outlines the process for deploying the Davon Library System to various environments.

## Prerequisites

- Node.js 18.x or higher
- npm or yarn
- Git

## Local Development Environment

1. Clone the repository

   ```bash
   git clone https://github.com/yasinsezgin44/davon-library-system.git
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

## Preparing for Production

1. Update environment variables if needed

   - Create a `.env.local` file for local environment variables
   - For production, set up environment variables in your hosting platform

2. Build the application

   ```bash
   npm run build
   # or
   yarn build
   ```

3. Test the production build locally
   ```bash
   npm start
   # or
   yarn start
   ```

## Deployment Options

### Option 1: Vercel (Recommended)

1. Create an account on [Vercel](https://vercel.com/)
2. Install Vercel CLI

   ```bash
   npm install -g vercel
   ```

3. Deploy using Vercel CLI

   ```bash
   vercel
   ```

4. Follow the prompts to configure your deployment
5. For production deployment, use:
   ```bash
   vercel --prod
   ```

### Option 2: Static Export

1. Add the export configuration to `next.config.ts`:

   ```typescript
   const nextConfig = {
     output: "export",
     // other config options...
   };
   ```

2. Create a static build

   ```bash
   npm run build
   # or
   yarn build
   ```

3. The static site will be available in the `out` directory
4. Deploy this directory to any static hosting service (Netlify, GitHub Pages, etc.)

### Option 3: Docker

1. Create a `Dockerfile` in the project root:

   ```dockerfile
   FROM node:18-alpine AS base

   # Install dependencies
   FROM base AS deps
   WORKDIR /app
   COPY package.json package-lock.json* ./
   RUN npm ci

   # Build the app
   FROM base AS builder
   WORKDIR /app
   COPY --from=deps /app/node_modules ./node_modules
   COPY . .
   RUN npm run build

   # Production image
   FROM base AS runner
   WORKDIR /app
   ENV NODE_ENV production

   COPY --from=builder /app/public ./public
   COPY --from=builder /app/.next/standalone ./
   COPY --from=builder /app/.next/static ./.next/static

   EXPOSE 3000
   CMD ["node", "server.js"]
   ```

2. Build the Docker image

   ```bash
   docker build -t davon-library-system .
   ```

3. Run the Docker container
   ```bash
   docker run -p 3000:3000 davon-library-system
   ```

## Continuous Integration/Continuous Deployment (CI/CD)

### GitHub Actions

1. Create a `.github/workflows/deploy.yml` file:

   ```yaml
   name: Deploy to Production

   on:
     push:
       branches: [main]

   jobs:
     deploy:
       runs-on: ubuntu-latest
       steps:
         - uses: actions/checkout@v2

         - name: Setup Node.js
           uses: actions/setup-node@v2
           with:
             node-version: "18"

         - name: Install dependencies
           run: npm ci

         - name: Build
           run: npm run build

         - name: Deploy to Vercel
           uses: amondnet/vercel-action@v20
           with:
             vercel-token: ${{ secrets.VERCEL_TOKEN }}
             vercel-org-id: ${{ secrets.VERCEL_ORG_ID }}
             vercel-project-id: ${{ secrets.VERCEL_PROJECT_ID }}
             vercel-args: "--prod"
   ```

2. Add the required secrets to your GitHub repository:
   - `VERCEL_TOKEN`
   - `VERCEL_ORG_ID`
   - `VERCEL_PROJECT_ID`

## Environment Variables

For production deployments, consider setting these environment variables:

- `NODE_ENV`: Set to 'production'
- `NEXT_PUBLIC_API_URL`: Base URL for API calls (if different from the main URL)
- Custom environment variables specific to your application

## Post-Deployment Verification

After deploying:

1. Verify all pages load correctly
2. Test user authentication (login and registration)
3. Test admin functionality
4. Check responsive design on various devices
5. Verify API endpoints are working correctly

## Rollback Procedure

If issues occur after deployment:

1. For Vercel: Use the dashboard to roll back to a previous deployment
2. For manual deployments: Redeploy the previous version
3. Check logs to identify and fix the issue before attempting redeployment

## Maintenance

- Regularly update dependencies:
  ```bash
  npm outdated
  npm update
  ```
- Monitor error reporting tools
- Check application logs regularly
- Schedule regular backups of user data
