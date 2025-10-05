import React from 'react';

interface SkeletonProps {
  className?: string;
  width?: string | number;
  height?: string | number;
  variant?: 'text' | 'rectangular' | 'circular';
}

export function Skeleton({
  className = '',
  width,
  height,
  variant = 'rectangular'
}: SkeletonProps) {
  const baseClasses = 'skeleton-loading animate-pulse bg-gray-200';

  const variantClasses = {
    text: 'rounded',
    rectangular: 'rounded-md',
    circular: 'rounded-full',
  };

  const style: React.CSSProperties = {};
  if (width) style.width = typeof width === 'number' ? `${width}px` : width;
  if (height) style.height = typeof height === 'number' ? `${height}px` : height;

  return (
    <div
      className={`${baseClasses} ${variantClasses[variant]} ${className}`}
      style={style}
    />
  );
}

// Pre-built skeleton components for common use cases
export function CarCardSkeleton() {
  return (
    <div className="car-card-skeleton">
      <Skeleton className="car-image-skeleton" height={200} />
      <div className="car-content-skeleton p-4">
        <Skeleton className="car-title-skeleton" width="80%" height={24} />
        <div className="car-meta-skeleton mt-2">
          <Skeleton width="60%" height={16} />
          <Skeleton className="mt-1" width="70%" height={16} />
        </div>
        <Skeleton className="car-price-skeleton mt-3" width="40%" height={28} />
        <div className="car-features-skeleton mt-3 flex gap-2">
          <Skeleton width={40} height={20} variant="rectangular" />
          <Skeleton width={50} height={20} variant="rectangular" />
          <Skeleton width={35} height={20} variant="rectangular" />
        </div>
        <Skeleton className="car-button-skeleton mt-4" width="100%" height={40} />
      </div>
    </div>
  );
}

export function CarDetailSkeleton() {
  return (
    <div className="car-detail-skeleton">
      {/* Image Gallery Skeleton */}
      <div className="car-images-skeleton mb-6">
        <Skeleton className="main-image-skeleton" height={400} />
        <div className="thumbnail-skeleton flex gap-2 mt-3">
          <Skeleton width={80} height={60} variant="rectangular" />
          <Skeleton width={80} height={60} variant="rectangular" />
          <Skeleton width={80} height={60} variant="rectangular" />
          <Skeleton width={80} height={60} variant="rectangular" />
        </div>
      </div>

      {/* Car Info Skeleton */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <Skeleton width="60%" height={32} className="mb-4" />
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
            {Array.from({ length: 8 }).map((_, i) => (
              <div key={i} className="text-center">
                <Skeleton variant="circular" width={40} height={40} className="mx-auto mb-2" />
                <Skeleton width="80%" height={16} className="mx-auto" />
                <Skeleton width="60%" height={20} className="mx-auto mt-1" />
              </div>
            ))}
          </div>
          <Skeleton width="100%" height={120} className="mb-4" />
          <Skeleton width="100%" height={100} />
        </div>

        <div className="lg:col-span-1">
          <Skeleton width="100%" height={200} className="mb-4" />
          <Skeleton width="100%" height={150} />
        </div>
      </div>
    </div>
  );
}

export function DashboardSkeleton() {
  return (
    <div className="dashboard-skeleton">
      {/* Header Skeleton */}
      <div className="mb-6">
        <Skeleton width="50%" height={32} className="mb-2" />
        <Skeleton width="70%" height={20} />
      </div>

      {/* Stats Cards Skeleton */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        {Array.from({ length: 4 }).map((_, i) => (
          <div key={i} className="stat-card-skeleton p-6">
            <Skeleton variant="circular" width={48} height={48} className="mb-4" />
            <Skeleton width="60%" height={28} className="mb-2" />
            <Skeleton width="40%" height={16} />
          </div>
        ))}
      </div>

      {/* Table Skeleton */}
      <div className="bg-white rounded-lg p-6">
        <div className="flex justify-between items-center mb-4">
          <Skeleton width="30%" height={24} />
          <Skeleton width={120} height={32} />
        </div>

        <div className="space-y-4">
          {Array.from({ length: 5 }).map((_, i) => (
            <div key={i} className="flex items-center space-x-4 p-4 border-b">
              <Skeleton variant="circular" width={40} height={40} />
              <div className="flex-1">
                <Skeleton width="60%" height={16} className="mb-2" />
                <Skeleton width="40%" height={14} />
              </div>
              <Skeleton width={80} height={20} />
              <Skeleton width={100} height={16} />
              <div className="flex space-x-2">
                <Skeleton width={32} height={32} variant="rectangular" />
                <Skeleton width={32} height={32} variant="rectangular" />
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export function FormSkeleton() {
  return (
    <div className="form-skeleton space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <Skeleton width="30%" height={16} className="mb-2" />
          <Skeleton width="100%" height={40} />
        </div>
        <div>
          <Skeleton width="30%" height={16} className="mb-2" />
          <Skeleton width="100%" height={40} />
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <Skeleton width="30%" height={16} className="mb-2" />
          <Skeleton width="100%" height={40} />
        </div>
        <div>
          <Skeleton width="30%" height={16} className="mb-2" />
          <Skeleton width="100%" height={40} />
        </div>
      </div>

      <div>
        <Skeleton width="30%" height={16} className="mb-2" />
        <Skeleton width="100%" height={100} />
      </div>

      <div>
        <Skeleton width="30%" height={16} className="mb-2" />
        <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
          {Array.from({ length: 6 }).map((_, i) => (
            <Skeleton key={i} width="100%" height={30} />
          ))}
        </div>
      </div>

      <div className="flex justify-end space-x-4">
        <Skeleton width={80} height={40} />
        <Skeleton width={100} height={40} />
      </div>
    </div>
  );
}