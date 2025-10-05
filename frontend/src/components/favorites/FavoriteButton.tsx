'use client';

import { useState } from 'react';
import { Car } from '@/types/car';
import { useFavorites } from '@/contexts/FavoritesContext';
import { Button } from '@/components/ui/Button';

interface FavoriteButtonProps {
  car: Car;
  variant?: 'button' | 'icon' | 'text';
  size?: 'sm' | 'md' | 'lg';
  className?: string;
  showText?: boolean;
}

export function FavoriteButton({
  car,
  variant = 'icon',
  size = 'md',
  className = '',
  showText = false
}: FavoriteButtonProps) {
  const { isFavorite, toggleFavorite, isLoading } = useFavorites();
  const [isAnimating, setIsAnimating] = useState(false);

  const isFav = isFavorite(car.id);

  const handleToggle = async () => {
    if (isLoading) return;

    setIsAnimating(true);
    await toggleFavorite(car);

    // Reset animation after it completes
    setTimeout(() => setIsAnimating(false), 300);
  };

  const sizeClasses = {
    sm: 'btn-sm',
    md: '',
    lg: 'btn-lg'
  };

  const variantClasses = {
    button: 'theme-btn theme-btn2',
    icon: 'favorite-icon-btn',
    text: 'favorite-text-btn'
  };

  if (variant === 'button') {
    return (
      <Button
        onClick={handleToggle}
        disabled={isLoading}
        className={`${variantClasses[variant]} ${sizeClasses[size]} ${className} ${
          isFav ? 'favorited' : ''
        }`}
      >
        <i className={`far fa-heart me-2 ${isFav ? 'fas' : 'far'} ${isAnimating ? 'fa-beat' : ''}`}></i>
        {showText && (isFav ? 'Remove from Favorites' : 'Add to Favorites')}
      </Button>
    );
  }

  if (variant === 'text') {
    return (
      <button
        onClick={handleToggle}
        disabled={isLoading}
        className={`favorite-text-btn ${className} ${isFav ? 'favorited' : ''}`}
      >
        <i className={`far fa-heart me-2 ${isFav ? 'fas' : 'far'} ${isAnimating ? 'fa-beat' : ''}`}></i>
        {isFav ? 'Remove from Favorites' : 'Add to Favorites'}
      </button>
    );
  }

  // Icon variant (default)
  return (
    <button
      onClick={handleToggle}
      disabled={isLoading}
      className={`favorite-icon-btn ${sizeClasses[size]} ${className} ${
        isFav ? 'favorited' : ''
      }`}
      title={isFav ? 'Remove from Favorites' : 'Add to Favorites'}
    >
      <i className={`far fa-heart ${isFav ? 'fas' : 'far'} ${isAnimating ? 'fa-beat' : ''}`}></i>
    </button>
  );
}