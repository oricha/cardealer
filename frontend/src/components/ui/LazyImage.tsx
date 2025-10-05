'use client';

import { useEffect, useRef, useState } from 'react';
import Image from 'next/image';
import { LazyImageLoader } from '@/lib/performance';

interface LazyImageProps {
  src: string;
  alt: string;
  width?: number;
  height?: number;
  className?: string;
  placeholder?: 'blur' | 'empty';
  blurDataURL?: string;
  priority?: boolean;
  quality?: number;
  onLoad?: () => void;
  onError?: () => void;
}

export function LazyImage({
  src,
  alt,
  width,
  height,
  className = '',
  placeholder = 'empty',
  blurDataURL,
  priority = false,
  quality = 75,
  onLoad,
  onError,
}: LazyImageProps) {
  const [isLoaded, setIsLoaded] = useState(false);
  const [hasError, setHasError] = useState(false);
  const [imageSrc, setImageSrc] = useState(priority ? src : '');
  const imgRef = useRef<HTMLImageElement>(null);

  useEffect(() => {
    const lazyLoader = LazyImageLoader.getInstance();

    if (!priority && imgRef.current) {
      lazyLoader.observeImage(imgRef.current);
    }

    return () => {
      // Cleanup if component unmounts
      if (imgRef.current) {
        const observer = lazyLoader['imageObserver'];
        if (observer) {
          observer.unobserve(imgRef.current);
        }
      }
    };
  }, [priority]);

  const handleLoad = () => {
    setIsLoaded(true);
    onLoad?.();
  };

  const handleError = () => {
    setHasError(true);
    onError?.();
  };

  // For priority images, load immediately
  if (priority) {
    return (
      <Image
        src={src}
        alt={alt}
        width={width}
        height={height}
        className={`${className} ${isLoaded ? 'loaded' : ''}`}
        placeholder={placeholder}
        blurDataURL={blurDataURL}
        quality={quality}
        onLoad={handleLoad}
        onError={handleError}
      />
    );
  }

  // For lazy-loaded images
  return (
    <div className={`lazy-image-container ${className}`}>
      {hasError ? (
        <div className="lazy-image-error">
          <i className="far fa-image"></i>
        </div>
      ) : (
        <>
          {!isLoaded && (
            <div className="lazy-image-placeholder">
              <div className="skeleton-loading"></div>
            </div>
          )}
          <img
            ref={imgRef}
            src={imageSrc}
            alt={alt}
            width={width}
            height={height}
            className={`lazy-image ${isLoaded ? 'loaded' : ''}`}
            onLoad={handleLoad}
            onError={handleError}
            data-src={src}
            style={{
              opacity: isLoaded ? 1 : 0,
              transition: 'opacity 0.3s ease',
            }}
          />
        </>
      )}
    </div>
  );
}