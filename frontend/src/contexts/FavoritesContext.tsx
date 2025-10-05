'use client';

import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { Car } from '@/types/car';

interface FavoritesContextType {
  favorites: Car[];
  favoriteIds: Set<string>;
  isLoading: boolean;
  error: string | null;
  addToFavorites: (car: Car) => Promise<void>;
  removeFromFavorites: (carId: string) => Promise<void>;
  isFavorite: (carId: string) => boolean;
  toggleFavorite: (car: Car) => Promise<void>;
  clearFavorites: () => void;
  refreshFavorites: () => Promise<void>;
}

const FavoritesContext = createContext<FavoritesContextType | undefined>(undefined);

interface FavoritesProviderProps {
  children: ReactNode;
}

export function FavoritesProvider({ children }: FavoritesProviderProps) {
  const [favorites, setFavorites] = useState<Car[]>([]);
  const [favoriteIds, setFavoriteIds] = useState<Set<string>>(new Set());
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Load favorites from localStorage on mount
  useEffect(() => {
    loadFavoritesFromStorage();
  }, []);

  const loadFavoritesFromStorage = () => {
    try {
      const stored = localStorage.getItem('favorites');
      if (stored) {
        const favoriteCars: Car[] = JSON.parse(stored);
        setFavorites(favoriteCars);
        setFavoriteIds(new Set(favoriteCars.map(car => car.id)));
      }
    } catch (err) {
      console.error('Failed to load favorites from storage:', err);
    }
  };

  const saveFavoritesToStorage = (newFavorites: Car[]) => {
    try {
      localStorage.setItem('favorites', JSON.stringify(newFavorites));
    } catch (err) {
      console.error('Failed to save favorites to storage:', err);
    }
  };

  const addToFavorites = async (car: Car) => {
    try {
      setIsLoading(true);
      setError(null);

      // Add to local state immediately for better UX
      const newFavorites = [...favorites, car];
      const newFavoriteIds = new Set([...favoriteIds, car.id]);

      setFavorites(newFavorites);
      setFavoriteIds(newFavoriteIds);
      saveFavoritesToStorage(newFavorites);

      // TODO: Sync with backend when user is authenticated
      // await favoritesService.addToFavorites(car.id);

    } catch (err: unknown) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to add to favorites';
      setError(errorMessage);

      // Revert optimistic update on error
      setFavorites(favorites);
      setFavoriteIds(favoriteIds);
    } finally {
      setIsLoading(false);
    }
  };

  const removeFromFavorites = async (carId: string) => {
    try {
      setIsLoading(true);
      setError(null);

      // Remove from local state immediately for better UX
      const newFavorites = favorites.filter(car => car.id !== carId);
      const newFavoriteIds = new Set(favoriteIds);
      newFavoriteIds.delete(carId);

      setFavorites(newFavorites);
      setFavoriteIds(newFavoriteIds);
      saveFavoritesToStorage(newFavorites);

      // TODO: Sync with backend when user is authenticated
      // await favoritesService.removeFromFavorites(carId);

    } catch (err: unknown) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to remove from favorites';
      setError(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  const isFavorite = (carId: string): boolean => {
    return favoriteIds.has(carId);
  };

  const toggleFavorite = async (car: Car) => {
    if (isFavorite(car.id)) {
      await removeFromFavorites(car.id);
    } else {
      await addToFavorites(car);
    }
  };

  const clearFavorites = () => {
    setFavorites([]);
    setFavoriteIds(new Set());
    saveFavoritesToStorage([]);
  };

  const refreshFavorites = async () => {
    // TODO: Implement backend sync when user authentication is available
    // This would fetch the user's favorites from the backend and merge with local storage
    loadFavoritesFromStorage();
  };

  const value: FavoritesContextType = {
    favorites,
    favoriteIds,
    isLoading,
    error,
    addToFavorites,
    removeFromFavorites,
    isFavorite,
    toggleFavorite,
    clearFavorites,
    refreshFavorites,
  };

  return (
    <FavoritesContext.Provider value={value}>
      {children}
    </FavoritesContext.Provider>
  );
}

export function useFavorites() {
  const context = useContext(FavoritesContext);
  if (context === undefined) {
    throw new Error('useFavorites must be used within a FavoritesProvider');
  }
  return context;
}