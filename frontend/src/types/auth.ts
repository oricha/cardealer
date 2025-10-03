export interface User {
  id: string;
  email: string;
  role: 'ADMIN' | 'DEALER' | 'BUYER';
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Dealer extends User {
  name: string;
  address?: string;
  phone?: string;
  website?: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  role: User['role'];
  email: string;
  expiresIn: number;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  role: User['role'];
  name?: string;
  address?: string;
  phone?: string;
  website?: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface AuthState {
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  isLoading: boolean;
  isAuthenticated: boolean;
}

export interface AuthContextType {
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  isLoading: boolean;
  isAuthenticated: boolean;
  login: (credentials: LoginRequest) => Promise<void>;
  register: (userData: RegisterRequest) => Promise<void>;
  logout: () => void;
  refreshAuthToken: () => Promise<void>;
}