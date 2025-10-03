'use client';

import { useState, useEffect, useCallback } from 'react';
import { CarCard } from '@/components/cars/CarCard';
import { SearchFilters } from '@/components/cars/SearchFilters';
import { Button } from '@/components/ui/Button';
import { Car, CarSearchRequest, CarFilters, PaginatedResponse } from '@/types/car';
import { carService } from '@/lib/api/cars';
import { toast } from '@/lib/utils/toast';
import {
  Grid3X3,
  List,
  Loader2,
  SlidersHorizontal,
  X
} from 'lucide-react';

export default function CarsPage() {
  const [cars, setCars] = useState<Car[]>([]);
  const [totalElements, setTotalElements] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingMore, setIsLoadingMore] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid');
  const [showFilters, setShowFilters] = useState(false);

  // Available filters (in a real app, this would come from an API)
  const [availableFilters] = useState<CarFilters>({
    makes: ['Toyota', 'Honda', 'Ford', 'Chevrolet', 'BMW', 'Mercedes-Benz', 'Audi', 'Nissan'],
    models: ['Camry', 'Civic', 'F-150', 'Silverado', '3 Series', 'C-Class', 'A4', 'Altima'],
    fuelTypes: ['GAS', 'DIESEL', 'HYBRID', 'ELECTRIC'],
    transmissions: ['MANUAL', 'AUTOMATIC', 'CVT'],
    vehicleTypes: ['PASSENGER', 'TRUCK', 'VAN', 'MOTOR'],
    conditions: ['USED', 'DAMAGED', 'ACCIDENTED', 'DERELICT'],
    priceRange: { min: 1000, max: 50000 },
    yearRange: { min: 2000, max: 2024 },
  });

  const [filters, setFilters] = useState<CarSearchRequest>({
    page: 0,
    size: 12,
    sortBy: 'createdAt',
    sortDir: 'desc',
  });

  const loadCars = useCallback(async (searchFilters: CarSearchRequest, append = false) => {
    try {
      if (!append) {
        setIsLoading(true);
        setError(null);
      } else {
        setIsLoadingMore(true);
      }

      const response: PaginatedResponse<Car> = await carService.searchCars(searchFilters);

      if (append) {
        setCars(prev => [...prev, ...response.content]);
      } else {
        setCars(response.content);
      }

      setTotalElements(response.totalElements);
      setTotalPages(response.totalPages);
      setCurrentPage(response.number);
    } catch (error: unknown) {
      const errorMessage = (error as any)?.response?.data?.message || 'Failed to load cars';
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setIsLoading(false);
      setIsLoadingMore(false);
    }
  }, []);

  // Initial load
  useEffect(() => {
    loadCars(filters);
  }, []);

  // Reload when filters change
  useEffect(() => {
    if (currentPage === 0) {
      loadCars({ ...filters, page: 0 });
    } else {
      setCurrentPage(0);
      loadCars({ ...filters, page: 0 });
    }
  }, [filters, loadCars]);

  const handleFiltersChange = (newFilters: CarSearchRequest) => {
    setFilters(newFilters);
  };

  const handleViewModeChange = (mode: 'grid' | 'list') => {
    setViewMode(mode);
  };

  const loadMore = () => {
    if (currentPage < totalPages - 1) {
      const nextPage = currentPage + 1;
      setCurrentPage(nextPage);
      loadCars({ ...filters, page: nextPage }, true);
    }
  };

  const clearError = () => {
    setError(null);
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="space-y-8">
        {/* Header */}
        <div className="text-center space-y-4">
          <h1 className="text-3xl lg:text-4xl font-bold">Browse Cars</h1>
          <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
            Find your perfect salvage vehicle from our extensive inventory
          </p>
        </div>

        {/* Search and Filters */}
        <div className="flex flex-col lg:flex-row gap-8">
          {/* Filters Sidebar - Desktop */}
          <aside className="hidden lg:block w-80 flex-shrink-0">
            <div className="sticky top-8">
              <SearchFilters
                filters={filters}
                availableFilters={availableFilters}
                onFiltersChange={handleFiltersChange}
                isLoading={isLoading}
              />
            </div>
          </aside>

          {/* Main Content */}
          <main className="flex-1 space-y-6">
            {/* Mobile Filter Toggle */}
            <div className="lg:hidden">
              <Button
                variant="outline"
                onClick={() => setShowFilters(!showFilters)}
                className="w-full"
              >
                <SlidersHorizontal className="h-4 w-4 mr-2" />
                Filters
                {Object.keys(filters).length > 2 && (
                  <span className="ml-2 bg-primary text-primary-foreground rounded-full px-2 py-1 text-xs">
                    Active
                  </span>
                )}
              </Button>
            </div>

            {/* Mobile Filters */}
            {showFilters && (
              <div className="lg:hidden">
                <SearchFilters
                  filters={filters}
                  availableFilters={availableFilters}
                  onFiltersChange={handleFiltersChange}
                  isLoading={isLoading}
                />
              </div>
            )}

            {/* Controls */}
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-4">
                {/* View Mode Toggle */}
                <div className="flex border rounded-lg">
                  <Button
                    variant={viewMode === 'grid' ? 'default' : 'ghost'}
                    size="sm"
                    onClick={() => handleViewModeChange('grid')}
                  >
                    <Grid3X3 className="h-4 w-4" />
                  </Button>
                  <Button
                    variant={viewMode === 'list' ? 'default' : 'ghost'}
                    size="sm"
                    onClick={() => handleViewModeChange('list')}
                  >
                    <List className="h-4 w-4" />
                  </Button>
                </div>

                {/* Results Count */}
                <span className="text-sm text-muted-foreground">
                  {totalElements} vehicles found
                </span>
              </div>
            </div>

            {/* Error State */}
            {error && (
              <div className="bg-destructive/10 border border-destructive/20 rounded-lg p-4">
                <div className="flex items-center justify-between">
                  <p className="text-destructive">{error}</p>
                  <Button variant="outline" size="sm" onClick={clearError}>
                    <X className="h-4 w-4" />
                  </Button>
                </div>
              </div>
            )}

            {/* Loading State */}
            {isLoading && (
              <div className="flex items-center justify-center py-12">
                <Loader2 className="h-8 w-8 animate-spin" />
                <span className="ml-2">Loading cars...</span>
              </div>
            )}

            {/* Cars Grid/List */}
            {!isLoading && cars.length > 0 && (
              <>
                <div
                  className={
                    viewMode === 'grid'
                      ? 'grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6'
                      : 'space-y-4'
                  }
                >
                  {cars.map((car) => (
                    <CarCard
                      key={car.id}
                      car={car}
                      viewMode={viewMode}
                    />
                  ))}
                </div>

                {/* Load More Button */}
                {currentPage < totalPages - 1 && (
                  <div className="text-center">
                    <Button
                      onClick={loadMore}
                      disabled={isLoadingMore}
                      size="lg"
                    >
                      {isLoadingMore ? (
                        <>
                          <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                          Loading...
                        </>
                      ) : (
                        'Load More Cars'
                      )}
                    </Button>
                  </div>
                )}
              </>
            )}

            {/* Empty State */}
            {!isLoading && !error && cars.length === 0 && (
              <div className="text-center py-12">
                <div className="text-muted-foreground">
                  <p className="text-lg mb-2">No cars found</p>
                  <p className="text-sm">Try adjusting your search filters</p>
                </div>
              </div>
            )}
          </main>
        </div>
      </div>
    </div>
  );
}