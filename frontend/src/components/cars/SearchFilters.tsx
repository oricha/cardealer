'use client';

import { useState, useEffect } from 'react';
import { CarSearchRequest, CarFilters } from '@/types/car';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Badge } from '@/components/ui/Badge';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/Select';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/Card';
// import { Slider } from '@/components/ui/Slider'; // TODO: Add when Radix Slider is available
import {
  Search,
  Filter,
  X,
  ChevronDown,
  RotateCcw
} from 'lucide-react';

interface SearchFiltersProps {
  filters: CarSearchRequest;
  availableFilters: CarFilters;
  onFiltersChange: (filters: CarSearchRequest) => void;
  isLoading?: boolean;
}

export const SearchFilters: React.FC<SearchFiltersProps> = ({
  filters,
  availableFilters,
  onFiltersChange,
  isLoading = false,
}) => {
  const [isExpanded, setIsExpanded] = useState(false);
  const [localFilters, setLocalFilters] = useState<CarSearchRequest>(filters);

  // Update local filters when props change
  useEffect(() => {
    setLocalFilters(filters);
  }, [filters]);

  const updateFilters = (newFilters: Partial<CarSearchRequest>) => {
    const updated = { ...localFilters, ...newFilters };
    setLocalFilters(updated);
    onFiltersChange(updated);
  };

  const resetFilters = () => {
    const resetFilters: CarSearchRequest = {
      page: 0,
      size: 20,
    };
    setLocalFilters(resetFilters);
    onFiltersChange(resetFilters);
  };

  const getActiveFiltersCount = () => {
    let count = 0;
    if (localFilters.make) count++;
    if (localFilters.model) count++;
    if (localFilters.fuelType) count++;
    if (localFilters.transmission) count++;
    if (localFilters.vehicleType) count++;
    if (localFilters.condition) count++;
    if (localFilters.minPrice || localFilters.maxPrice) count++;
    if (localFilters.minYear || localFilters.maxYear) count++;
    if (localFilters.maxMileage) count++;
    return count;
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(price);
  };

  return (
    <Card>
      <CardHeader className="pb-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Filter className="h-5 w-5" />
            <CardTitle className="text-lg">Filters</CardTitle>
            {getActiveFiltersCount() > 0 && (
              <Badge variant="secondary">
                {getActiveFiltersCount()} active
              </Badge>
            )}
          </div>

          <div className="flex items-center gap-2">
            {getActiveFiltersCount() > 0 && (
              <Button
                variant="outline"
                size="sm"
                onClick={resetFilters}
                disabled={isLoading}
              >
                <RotateCcw className="h-4 w-4 mr-1" />
                Reset
              </Button>
            )}
            <Button
              variant="ghost"
              size="sm"
              onClick={() => setIsExpanded(!isExpanded)}
            >
              <ChevronDown className={`h-4 w-4 transition-transform ${isExpanded ? 'rotate-180' : ''}`} />
            </Button>
          </div>
        </div>
      </CardHeader>

      <CardContent className="space-y-6">
        {/* Quick Search */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <Input
            placeholder="Search by make, model, or description..."
            value={localFilters.query || ''}
            onChange={(e) => updateFilters({ query: e.target.value })}
            className="pl-10"
          />
        </div>

        {/* Expanded Filters */}
        {isExpanded && (
          <div className="space-y-6 pt-4 border-t">
            {/* Make and Model */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <label className="text-sm font-medium">Make</label>
                <Select
                  value={localFilters.make || ''}
                  onValueChange={(value) => updateFilters({ make: value || undefined })}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="All makes" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="">All makes</SelectItem>
                    {availableFilters.makes.map((make) => (
                      <SelectItem key={make} value={make}>
                        {make}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <label className="text-sm font-medium">Model</label>
                <Select
                  value={localFilters.model || ''}
                  onValueChange={(value) => updateFilters({ model: value || undefined })}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="All models" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="">All models</SelectItem>
                    {availableFilters.models.map((model) => (
                      <SelectItem key={model} value={model}>
                        {model}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            </div>

            {/* Vehicle Type and Condition */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <label className="text-sm font-medium">Vehicle Type</label>
                <Select
                  value={localFilters.vehicleType || ''}
                  onValueChange={(value) => updateFilters({ vehicleType: value as any || undefined })}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="All types" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="">All types</SelectItem>
                    {availableFilters.vehicleTypes.map((type) => (
                      <SelectItem key={type} value={type}>
                        {type}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <label className="text-sm font-medium">Condition</label>
                <Select
                  value={localFilters.condition || ''}
                  onValueChange={(value) => updateFilters({ condition: value as any || undefined })}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="All conditions" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="">All conditions</SelectItem>
                    {availableFilters.conditions.map((condition) => (
                      <SelectItem key={condition} value={condition}>
                        {condition}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            </div>

            {/* Fuel Type and Transmission */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <label className="text-sm font-medium">Fuel Type</label>
                <Select
                  value={localFilters.fuelType || ''}
                  onValueChange={(value) => updateFilters({ fuelType: value as any || undefined })}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="All fuel types" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="">All fuel types</SelectItem>
                    {availableFilters.fuelTypes.map((fuelType) => (
                      <SelectItem key={fuelType} value={fuelType}>
                        {fuelType}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <label className="text-sm font-medium">Transmission</label>
                <Select
                  value={localFilters.transmission || ''}
                  onValueChange={(value) => updateFilters({ transmission: value as any || undefined })}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="All transmissions" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="">All transmissions</SelectItem>
                    {availableFilters.transmissions.map((transmission) => (
                      <SelectItem key={transmission} value={transmission}>
                        {transmission}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            </div>

            {/* Price Range */}
            <div className="space-y-3">
              <label className="text-sm font-medium">Price Range</label>
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <Input
                    type="number"
                    placeholder="Min price"
                    value={localFilters.minPrice || ''}
                    onChange={(e) =>
                      updateFilters({
                        minPrice: e.target.value ? parseInt(e.target.value) : undefined,
                      })
                    }
                  />
                </div>
                <div>
                  <Input
                    type="number"
                    placeholder="Max price"
                    value={localFilters.maxPrice || ''}
                    onChange={(e) =>
                      updateFilters({
                        maxPrice: e.target.value ? parseInt(e.target.value) : undefined,
                      })
                    }
                  />
                </div>
              </div>
              <div className="flex justify-between text-sm text-muted-foreground">
                <span>{formatPrice(localFilters.minPrice || availableFilters.priceRange.min)}</span>
                <span>{formatPrice(localFilters.maxPrice || availableFilters.priceRange.max)}</span>
              </div>
            </div>

            {/* Year Range */}
            <div className="space-y-3">
              <label className="text-sm font-medium">Year Range</label>
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <Input
                    type="number"
                    placeholder="Min year"
                    value={localFilters.minYear || ''}
                    onChange={(e) =>
                      updateFilters({
                        minYear: e.target.value ? parseInt(e.target.value) : undefined,
                      })
                    }
                  />
                </div>
                <div>
                  <Input
                    type="number"
                    placeholder="Max year"
                    value={localFilters.maxYear || ''}
                    onChange={(e) =>
                      updateFilters({
                        maxYear: e.target.value ? parseInt(e.target.value) : undefined,
                      })
                    }
                  />
                </div>
              </div>
              <div className="flex justify-between text-sm text-muted-foreground">
                <span>{localFilters.minYear || availableFilters.yearRange.min}</span>
                <span>{localFilters.maxYear || availableFilters.yearRange.max}</span>
              </div>
            </div>

            {/* Max Mileage */}
            <div className="space-y-2">
              <label className="text-sm font-medium">Max Mileage</label>
              <Input
                type="number"
                placeholder="No limit"
                value={localFilters.maxMileage || ''}
                onChange={(e) =>
                  updateFilters({
                    maxMileage: e.target.value ? parseInt(e.target.value) : undefined,
                  })
                }
              />
            </div>
          </div>
        )}

        {/* Active Filters Display */}
        {getActiveFiltersCount() > 0 && (
          <div className="flex flex-wrap gap-2 pt-4 border-t">
            {localFilters.make && (
              <Badge variant="secondary" className="gap-1">
                Make: {localFilters.make}
                <X
                  className="h-3 w-3 cursor-pointer"
                  onClick={() => updateFilters({ make: undefined })}
                />
              </Badge>
            )}
            {localFilters.model && (
              <Badge variant="secondary" className="gap-1">
                Model: {localFilters.model}
                <X
                  className="h-3 w-3 cursor-pointer"
                  onClick={() => updateFilters({ model: undefined })}
                />
              </Badge>
            )}
            {localFilters.fuelType && (
              <Badge variant="secondary" className="gap-1">
                Fuel: {localFilters.fuelType}
                <X
                  className="h-3 w-3 cursor-pointer"
                  onClick={() => updateFilters({ fuelType: undefined })}
                />
              </Badge>
            )}
            {localFilters.condition && (
              <Badge variant="secondary" className="gap-1">
                Condition: {localFilters.condition}
                <X
                  className="h-3 w-3 cursor-pointer"
                  onClick={() => updateFilters({ condition: undefined })}
                />
              </Badge>
            )}
            {(localFilters.minPrice || localFilters.maxPrice) && (
              <Badge variant="secondary" className="gap-1">
                Price: {formatPrice(localFilters.minPrice || 0)} - {formatPrice(localFilters.maxPrice || 999999)}
                <X
                  className="h-3 w-3 cursor-pointer"
                  onClick={() => updateFilters({ minPrice: undefined, maxPrice: undefined })}
                />
              </Badge>
            )}
          </div>
        )}
      </CardContent>
    </Card>
  );
};