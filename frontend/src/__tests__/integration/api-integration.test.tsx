/**
 * API Integration Tests
 * Tests the integration between frontend and backend APIs
 */

import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { carService } from '@/lib/api/cars';
import { favoritesService } from '@/lib/api/favorites';
import { authService } from '@/lib/api/auth';

// Mock API responses
const mockCar = {
  id: '1',
  make: 'Toyota',
  model: 'Camry',
  year: 2020,
  price: 15000,
  condition: 'USED' as const,
  fuelType: 'GAS' as const,
  transmission: 'AUTOMATIC' as const,
  vehicleType: 'PASSENGER' as const,
  mileage: 45000,
  description: 'Well maintained vehicle',
  images: [
    {
      id: '1',
      imageUrl: '/test-image.jpg',
      altText: 'Toyota Camry',
      displayOrder: 0,
    },
  ],
  features: {
    airbags: true,
    absBrakes: true,
    airConditioning: true,
    powerSteering: true,
    centralLocking: false,
    electricWindows: true,
  },
  isFeatured: false,
  isActive: true,
  createdAt: '2024-01-01T00:00:00Z',
  updatedAt: '2024-01-01T00:00:00Z',
  dealer: {
    id: 'dealer1',
    name: 'Test Dealer',
    email: 'dealer@test.com',
  },
};

const mockUser = {
  id: 'user1',
  email: 'test@example.com',
  role: 'BUYER' as const,
  isActive: true,
};

// Setup test environment
const createTestQueryClient = () => new QueryClient({
  defaultOptions: {
    queries: {
      retry: false,
    },
    mutations: {
      retry: false,
    },
  },
});

describe('API Integration Tests', () => {
  let queryClient: QueryClient;

  beforeEach(() => {
    queryClient = createTestQueryClient();

    // Mock fetch globally
    global.fetch = jest.fn();
  });

  afterEach(() => {
    jest.clearAllMocks();
    queryClient.clear();
  });

  describe('Car API Integration', () => {
    test('should fetch and display cars successfully', async () => {
      const mockResponse = {
        content: [mockCar],
        totalElements: 1,
        totalPages: 1,
        size: 20,
        number: 0,
        first: true,
        last: true,
        numberOfElements: 1,
      };

      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      });

      // Test the car service
      const result = await carService.getAllCars();

      expect(result).toEqual(mockResponse);
      expect(global.fetch).toHaveBeenCalledWith(
        expect.stringContaining('/api/public/cars'),
        expect.objectContaining({
          method: 'GET',
        })
      );
    });

    test('should handle car search with filters', async () => {
      const searchFilters = {
        make: 'Toyota',
        minPrice: 10000,
        maxPrice: 20000,
        page: 0,
        size: 20,
      };

      const mockResponse = {
        content: [mockCar],
        totalElements: 1,
        totalPages: 1,
        size: 20,
        number: 0,
        first: true,
        last: true,
        numberOfElements: 1,
      };

      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      });

      const result = await carService.searchCars(searchFilters);

      expect(result).toEqual(mockResponse);
      expect(global.fetch).toHaveBeenCalledWith(
        expect.stringContaining('/api/public/cars/search'),
        expect.objectContaining({
          method: 'GET',
        })
      );
    });

    test('should handle API errors gracefully', async () => {
      (global.fetch as jest.Mock).mockRejectedValueOnce(
        new Error('Network error')
      );

      await expect(carService.getAllCars()).rejects.toThrow('Network error');
    });

    test('should handle HTTP error responses', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: false,
        status: 404,
        json: async () => ({ message: 'Not found' }),
      });

      await expect(carService.getCarById('nonexistent')).rejects.toThrow();
    });
  });

  describe('Authentication API Integration', () => {
    test('should handle user login successfully', async () => {
      const loginCredentials = {
        email: 'test@example.com',
        password: 'password123',
      };

      const mockResponse = {
        accessToken: 'mock-access-token',
        refreshToken: 'mock-refresh-token',
        user: mockUser,
      };

      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => mockResponse,
      });

      const result = await authService.login(loginCredentials);

      expect(result).toEqual(mockResponse);
      expect(global.fetch).toHaveBeenCalledWith(
        expect.stringContaining('/api/auth/login'),
        expect.objectContaining({
          method: 'POST',
          body: JSON.stringify(loginCredentials),
        })
      );
    });

    test('should handle login validation errors', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: false,
        status: 400,
        json: async () => ({
          message: 'Invalid credentials',
          code: 'VALIDATION_ERROR',
        }),
      });

      await expect(
        authService.login({ email: 'invalid', password: 'short' })
      ).rejects.toThrow();
    });
  });

  describe('Favorites API Integration', () => {
    test('should add car to favorites', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ success: true }),
      });

      const result = await favoritesService.addToFavorites('car1');

      expect(result).toEqual({ success: true });
      expect(global.fetch).toHaveBeenCalledWith(
        expect.stringContaining('/api/favorites/car1'),
        expect.objectContaining({
          method: 'POST',
        })
      );
    });

    test('should remove car from favorites', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => ({ success: true }),
      });

      const result = await favoritesService.removeFromFavorites('car1');

      expect(result).toEqual({ success: true });
      expect(global.fetch).toHaveBeenCalledWith(
        expect.stringContaining('/api/favorites/car1'),
        expect.objectContaining({
          method: 'DELETE',
        })
      );
    });

    test('should fetch user favorites', async () => {
      const mockFavorites = [mockCar];

      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: true,
        json: async () => mockFavorites,
      });

      const result = await favoritesService.getFavorites();

      expect(result).toEqual(mockFavorites);
      expect(global.fetch).toHaveBeenCalledWith(
        expect.stringContaining('/api/favorites'),
        expect.objectContaining({
          method: 'GET',
        })
      );
    });
  });

  describe('Error Handling Integration', () => {
    test('should handle network timeouts', async () => {
      (global.fetch as jest.Mock).mockImplementationOnce(
        () => new Promise((_, reject) =>
          setTimeout(() => reject(new Error('Timeout')), 100)
        )
      );

      await expect(carService.getAllCars()).rejects.toThrow('Timeout');
    });

    test('should handle rate limiting', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: false,
        status: 429,
        json: async () => ({
          message: 'Too many requests',
          code: 'RATE_LIMITED',
        }),
      });

      await expect(carService.getAllCars()).rejects.toThrow();
    });

    test('should handle server errors', async () => {
      (global.fetch as jest.Mock).mockResolvedValueOnce({
        ok: false,
        status: 500,
        json: async () => ({
          message: 'Internal server error',
          code: 'SERVER_ERROR',
        }),
      });

      await expect(carService.getAllCars()).rejects.toThrow();
    });
  });

  describe('Data Consistency', () => {
    test('should maintain data consistency across API calls', async () => {
      // Mock multiple API calls
      (global.fetch as jest.Mock)
        .mockResolvedValueOnce({
          ok: true,
          json: async () => [mockCar],
        })
        .mockResolvedValueOnce({
          ok: true,
          json: async () => mockCar,
        });

      const carsList = await carService.getAllCars();
      const singleCar = await carService.getCarById('1');

      expect(carsList.content[0]).toEqual(singleCar);
    });

    test('should handle concurrent API requests', async () => {
      const mockResponse = { content: [mockCar], totalElements: 1 };

      (global.fetch as jest.Mock).mockResolvedValue({
        ok: true,
        json: async () => mockResponse,
      });

      // Make concurrent requests
      const requests = [
        carService.getAllCars(),
        carService.getFeaturedCars(),
        carService.getRecentCars(),
      ];

      const results = await Promise.all(requests);

      expect(results).toHaveLength(3);
      results.forEach(result => {
        expect(result).toEqual(mockResponse);
      });
    });
  });
});