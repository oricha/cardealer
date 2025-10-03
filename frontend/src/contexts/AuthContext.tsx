'use client';

import React, { createContext, useContext, useEffect, useState, useCallback } from 'react';
import { AuthContextType, User, LoginRequest, RegisterRequest } from '@/types/auth';
import { authService } from '@/lib/api/auth';

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: React.ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [accessToken, setAccessToken] = useState<string | null>(null);
  const [refreshToken, setRefreshToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const isAuthenticated = !!accessToken && !!user;

  // Initialize auth state from localStorage
  useEffect(() => {
    const initializeAuth = () => {
      try {
        const { accessToken: storedAccessToken, refreshToken: storedRefreshToken } = authService.getStoredTokens();

        if (storedAccessToken && storedRefreshToken) {
          setAccessToken(storedAccessToken);
          setRefreshToken(storedRefreshToken);

          // Extract user info from token
          const userRole = authService.getUserRole();
          if (userRole) {
            // In a real app, you'd decode the full token to get user info
            // For now, we'll set a basic user object
            setUser({
              id: '',
              email: '',
              role: userRole as User['role'],
              isActive: true,
              createdAt: new Date().toISOString(),
              updatedAt: new Date().toISOString(),
            });
          }
        }
      } catch (error) {
        console.error('Error initializing auth:', error);
        // Clear corrupted tokens
        authService.getStoredTokens();
      } finally {
        setIsLoading(false);
      }
    };

    initializeAuth();
  }, []);

  // Auto-refresh token before expiry
  useEffect(() => {
    if (!accessToken) return;

    // Set up token refresh interval (refresh every 50 minutes for 1-hour tokens)
    const refreshInterval = setInterval(async () => {
      try {
        await refreshAuthToken();
      } catch (error) {
        console.error('Auto-refresh failed:', error);
        logout();
      }
    }, 50 * 60 * 1000); // 50 minutes

    return () => clearInterval(refreshInterval);
  }, [accessToken]);

  const login = useCallback(async (credentials: LoginRequest): Promise<void> => {
    setIsLoading(true);
    try {
      const response = await authService.login(credentials);

      setAccessToken(response.accessToken);
      setRefreshToken(response.refreshToken);

      // Set basic user info (in a real app, you'd fetch full user profile)
      setUser({
        id: '',
        email: credentials.email,
        role: response.role,
        isActive: true,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      });
    } catch (error) {
      setAccessToken(null);
      setRefreshToken(null);
      setUser(null);
      throw error;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const register = useCallback(async (userData: RegisterRequest): Promise<void> => {
    setIsLoading(true);
    try {
      const response = await authService.register(userData);

      setAccessToken(response.accessToken);
      setRefreshToken(response.refreshToken);

      // Set basic user info
      setUser({
        id: '',
        email: userData.email,
        role: userData.role,
        isActive: true,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      });
    } catch (error) {
      setAccessToken(null);
      setRefreshToken(null);
      setUser(null);
      throw error;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const refreshAuthToken = useCallback(async (): Promise<void> => {
    try {
      const response = await authService.refreshToken();
      setAccessToken(response.accessToken);
      setRefreshToken(response.refreshToken);
    } catch (error) {
      // Refresh failed, logout user
      logout();
      throw error;
    }
  }, []);

  const logout = useCallback((): void => {
    authService.logout();
    setUser(null);
    setAccessToken(null);
    setRefreshToken(null);
  }, []);

  const value: AuthContextType = {
    user,
    accessToken,
    refreshToken,
    isLoading,
    isAuthenticated,
    login,
    register,
    logout,
    refreshAuthToken,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};