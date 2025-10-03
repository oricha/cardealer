import axios from 'axios';
import { Car, CarSearchRequest, PaginatedResponse } from '@/types/car';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

const carsApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token if available
carsApi.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export const carService = {
  async getAllCars(params?: {
    page?: number;
    size?: number;
    sortBy?: string;
    sortDir?: 'asc' | 'desc';
  }): Promise<PaginatedResponse<Car>> {
    const response = await carsApi.get<PaginatedResponse<Car>>('/public/cars', {
      params: {
        page: params?.page || 0,
        size: params?.size || 20,
        sortBy: params?.sortBy || 'createdAt',
        sortDir: params?.sortDir || 'desc',
      },
    });
    return response.data;
  },

  async getCarById(id: string): Promise<Car> {
    const response = await carsApi.get<Car>(`/public/cars/${id}`);
    return response.data;
  },

  async searchCars(filters: CarSearchRequest): Promise<PaginatedResponse<Car>> {
    const response = await carsApi.get<PaginatedResponse<Car>>('/public/cars/search', {
      params: {
        ...filters,
        page: filters.page || 0,
        size: filters.size || 20,
      },
    });
    return response.data;
  },

  async getFeaturedCars(): Promise<Car[]> {
    const response = await carsApi.get<Car[]>('/public/cars/featured');
    return response.data;
  },

  async getRecentCars(limit: number = 10): Promise<Car[]> {
    const response = await carsApi.get<Car[]>('/public/cars/recent', {
      params: { limit },
    });
    return response.data;
  },

  async getSimilarCars(carId: string): Promise<Car[]> {
    const response = await carsApi.get<Car[]>(`/public/cars/${carId}/similar`);
    return response.data;
  },

  async getDealerCars(dealerId: string): Promise<Car[]> {
    const response = await carsApi.get<Car[]>(`/public/dealers/${dealerId}/cars`);
    return response.data;
  },

  // Authenticated endpoints (for dealers)
  async createCar(carData: Omit<Car, 'id' | 'createdAt' | 'updatedAt' | 'dealer'>): Promise<Car> {
    const response = await carsApi.post<Car>('/cars', carData);
    return response.data;
  },

  async updateCar(id: string, carData: Partial<Car>): Promise<Car> {
    const response = await carsApi.put<Car>(`/cars/${id}`, carData);
    return response.data;
  },

  async deleteCar(id: string): Promise<void> {
    await carsApi.delete(`/cars/${id}`);
  },

  async getMyCars(params?: {
    page?: number;
    size?: number;
    sortBy?: string;
    sortDir?: 'asc' | 'desc';
  }): Promise<PaginatedResponse<Car>> {
    const response = await carsApi.get<PaginatedResponse<Car>>('/cars', {
      params: {
        page: params?.page || 0,
        size: params?.size || 20,
        sortBy: params?.sortBy || 'createdAt',
        sortDir: params?.sortDir || 'desc',
      },
    });
    return response.data;
  },
};

export default carsApi;