# Crashed Car Sales Frontend

Next.js React application for the Crashed Car Sales platform.

## Technology Stack

- **Next.js 14+** (App Router)
- **React 18**
- **TypeScript**
- **Tailwind CSS**
- **Radix UI** (Components)
- **React Hook Form** (Forms)
- **Zod** (Validation)
- **Axios** (HTTP Client)
- **Next-Intl** (Internationalization)

## Project Structure

```
src/
├── app/                    # Next.js App Router
│   ├── [locale]/          # Internationalized routes
│   ├── globals.css        # Global styles
│   └── layout.tsx         # Root layout
├── components/            # Reusable components
│   ├── ui/               # Base UI components
│   ├── forms/            # Form components
│   └── layout/           # Layout components
├── lib/                  # Utilities and configurations
│   ├── api.ts           # API client
│   ├── auth.ts          # Authentication utilities
│   └── utils.ts         # General utilities
├── hooks/               # Custom React hooks
├── types/               # TypeScript type definitions
└── messages/            # Internationalization messages
```

## Getting Started

### Prerequisites

- Node.js 18 or higher
- npm or yarn

### Local Development Setup

1. **Install dependencies**
   ```bash
   npm install
   ```

2. **Set up environment variables**
   ```bash
   cp .env.example .env.local
   # Edit .env.local with your configuration
   ```

3. **Start the development server**
   ```bash
   npm run dev
   ```

The application will be available at `http://localhost:3000`

### Available Scripts

```bash
# Development
npm run dev          # Start development server
npm run build        # Build for production
npm run start        # Start production server
npm run lint         # Run ESLint
npm run type-check   # Run TypeScript checks

# Testing
npm run test         # Run tests
npm run test:watch   # Run tests in watch mode
npm run test:coverage # Run tests with coverage
```

## Features

### Pages

- **Home Page** (`/`) - Landing page with featured cars and search
- **Car Listings** (`/cars`) - Browse and filter cars
- **Car Details** (`/cars/[id]`) - Detailed car information
- **Dealer Dashboard** (`/dashboard`) - Dealer management interface
- **Authentication** (`/auth/*`) - Login, register, forgot password

### Components

#### UI Components
- `CarCard` - Car display component
- `SearchFilters` - Advanced filtering interface
- `ImageGallery` - Responsive image viewer
- `Navigation` - Header with authentication
- `Footer` - Site footer with links

#### Form Components
- `LoginForm` - User authentication
- `RegisterForm` - User registration
- `CarForm` - Car listing creation/editing
- `SearchForm` - Car search interface

### Internationalization

The application supports multiple languages:
- English (en)
- Spanish (es)

Language files are located in `src/messages/`

### API Integration

The frontend communicates with the backend API through:
- Axios HTTP client
- Type-safe API calls
- Error handling and retry logic
- Authentication token management

### State Management

- React Context for global state
- Custom hooks for data fetching
- Local state with useState/useReducer
- Form state with React Hook Form

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `NEXT_PUBLIC_API_URL` | Backend API URL | `http://localhost:8080/api` |
| `NEXT_PUBLIC_CDN_URL` | CDN base URL | `https://cdn.example.com` |
| `NEXTAUTH_SECRET` | NextAuth secret key | Required |
| `NEXTAUTH_URL` | Application URL | `http://localhost:3000` |

### Styling

The application uses Tailwind CSS with:
- Custom color palette
- Responsive design utilities
- Component-based styling
- Dark mode support (optional)

### Performance Optimization

- Image optimization with Next.js Image component
- Code splitting with dynamic imports
- Service Worker for caching
- Bundle analysis and optimization

## Deployment

### Docker

```bash
# Build Docker image
docker build -t crashed-car-sales-frontend .

# Run container
docker run -p 3000:3000 crashed-car-sales-frontend
```

### Production Build

```bash
# Build for production
npm run build

# Start production server
npm start
```

## Testing

### Unit Tests
- Jest for test runner
- React Testing Library for component testing
- MSW for API mocking

### E2E Tests
- Playwright for end-to-end testing
- Critical user journey coverage

### Running Tests

```bash
# Unit tests
npm run test

# E2E tests
npm run test:e2e

# Coverage report
npm run test:coverage
```

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Accessibility

- WCAG 2.1 AA compliance
- Keyboard navigation support
- Screen reader compatibility
- High contrast mode support