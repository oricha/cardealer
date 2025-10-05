'use client';

import { useEffect } from 'react';
import Link from 'next/link';
import { useFavorites } from '@/contexts/FavoritesContext';
import { FavoriteButton } from '@/components/favorites/FavoriteButton';
import { Button } from '@/components/ui/Button';
import { Card } from '@/components/ui/Card';

export default function FavoritesPage() {
  const { favorites, isLoading, error, clearFavorites } = useFavorites();

  if (isLoading) {
    return (
      <div className="main">
        <div className="site-breadcrumb" style={{ backgroundImage: 'url(/assets/img/breadcrumb/01.jpg)' }}>
          <div className="container">
            <h2 className="breadcrumb-title">My Favorites</h2>
            <ul className="breadcrumb-menu">
              <li><Link href="/">Home</Link></li>
              <li className="active">Favorites</li>
            </ul>
          </div>
        </div>

        <div className="favorites-section py-120">
          <div className="container">
            <div className="text-center py-5">
              <div className="spinner"></div>
              <p className="mt-3">Loading your favorites...</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="main">
      {/* Breadcrumb */}
      <div className="site-breadcrumb" style={{ backgroundImage: 'url(/assets/img/breadcrumb/01.jpg)' }}>
        <div className="container">
          <h2 className="breadcrumb-title">My Favorites</h2>
          <ul className="breadcrumb-menu">
            <li><Link href="/">Home</Link></li>
            <li className="active">Favorites</li>
          </ul>
        </div>
      </div>

      {/* Favorites Content */}
      <div className="favorites-section py-120">
        <div className="container">
          {/* Header */}
          <div className="favorites-header mb-5">
            <div className="row align-items-center">
              <div className="col-lg-8">
                <h2 className="favorites-title">My Favorite Cars</h2>
                <p className="favorites-subtitle">
                  {favorites.length > 0
                    ? `You have ${favorites.length} favorite car${favorites.length !== 1 ? 's' : ''}`
                    : 'You haven\'t saved any cars to your favorites yet'
                  }
                </p>
              </div>
              {favorites.length > 0 && (
                <div className="col-lg-4 text-lg-end">
                  <Button
                    onClick={clearFavorites}
                    className="theme-btn theme-btn2"
                  >
                    <i className="far fa-trash me-2"></i>Clear All
                  </Button>
                </div>
              )}
            </div>
          </div>

          {/* Error Display */}
          {error && (
            <div className="alert alert-danger mb-4">
              <i className="far fa-exclamation-triangle me-2"></i>
              {error}
            </div>
          )}

          {/* Favorites Grid */}
          {favorites.length === 0 ? (
            <div className="empty-favorites">
              <div className="empty-favorites-icon">
                <i className="far fa-heart"></i>
              </div>
              <h3>No Favorite Cars Yet</h3>
              <p>Start browsing cars and save your favorites for easy access later.</p>
              <Link href="/cars" className="theme-btn">
                <i className="far fa-search me-2"></i>Browse Cars
              </Link>
            </div>
          ) : (
            <>
              <div className="row">
                {favorites.map((car) => (
                  <div key={car.id} className="col-lg-4 col-md-6 mb-4">
                    <Card className="car-card favorite-car-card">
                      <div className="car-card-img">
                        <Link href={`/cars/${car.id}`}>
                          <img
                            src={car.images?.[0]?.imageUrl || '/assets/img/car/01.jpg'}
                            alt={`${car.make} ${car.model}`}
                          />
                        </Link>
                        <div className="car-card-badges">
                          <span className={`car-status status-${car.condition.toLowerCase()}`}>
                            {car.condition}
                          </span>
                        </div>
                        <div className="car-card-favorite">
                          <FavoriteButton car={car} variant="icon" />
                        </div>
                      </div>

                      <div className="car-card-content">
                        <h4 className="car-card-title">
                          <Link href={`/cars/${car.id}`}>
                            {car.make} {car.model}
                          </Link>
                        </h4>

                        <div className="car-card-meta">
                          <span className="car-year">{car.year}</span>
                          <span className="car-mileage">{car.mileage?.toLocaleString()} mi</span>
                          <span className="car-transmission">{car.transmission}</span>
                        </div>

                        <div className="car-card-price">
                          <span className="price">${car.price.toLocaleString()}</span>
                        </div>

                        <div className="car-card-features">
                          {car.features.airConditioning && (
                            <span className="feature-tag">AC</span>
                          )}
                          {car.features.airbags && (
                            <span className="feature-tag">Airbags</span>
                          )}
                          {car.features.absBrakes && (
                            <span className="feature-tag">ABS</span>
                          )}
                        </div>

                        <div className="car-card-actions">
                          <Link href={`/cars/${car.id}`} className="theme-btn w-100 mb-2">
                            <i className="far fa-eye me-2"></i>View Details
                          </Link>
                          <div className="car-card-links">
                            <FavoriteButton
                              car={car}
                              variant="text"
                              className="remove-favorite-link"
                            />
                          </div>
                        </div>
                      </div>
                    </Card>
                  </div>
                ))}
              </div>

              {/* Load More / Pagination could go here */}
            </>
          )}
        </div>
      </div>
    </div>
  );
}