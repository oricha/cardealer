import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { AuthProvider, useAuth } from '@/contexts/AuthContext';
import { LoginForm } from '@/components/auth/LoginForm';
import { RegisterForm } from '@/components/auth/RegisterForm';
import { ProtectedRoute } from '@/components/auth/ProtectedRoute';

// Mock the toast utility
jest.mock('@/lib/utils/toast', () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
    info: jest.fn(),
    warning: jest.fn(),
  },
}));

// Mock axios
jest.mock('axios', () => ({
  create: jest.fn(() => ({
    post: jest.fn(),
    interceptors: {
      request: { use: jest.fn() },
      response: { use: jest.fn() },
    },
  })),
}));

// Test component that uses auth context
const TestComponent = () => {
  const { isAuthenticated, user, login, logout } = useAuth();

  return (
    <div>
      <span data-testid="auth-status">
        {isAuthenticated ? 'authenticated' : 'not-authenticated'}
      </span>
      <span data-testid="user-role">{user?.role || 'none'}</span>
      <button onClick={() => login({ email: 'test@test.com', password: 'password' })}>
        Login
      </button>
      <button onClick={logout}>Logout</button>
    </div>
  );
};

describe('Authentication System', () => {
  beforeEach(() => {
    // Clear localStorage before each test
    localStorage.clear();
    jest.clearAllMocks();
  });

  describe('AuthContext', () => {
    it('provides authentication state to components', () => {
      render(
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      );

      expect(screen.getByTestId('auth-status')).toHaveTextContent('not-authenticated');
      expect(screen.getByTestId('user-role')).toHaveTextContent('none');
    });

    it('handles login functionality', async () => {
      render(
        <AuthProvider>
          <TestComponent />
        </AuthProvider>
      );

      const loginButton = screen.getByText('Login');
      fireEvent.click(loginButton);

      // Since we're mocking axios, the login should handle the mock response
      await waitFor(() => {
        expect(screen.getByTestId('auth-status')).toBeInTheDocument();
      });
    });
  });

  describe('LoginForm', () => {
    it('renders login form with all fields', () => {
      const mockToggle = jest.fn();

      render(<LoginForm onToggleMode={mockToggle} />);

      expect(screen.getByText('Sign In')).toBeInTheDocument();
      expect(screen.getByLabelText('Email')).toBeInTheDocument();
      expect(screen.getByLabelText('Password')).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /sign in/i })).toBeInTheDocument();
    });

    it('validates email format', async () => {
      const mockToggle = jest.fn();

      render(<LoginForm onToggleMode={mockToggle} />);

      const emailInput = screen.getByLabelText('Email');
      const submitButton = screen.getByRole('button', { name: /sign in/i });

      fireEvent.change(emailInput, { target: { value: 'invalid-email' } });
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText('Please enter a valid email address')).toBeInTheDocument();
      });
    });

    it('toggles to register form', () => {
      const mockToggle = jest.fn();

      render(<LoginForm onToggleMode={mockToggle} />);

      const toggleButton = screen.getByText(/sign up/i);
      fireEvent.click(toggleButton);

      expect(mockToggle).toHaveBeenCalled();
    });
  });

  describe('RegisterForm', () => {
    it('renders register form with all fields', () => {
      const mockToggle = jest.fn();

      render(<RegisterForm onToggleMode={mockToggle} />);

      expect(screen.getByText('Create Account')).toBeInTheDocument();
      expect(screen.getByLabelText('Full Name')).toBeInTheDocument();
      expect(screen.getByLabelText('I am a')).toBeInTheDocument();
      expect(screen.getByLabelText('Email')).toBeInTheDocument();
      expect(screen.getByLabelText('Password')).toBeInTheDocument();
      expect(screen.getByLabelText('Confirm Password')).toBeInTheDocument();
    });

    it('validates password confirmation', async () => {
      const mockToggle = jest.fn();

      render(<RegisterForm onToggleMode={mockToggle} />);

      const passwordInput = screen.getByLabelText('Password');
      const confirmPasswordInput = screen.getByLabelText('Confirm Password');
      const submitButton = screen.getByRole('button', { name: /create account/i });

      fireEvent.change(passwordInput, { target: { value: 'password123' } });
      fireEvent.change(confirmPasswordInput, { target: { value: 'different' } });
      fireEvent.click(submitButton);

      await waitFor(() => {
        expect(screen.getByText(/passwords don't match/i)).toBeInTheDocument();
      });
    });
  });

  describe('ProtectedRoute', () => {
    it('renders children when authenticated', () => {
      // Mock authenticated state
      jest.spyOn(require('@/contexts/AuthContext'), 'useAuth').mockReturnValue({
        isAuthenticated: true,
        user: { role: 'BUYER' },
        isLoading: false,
      });

      render(
        <ProtectedRoute>
          <div data-testid="protected-content">Protected Content</div>
        </ProtectedRoute>
      );

      expect(screen.getByTestId('protected-content')).toBeInTheDocument();
    });

    it('redirects when not authenticated', () => {
      // Mock unauthenticated state
      jest.spyOn(require('@/contexts/AuthContext'), 'useAuth').mockReturnValue({
        isAuthenticated: false,
        user: null,
        isLoading: false,
      });

      const { container } = render(
        <ProtectedRoute>
          <div>Protected Content</div>
        </ProtectedRoute>
      );

      // Should not render children when not authenticated
      expect(container.firstChild).toBeNull();
    });

    it('shows loading spinner while checking authentication', () => {
      // Mock loading state
      jest.spyOn(require('@/contexts/AuthContext'), 'useAuth').mockReturnValue({
        isAuthenticated: false,
        user: null,
        isLoading: true,
      });

      render(
        <ProtectedRoute>
          <div>Protected Content</div>
        </ProtectedRoute>
      );

      expect(screen.getByRole('generic')).toHaveClass('animate-spin');
    });
  });
});