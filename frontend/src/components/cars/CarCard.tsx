'use client';

import Image from 'next/image';
import Link from 'next/link';
import { Car } from '@/types/car';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import { Card, CardContent, CardFooter } from '@/components/ui/Card';
import { FavoriteButton } from '@/components/favorites/FavoriteButton';
import {
  Car as CarIcon,
  Fuel,
  Settings,
  Calendar,
  Heart,
  MapPin,
  Star,
  TrendingUp
} from 'lucide-react';
import { useState } from 'react';

interface CarCardProps {
  car: Car;
  viewMode?: 'grid' | 'list';
}

export const CarCard: React.FC<CarCardProps> = ({
  car,
  viewMode = 'grid',
}) => {
  const [imageError, setImageError] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  const primaryImage = car.images.find(img => img.displayOrder === 0) || car.images[0];

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(price);
  };

  const formatMileage = (mileage?: number) => {
    if (!mileage) return 'Unknown';
    return new Intl.NumberFormat('en-US').format(mileage) + ' miles';
  };

  const getConditionColor = (condition: Car['condition']) => {
    switch (condition) {
      case 'DAMAGED':
        return 'destructive';
      case 'ACCIDENTED':
        return 'destructive';
      case 'USED':
        return 'secondary';
      case 'DERELICT':
        return 'outline';
      default:
        return 'default';
    }
  };

  const getFuelIcon = (fuelType: Car['fuelType']) => {
    switch (fuelType) {
      case 'GAS':
        return '⛽';
      case 'DIESEL':
        return '⛽';
      case 'HYBRID':
        return '🔋';
      case 'ELECTRIC':
        return '⚡';
      default:
        return '⛽';
    }
  };

  if (viewMode === 'list') {
    return (
      <Card className="overflow-hidden hover:shadow-lg transition-shadow">
        <CardContent className="p-0">
          <div className="flex">
            {/* Image */}
            <div className="relative w-48 h-32 flex-shrink-0">
              {primaryImage && !imageError ? (
                <Image
                  src={primaryImage.imageUrl}
                  alt={primaryImage.altText || `${car.make} ${car.model}`}
                  fill
                  className="object-cover"
                  onError={() => setImageError(true)}
                  onLoad={() => setIsLoading(false)}
                />
              ) : (
                <div className="w-full h-full bg-muted flex items-center justify-center">
                  <CarIcon className="h-12 w-12 text-muted-foreground" />
                </div>
              )}
              {car.isFeatured && (
                <Badge className="absolute top-2 left-2 bg-primary">
                  <Star className="h-3 w-3 mr-1" />
                  Featured
                </Badge>
              )}
            </div>

            {/* Content */}
            <div className="flex-1 p-4">
              <div className="flex justify-between items-start mb-2">
                <div>
                  <h3 className="font-semibold text-lg">
                    {car.year} {car.make} {car.model}
                  </h3>
                  <div className="flex items-center gap-2 text-sm text-muted-foreground">
                    <Calendar className="h-4 w-4" />
                    {car.year}
                    <span>•</span>
                    <span>{formatMileage(car.mileage)}</span>
                    <span>•</span>
                    <span className="flex items-center gap-1">
                      <span>{getFuelIcon(car.fuelType)}</span>
                      {car.fuelType}
                    </span>
                  </div>
                </div>
                <div className="text-right">
                  <div className="text-2xl font-bold text-primary">
                    {formatPrice(car.price)}
                  </div>
                  <Badge variant={getConditionColor(car.condition)}>
                    {car.condition}
                  </Badge>
                </div>
              </div>

              {car.description && (
                <p className="text-sm text-muted-foreground mb-3 line-clamp-2">
                  {car.description}
                </p>
              )}

              <div className="flex items-center justify-between">
                <div className="flex items-center gap-4 text-sm text-muted-foreground">
                  <span className="flex items-center gap-1">
                    <Settings className="h-4 w-4" />
                    {car.transmission}
                  </span>
                  <span className="flex items-center gap-1">
                    <MapPin className="h-4 w-4" />
                    {car.vehicleType}
                  </span>
                </div>

                <div className="flex gap-2">
                  <FavoriteButton car={car} variant="icon" size="sm" />
                  <Button asChild>
                    <Link href={`/cars/${car.id}`}>
                      View Details
                    </Link>
                  </Button>
                </div>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>
    );
  }

  // Grid view (default)
  return (
    <Card className="overflow-hidden hover:shadow-lg transition-shadow group">
      <CardContent className="p-0">
        {/* Image */}
        <div className="relative aspect-[4/3] overflow-hidden">
          {primaryImage && !imageError ? (
            <Image
              src={primaryImage.imageUrl}
              alt={primaryImage.altText || `${car.make} ${car.model}`}
              fill
              className="object-cover group-hover:scale-105 transition-transform duration-300"
              onError={() => setImageError(true)}
              onLoad={() => setIsLoading(false)}
            />
          ) : (
            <div className="w-full h-full bg-muted flex items-center justify-center">
              <CarIcon className="h-16 w-16 text-muted-foreground" />
            </div>
          )}

          {/* Badges */}
          <div className="absolute top-3 left-3 flex flex-col gap-2">
            {car.isFeatured && (
              <Badge className="bg-primary">
                <Star className="h-3 w-3 mr-1" />
                Featured
              </Badge>
            )}
            <Badge variant={getConditionColor(car.condition)}>
              {car.condition}
            </Badge>
          </div>

          {/* Favorite button */}
          <div className="absolute top-3 right-3 opacity-0 group-hover:opacity-100 transition-opacity">
            <FavoriteButton car={car} variant="icon" size="sm" />
          </div>

          {/* Price overlay */}
          <div className="absolute bottom-3 right-3">
            <div className="bg-black/80 text-white px-3 py-1 rounded-lg">
              <span className="font-bold text-lg">{formatPrice(car.price)}</span>
            </div>
          </div>
        </div>

        {/* Content */}
        <div className="p-4">
          <h3 className="font-semibold text-lg mb-2 line-clamp-1">
            {car.year} {car.make} {car.model}
          </h3>

          <div className="space-y-2 mb-4">
            <div className="flex items-center justify-between text-sm text-muted-foreground">
              <span className="flex items-center gap-1">
                <Calendar className="h-4 w-4" />
                {car.year}
              </span>
              <span>{formatMileage(car.mileage)}</span>
            </div>

            <div className="flex items-center justify-between text-sm text-muted-foreground">
              <span className="flex items-center gap-1">
                <span>{getFuelIcon(car.fuelType)}</span>
                {car.fuelType}
              </span>
              <span className="flex items-center gap-1">
                <Settings className="h-4 w-4" />
                {car.transmission}
              </span>
            </div>
          </div>

          {car.description && (
            <p className="text-sm text-muted-foreground mb-4 line-clamp-2">
              {car.description}
            </p>
          )}

          <CardFooter className="p-0">
            <Button asChild className="w-full">
              <Link href={`/cars/${car.id}`}>
                View Details
              </Link>
            </Button>
          </CardFooter>
        </div>
      </CardContent>
    </Card>
  );
};