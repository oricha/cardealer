'use client';

import { useState, useEffect, useCallback } from 'react';
import Link from 'next/link';
import { Car, CarSearchRequest, CarFilters, PaginatedResponse } from '@/types/car';
import { carService } from '@/lib/api/cars';
import { toast } from '@/lib/utils/toast';

export default function CarsPage() {
  const [cars, setCars] = useState<Car[]>([]);
  const [totalElements, setTotalElements] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingMore, setIsLoadingMore] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid');

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

  useEffect(() => {
    loadCars(filters);
  }, []);

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

  const loadMore = () => {
    if (currentPage < totalPages - 1) {
      const nextPage = currentPage + 1;
      setCurrentPage(nextPage);
      loadCars({ ...filters, page: nextPage }, true);
    }
  };

  return (
    <main className="main">
      {/* Breadcrumb */}
      <div className="breadcrumb-area">
        <div className="container">
          <div className="row">
            <div className="col-12">
              <div className="breadcrumb-wrap">
                <nav aria-label="breadcrumb">
                  <ul className="breadcrumb">
                    <li className="breadcrumb-item">
                      <Link href="/">Home</Link>
                    </li>
                    <li className="breadcrumb-item active" aria-current="page">
                      Cars
                    </li>
                  </ul>
                </nav>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Page Header */}
      <div className="page-header-area">
        <div className="container">
          <div className="row">
            <div className="col-12">
              <div className="page-header-content">
                <h2 className="page-title">Browse Cars</h2>
                <p className="page-subtitle">
                  Find your perfect salvage vehicle from our extensive inventory
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Inventory Section */}
      <div className="inventory-area py-120">
        <div className="container">
          <div className="row">
            {/* Sidebar */}
            <div className="col-lg-4">
              <div className="sidebar">
                <div className="widget">
                  <h4 className="widget-title">Search Filters</h4>
                  <form className="search-form">
                    <div className="form-group">
                      <label>Make</label>
                      <select 
                        className="form-control"
                        value={filters.make || ''}
                        onChange={(e) => setFilters({...filters, make: e.target.value})}
                      >
                        <option value="">All Makes</option>
                        <option value="Toyota">Toyota</option>
                        <option value="Honda">Honda</option>
                        <option value="Ford">Ford</option>
                        <option value="Chevrolet">Chevrolet</option>
                        <option value="BMW">BMW</option>
                        <option value="Mercedes-Benz">Mercedes-Benz</option>
                        <option value="Audi">Audi</option>
                        <option value="Nissan">Nissan</option>
                      </select>
                    </div>
                    
                    <div className="form-group">
                      <label>Condition</label>
                      <select 
                        className="form-control"
                        value={filters.condition || ''}
                        onChange={(e) => setFilters({...filters, condition: e.target.value})}
                      >
                        <option value="">All Conditions</option>
                        <option value="USED">Used</option>
                        <option value="DAMAGED">Damaged</option>
                        <option value="ACCIDENTED">Accidented</option>
                        <option value="DERELICT">Derelict</option>
                      </select>
                    </div>

                    <div className="form-group">
                      <label>Price Range</label>
                      <select 
                        className="form-control"
                        value={filters.maxPrice ? `0-${filters.maxPrice}` : ''}
                        onChange={(e) => {
                          const value = e.target.value;
                          if (value) {
                            const maxPrice = parseInt(value.split('-')[1]);
                            setFilters({...filters, maxPrice});
                          } else {
                            setFilters({...filters, maxPrice: undefined});
                          }
                        }}
                      >
                        <option value="">All Prices</option>
                        <option value="0-5000">$0 - $5,000</option>
                        <option value="0-10000">$0 - $10,000</option>
                        <option value="0-20000">$0 - $20,000</option>
                        <option value="0-50000">$0 - $50,000</option>
                      </select>
                    </div>

                    <div className="form-group">
                      <label>Year</label>
                      <select 
                        className="form-control"
                        value={filters.minYear || ''}
                        onChange={(e) => setFilters({...filters, minYear: e.target.value ? parseInt(e.target.value) : undefined})}
                      >
                        <option value="">All Years</option>
                        <option value="2020">2020+</option>
                        <option value="2015">2015+</option>
                        <option value="2010">2010+</option>
                        <option value="2005">2005+</option>
                      </select>
                    </div>

                    <button type="button" className="theme-btn w-100">
                      <i className="far fa-search"></i> Search
                    </button>
                  </form>
                </div>
              </div>
            </div>

            {/* Main Content */}
            <div className="col-lg-8">
              <div className="inventory-content">
                {/* Header */}
                <div className="inventory-header">
                  <div className="row align-items-center">
                    <div className="col-md-6">
                      <div className="inventory-info">
                        <h4>Showing {cars.length} of {totalElements} cars</h4>
                      </div>
                    </div>
                    <div className="col-md-6">
                      <div className="inventory-view">
                        <div className="view-toggle">
                          <button 
                            className={`view-btn ${viewMode === 'grid' ? 'active' : ''}`}
                            onClick={() => setViewMode('grid')}
                          >
                            <i className="far fa-th"></i>
                          </button>
                          <button 
                            className={`view-btn ${viewMode === 'list' ? 'active' : ''}`}
                            onClick={() => setViewMode('list')}
                          >
                            <i className="far fa-list"></i>
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                {/* Loading State */}
                {isLoading && (
                  <div className="text-center py-5">
                    <div className="spinner"></div>
                    <p>Loading cars...</p>
                  </div>
                )}

                {/* Error State */}
                {error && (
                  <div className="alert alert-danger">
                    <p>{error}</p>
                  </div>
                )}

                {/* Cars Grid/List */}
                {!isLoading && cars.length > 0 && (
                  <>
                    <div className={`inventory-grid ${viewMode === 'list' ? 'list-view' : ''}`}>
                      {cars.map((car) => (
                        <div key={car.id} className="inventory-item">
                          <div className="inventory-item-img">
                            <Link href={`/cars/${car.id}`}>
                              <img 
                                src={car.images?.[0]?.imageUrl || '/assets/img/car/01.jpg'} 
                                alt={`${car.make} ${car.model}`}
                              />
                            </Link>
                            <div className="inventory-item-badge">
                              <span className="badge">{car.condition}</span>
                            </div>
                          </div>
                          <div className="inventory-item-content">
                            <div className="inventory-item-meta">
                              <span className="inventory-item-meta-item">
                                <i className="far fa-calendar"></i>
                                {car.year}
                              </span>
                              <span className="inventory-item-meta-item">
                                <i className="far fa-gauge"></i>
                                {car.mileage?.toLocaleString()} mi
                              </span>
                              <span className="inventory-item-meta-item">
                                <i className="far fa-cog"></i>
                                {car.transmission}
                              </span>
                            </div>
                            <h4 className="inventory-item-title">
                              <Link href={`/cars/${car.id}`}>
                                {car.make} {car.model}
                              </Link>
                            </h4>
                            <p className="inventory-item-desc">
                              {car.description?.substring(0, 100)}...
                            </p>
                            <div className="inventory-item-bottom">
                              <div className="inventory-item-price">
                                <span className="price">${car.price.toLocaleString()}</span>
                              </div>
                              <div className="inventory-item-btn">
                                <Link href={`/cars/${car.id}`} className="theme-btn">
                                  View Details
                                </Link>
                              </div>
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>

                    {/* Load More */}
                    {currentPage < totalPages - 1 && (
                      <div className="text-center mt-4">
                        <button 
                          className="theme-btn"
                          onClick={loadMore}
                          disabled={isLoadingMore}
                        >
                          {isLoadingMore ? 'Loading...' : 'Load More Cars'}
                        </button>
                      </div>
                    )}
                  </>
                )}

                {/* Empty State */}
                {!isLoading && !error && cars.length === 0 && (
                  <div className="text-center py-5">
                    <div className="empty-state">
                      <i className="far fa-car fa-3x mb-3"></i>
                      <h4>No cars found</h4>
                      <p>Try adjusting your search filters</p>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}