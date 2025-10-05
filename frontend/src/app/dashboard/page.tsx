'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { Car, PaginatedResponse } from '@/types/car';
import { carService } from '@/lib/api/cars';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';
import { Badge } from '@/components/ui/Badge';
import { CarForm } from '@/components/dashboard/CarForm';

interface DashboardStats {
  totalCars: number;
  activeCars: number;
  soldCars: number;
  totalRevenue: number;
  recentViews: number;
}

export default function DashboardPage() {
  const [cars, setCars] = useState<Car[]>([]);
  const [stats, setStats] = useState<DashboardStats>({
    totalCars: 0,
    activeCars: 0,
    soldCars: 0,
    totalRevenue: 0,
    recentViews: 0,
  });
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingStats, setIsLoadingStats] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [showAddForm, setShowAddForm] = useState(false);
  const [editingCar, setEditingCar] = useState<Car | null>(null);
  const router = useRouter();

  useEffect(() => {
    loadDashboardData();
  }, [currentPage]);

  const loadDashboardData = async () => {
    try {
      setIsLoading(true);
      setError(null);

      // Load user's cars
      const carsResponse: PaginatedResponse<Car> = await carService.getMyCars({
        page: currentPage,
        size: 10,
        sortBy: 'createdAt',
        sortDir: 'desc',
      });

      setCars(carsResponse.content);
      setTotalPages(carsResponse.totalPages);

      // Calculate basic stats from cars data
      const totalCars = carsResponse.totalElements;
      const activeCars = carsResponse.content.filter(car => car.isActive).length;
      const totalRevenue = carsResponse.content
        .filter(car => !car.isActive) // Assuming inactive means sold
        .reduce((sum, car) => sum + car.price, 0);

      setStats(prev => ({
        ...prev,
        totalCars,
        activeCars,
        totalRevenue,
      }));

    } catch (err: unknown) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to load dashboard data';
      setError(errorMessage);
      console.error('Dashboard load error:', err);
    } finally {
      setIsLoading(false);
      setIsLoadingStats(false);
    }
  };

  const handleDeleteCar = async (carId: string) => {
    if (!confirm('Are you sure you want to delete this car listing?')) return;

    try {
      await carService.deleteCar(carId);
      await loadDashboardData(); // Reload data
    } catch (err: unknown) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to delete car';
      setError(errorMessage);
    }
  };

  const handleToggleActive = async (carId: string, currentStatus: boolean) => {
    try {
      await carService.updateCar(carId, { isActive: !currentStatus });
      await loadDashboardData(); // Reload data
    } catch (err: unknown) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to update car status';
      setError(errorMessage);
    }
  };

  if (isLoading && cars.length === 0) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex justify-center items-center min-h-[400px]">
          <div className="spinner"></div>
          <span className="ml-3">Loading dashboard...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="main">
      {/* Breadcrumb */}
      <div className="site-breadcrumb" style={{ backgroundImage: 'url(/assets/img/breadcrumb/01.jpg)' }}>
        <div className="container">
          <h2 className="breadcrumb-title">Dealer Dashboard</h2>
          <ul className="breadcrumb-menu">
            <li><Link href="/">Home</Link></li>
            <li className="active">Dashboard</li>
          </ul>
        </div>
      </div>

      {/* Dashboard Content */}
      <div className="dashboard-section py-120">
        <div className="container">
          {/* Header */}
          <div className="dashboard-header mb-5">
            <div className="row align-items-center">
              <div className="col-lg-8">
                <h2 className="dashboard-title">Welcome to Your Dashboard</h2>
                <p className="dashboard-subtitle">Manage your inventory, track sales, and grow your business</p>
              </div>
              <div className="col-lg-4 text-lg-end">
                <Button
                  onClick={() => setShowAddForm(true)}
                  className="theme-btn"
                >
                  <i className="far fa-plus me-2"></i>Add New Car
                </Button>
              </div>
            </div>
          </div>

          {/* Stats Cards */}
          <div className="row mb-5">
            <div className="col-lg-3 col-md-6">
              <Card className="stat-card">
                <div className="stat-icon">
                  <i className="flaticon-car"></i>
                </div>
                <div className="stat-content">
                  <h3>{stats.totalCars}</h3>
                  <p>Total Cars</p>
                </div>
              </Card>
            </div>
            <div className="col-lg-3 col-md-6">
              <Card className="stat-card">
                <div className="stat-icon">
                  <i className="flaticon-check"></i>
                </div>
                <div className="stat-content">
                  <h3>{stats.activeCars}</h3>
                  <p>Active Listings</p>
                </div>
              </Card>
            </div>
            <div className="col-lg-3 col-md-6">
              <Card className="stat-card">
                <div className="stat-icon">
                  <i className="flaticon-dollar-sign"></i>
                </div>
                <div className="stat-content">
                  <h3>${stats.totalRevenue.toLocaleString()}</h3>
                  <p>Total Revenue</p>
                </div>
              </Card>
            </div>
            <div className="col-lg-3 col-md-6">
              <Card className="stat-card">
                <div className="stat-icon">
                  <i className="flaticon-eye"></i>
                </div>
                <div className="stat-content">
                  <h3>{stats.recentViews}</h3>
                  <p>Recent Views</p>
                </div>
              </Card>
            </div>
          </div>

          {/* Error Display */}
          {error && (
            <div className="alert alert-danger mb-4">
              <i className="far fa-exclamation-triangle me-2"></i>
              {error}
            </div>
          )}

          {/* Cars Management */}
          <Card className="cars-management">
            <div className="cars-management-header">
              <h3>Your Car Listings</h3>
              <div className="cars-management-actions">
                <Button
                  onClick={() => setShowAddForm(true)}
                  className="theme-btn"
                >
                  <i className="far fa-plus me-2"></i>Add Car
                </Button>
              </div>
            </div>

            {isLoading ? (
              <div className="text-center py-5">
                <div className="spinner"></div>
                <p className="mt-3">Loading your cars...</p>
              </div>
            ) : cars.length === 0 ? (
              <div className="empty-state">
                <i className="flaticon-car"></i>
                <h4>No Cars Listed</h4>
                <p>You haven't listed any cars yet. Add your first car to get started!</p>
                <Button
                  onClick={() => setShowAddForm(true)}
                  className="theme-btn mt-3"
                >
                  Add Your First Car
                </Button>
              </div>
            ) : (
              <>
                <div className="cars-table-container">
                  <div className="table-responsive">
                    <table className="cars-table">
                      <thead>
                        <tr>
                          <th>Car Details</th>
                          <th>Price</th>
                          <th>Status</th>
                          <th>Listed Date</th>
                          <th>Actions</th>
                        </tr>
                      </thead>
                      <tbody>
                        {cars.map((car) => (
                          <tr key={car.id}>
                            <td>
                              <div className="car-info">
                                <div className="car-image">
                                  <img
                                    src={car.images?.[0]?.imageUrl || '/assets/img/car/01.jpg'}
                                    alt={`${car.make} ${car.model}`}
                                  />
                                </div>
                                <div className="car-details">
                                  <h5>{car.make} {car.model}</h5>
                                  <p>{car.year} • {car.fuelType} • {car.transmission}</p>
                                  <small className="text-muted">
                                    {car.mileage?.toLocaleString()} miles • {car.condition}
                                  </small>
                                </div>
                              </div>
                            </td>
                            <td>
                              <span className="car-price">${car.price.toLocaleString()}</span>
                            </td>
                            <td>
                              <Badge
                                className={car.isActive ? 'status-active' : 'status-inactive'}
                              >
                                {car.isActive ? 'Active' : 'Inactive'}
                              </Badge>
                            </td>
                            <td>
                              {new Date(car.createdAt).toLocaleDateString()}
                            </td>
                            <td>
                              <div className="action-buttons">
                                <Button
                                  onClick={() => router.push(`/cars/${car.id}`)}
                                  className="btn btn-sm btn-outline-primary me-2"
                                >
                                  <i className="far fa-eye"></i>
                                </Button>
                                <Button
                                  onClick={() => setEditingCar(car)}
                                  className="btn btn-sm btn-outline-secondary me-2"
                                >
                                  <i className="far fa-edit"></i>
                                </Button>
                                <Button
                                  onClick={() => handleToggleActive(car.id, car.isActive)}
                                  className={`btn btn-sm me-2 ${
                                    car.isActive ? 'btn-warning' : 'btn-success'
                                  }`}
                                >
                                  <i className={`far fa-${car.isActive ? 'pause' : 'play'}`}></i>
                                </Button>
                                <Button
                                  onClick={() => handleDeleteCar(car.id)}
                                  className="btn btn-sm btn-outline-danger"
                                >
                                  <i className="far fa-trash"></i>
                                </Button>
                              </div>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>

                {/* Pagination */}
                {totalPages > 1 && (
                  <div className="pagination-container">
                    <div className="pagination-info">
                      Showing page {currentPage + 1} of {totalPages}
                    </div>
                    <div className="pagination-buttons">
                      <Button
                        onClick={() => setCurrentPage(prev => Math.max(0, prev - 1))}
                        disabled={currentPage === 0}
                        className="btn btn-outline-secondary me-2"
                      >
                        Previous
                      </Button>
                      <Button
                        onClick={() => setCurrentPage(prev => Math.min(totalPages - 1, prev + 1))}
                        disabled={currentPage === totalPages - 1}
                        className="btn btn-outline-secondary"
                      >
                        Next
                      </Button>
                    </div>
                  </div>
                )}
              </>
            )}
          </Card>
        </div>
      </div>

      {/* Car Form Modal */}
      {showAddForm && (
        <CarForm
          onClose={() => setShowAddForm(false)}
          onSuccess={loadDashboardData}
        />
      )}

      {editingCar && (
        <CarForm
          car={editingCar}
          onClose={() => setEditingCar(null)}
          onSuccess={loadDashboardData}
        />
      )}
    </div>
  );
}