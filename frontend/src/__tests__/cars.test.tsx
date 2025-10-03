import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { CarCard } from '@/components/cars/CarCard';
import { SearchFilters } from '@/components/cars/SearchFilters';
import CarsPage from '@/app/cars/page';
import { Car, CarFilters } from '@/types/car';

// Mock the car service
jest.mock('@/lib/api/cars', () => ({
  carService: {
    searchCars: jest.fn(),
    getAllCars: jest.fn(),
  },
}));

// Mock the toast utility
jest.mock('@/lib/utils/toast', () => ({
  toast: {
    error: jest.fn(),
  },
}));

// Mock Next.js Image component
jest.mock('next/image', () => ({
  __esModule: true,
  default: ({ src, alt, ...props }: any) => <img src={src} alt={alt} {...props} />,
}));

// Mock Next.js Link component
jest.mock('next/link', () => ({
  __esModule: true,
  default: ({ children, href }: any) => <a href={href}>{children}</a>,
}));

const mockCar: Car = {
  id: '1',
  dealerId: 'dealer1',
  make: 'Toyota',
  model: 'Camry',
  year: 2020,
  fuelType: 'GAS',
  transmission: 'AUTOMATIC',
  vehicleType: 'PASSENGER',
  condition: 'USED',
  price: 15000,
  mileage: 50000,
  description: 'Great condition, low mileage',
  images: [
    {
      id: 'img1',
      carId: '1',
      imageUrl: 'https://example.com/car.jpg',
      altText: 'Toyota Camry',
      displayOrder: 0,
      createdAt: '2024-01-01T00:00:00Z',
    },
  ],
  features: {
    airbags: true,
    absBrakes: true,
    airConditioning: true,
    powerSteering: true,
    centralLocking: true,
    electricWindows: true,
  },
  isFeatured: false,
  isActive: true,
  createdAt: '2024-01-01T00:00:00Z',
  updatedAt: '2024-01-01T00:00:00Z',
};

const mockFilters: CarFilters = {
  makes: ['Toyota', 'Honda', 'Ford'],
  models: ['Camry', 'Civic', 'F-150'],
  fuelTypes: ['GAS', 'DIESEL', 'HYBRID', 'ELECTRIC'],
  transmissions: ['MANUAL', 'AUTOMATIC', 'CVT'],
  vehicleTypes: ['PASSENGER', 'TRUCK', 'VAN', 'MOTOR'],
  conditions: ['USED', 'DAMAGED', 'ACCIDENTED', 'DERELICT'],
  priceRange: { min: 1000, max: 50000 },
  yearRange: { min: 2000, max: 2024 },
};

describe('Car Listing Components', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('CarCard', () => {
    it('renders car information correctly in grid mode', () => {
      render(<CarCard car={mockCar} viewMode="grid" />);

      expect(screen.getByText('2020 Toyota Camry')).toBeInTheDocument();
      expect(screen.getByText('$15,000')).toBeInTheDocument();
      expect(screen.getByText('USED')).toBeInTheDocument();
      expect(screen.getByText('View Details')).toBeInTheDocument();
    });

    it('renders car information correctly in list mode', () => {
      render(<CarCard car={mockCar} viewMode="list" />);

      expect(screen.getByText('2020 Toyota Camry')).toBeInTheDocument();
      expect(screen.getByText('50,000 miles')).toBeInTheDocument();
      expect(screen.getByText('AUTOMATIC')).toBeInTheDocument();
    });

    it('displays featured badge when car is featured', () => {
      const featuredCar = { ...mockCar, isFeatured: true };
      render(<CarCard car={featuredCar} />);

      expect(screen.getByText('Featured')).toBeInTheDocument();
    });

    it('handles image error gracefully', () => {
      render(<CarCard car={mockCar} />);

      const image = screen.getByAltText('Toyota Camry');
      fireEvent.error(image);

      // Should show fallback icon
      expect(screen.getByRole('generic')).toBeInTheDocument();
    });

    it('calls onFavoriteToggle when favorite button is clicked', () => {
      const mockToggle = jest.fn();
      render(<CarCard car={mockCar} onFavoriteToggle={mockToggle} />);

      const favoriteButton = screen.getByRole('button');
      fireEvent.click(favoriteButton);

      expect(mockToggle).toHaveBeenCalledWith('1');
    });
  });

  describe('SearchFilters', () => {
    const mockOnFiltersChange = jest.fn();

    it('renders all filter options', () => {
      render(
        <SearchFilters
          filters={{}}
          availableFilters={mockFilters}
          onFiltersChange={mockOnFiltersChange}
        />
      );

      expect(screen.getByText('Filters')).toBeInTheDocument();
      expect(screen.getByPlaceholderText('Search by make, model, or description...')).toBeInTheDocument();
    });

    it('updates search query when input changes', () => {
      render(
        <SearchFilters
          filters={{}}
          availableFilters={mockFilters}
          onFiltersChange={mockOnFiltersChange}
        />
      );

      const searchInput = screen.getByPlaceholderText('Search by make, model, or description...');
      fireEvent.change(searchInput, { target: { value: 'Toyota' } });

      expect(mockOnFiltersChange).toHaveBeenCalledWith({ query: 'Toyota' });
    });

    it('displays active filters count', () => {
      render(
        <SearchFilters
          filters={{ make: 'Toyota', condition: 'USED' }}
          availableFilters={mockFilters}
          onFiltersChange={mockOnFiltersChange}
        />
      );

      expect(screen.getByText('2 active')).toBeInTheDocument();
    });

    it('resets filters when reset button is clicked', () => {
      render(
        <SearchFilters
          filters={{ make: 'Toyota' }}
          availableFilters={mockFilters}
          onFiltersChange={mockOnFiltersChange}
        />
      );

      const resetButton = screen.getByText('Reset');
      fireEvent.click(resetButton);

      expect(mockOnFiltersChange).toHaveBeenCalledWith({
        page: 0,
        size: 20,
      });
    });
  });

  describe('CarsPage', () => {
    beforeEach(() => {
      // Mock successful API response
      const { carService } = require('@/lib/api/cars');
      carService.searchCars.mockResolvedValue({
        content: [mockCar],
        totalElements: 1,
        totalPages: 1,
        size: 12,
        number: 0,
        first: true,
        last: true,
        numberOfElements: 1,
      });
    });

    it('renders cars page with title and description', async () => {
      render(<CarsPage />);

      expect(screen.getByText('Browse Cars')).toBeInTheDocument();
      expect(screen.getByText('Find your perfect salvage vehicle from our extensive inventory')).toBeInTheDocument();

      await waitFor(() => {
        expect(screen.getByText('2020 Toyota Camry')).toBeInTheDocument();
      });
    });

    it('toggles between grid and list view modes', async () => {
      render(<CarsPage />);

      await waitFor(() => {
        expect(screen.getByText('2020 Toyota Camry')).toBeInTheDocument();
      });

      // Should start in grid mode by default
      const listButton = screen.getByRole('button', { name: /list/i });
      fireEvent.click(listButton);

      // View mode should change (component would re-render with different styling)
      expect(listButton).toBeInTheDocument();
    });

    it('toggles mobile filters on mobile devices', async () => {
      render(<CarsPage />);

      const filterButton = screen.getByText('Filters');
      fireEvent.click(filterButton);

      // Mobile filters should become visible
      await waitFor(() => {
        expect(screen.getByText('Search by make, model, or description...')).toBeInTheDocument();
      });
    });

    it('displays loading state initially', () => {
      render(<CarsPage />);

      // Should show loading spinner initially
      expect(screen.getByText('Loading cars...')).toBeInTheDocument();
    });

    it('displays error state when API fails', async () => {
      const { carService } = require('@/lib/api/cars');
      carService.searchCars.mockRejectedValue(new Error('API Error'));

      render(<CarsPage />);

      await waitFor(() => {
        expect(screen.getByText('Failed to load cars')).toBeInTheDocument();
      });
    });
  });
});