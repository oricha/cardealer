export interface CarImage {
  id: string;
  carId: string;
  imageUrl: string;
  altText?: string;
  displayOrder: number;
  createdAt: string;
}

export interface CarFeatures {
  airbags: boolean;
  absBrakes: boolean;
  airConditioning: boolean;
  powerSteering: boolean;
  centralLocking: boolean;
  electricWindows: boolean;
}

export interface Car {
  id: string;
  dealerId: string;
  make: string;
  model: string;
  year: number;
  fuelType: 'GAS' | 'HYBRID' | 'DIESEL' | 'ELECTRIC';
  transmission: 'MANUAL' | 'AUTOMATIC' | 'CVT';
  vehicleType: 'VAN' | 'MOTOR' | 'PASSENGER' | 'TRUCK';
  condition: 'DAMAGED' | 'USED' | 'ACCIDENTED' | 'DERELICT';
  price: number;
  mileage?: number;
  description?: string;
  images: CarImage[];
  features: CarFeatures;
  isFeatured: boolean;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
  dealer?: Dealer;
}

export interface Dealer {
  id: string;
  name: string;
  email: string;
  address?: string;
  phone?: string;
  website?: string;
}

export interface CarSearchRequest {
  query?: string;
  make?: string;
  model?: string;
  fuelType?: Car['fuelType'];
  transmission?: Car['transmission'];
  vehicleType?: Car['vehicleType'];
  condition?: Car['condition'];
  minPrice?: number;
  maxPrice?: number;
  minYear?: number;
  maxYear?: number;
  maxMileage?: number;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
}

export interface CarFilters {
  makes: string[];
  models: string[];
  fuelTypes: Car['fuelType'][];
  transmissions: Car['transmission'][];
  vehicleTypes: Car['vehicleType'][];
  conditions: Car['condition'][];
  priceRange: {
    min: number;
    max: number;
  };
  yearRange: {
    min: number;
    max: number;
  };
}

export interface SearchState {
  query: string;
  filters: CarSearchRequest;
  results: Car[];
  totalResults: number;
  currentPage: number;
  totalPages: number;
  isLoading: boolean;
  error: string | null;
  viewMode: 'grid' | 'list';
}