// Performance monitoring and optimization utilities

export interface PerformanceMetrics {
  pageLoadTime: number;
  firstContentfulPaint: number;
  largestContentfulPaint: number;
  firstInputDelay: number;
  cumulativeLayoutShift: number;
}

export class PerformanceMonitor {
  private static instance: PerformanceMonitor;
  private metrics: Partial<PerformanceMetrics> = {};
  private observers: PerformanceObserver[] = [];

  static getInstance(): PerformanceMonitor {
    if (!PerformanceMonitor.instance) {
      PerformanceMonitor.instance = new PerformanceMonitor();
    }
    return PerformanceMonitor.instance;
  }

  startMonitoring(): void {
    this.measurePageLoadTime();
    this.observeWebVitals();
    this.monitorResourceLoading();
  }

  stopMonitoring(): void {
    this.observers.forEach(observer => observer.disconnect());
    this.observers = [];
  }

  private measurePageLoadTime(): void {
    if (typeof window !== 'undefined') {
      window.addEventListener('load', () => {
        const navigation = performance.getEntriesByType('navigation')[0] as PerformanceNavigationTiming;
        if (navigation) {
          this.metrics.pageLoadTime = navigation.loadEventEnd - navigation.fetchStart;
          this.logMetric('pageLoadTime', this.metrics.pageLoadTime);
        }
      });
    }
  }

  private observeWebVitals(): void {
    if (typeof window !== 'undefined' && 'PerformanceObserver' in window) {
      // Observe Largest Contentful Paint (LCP)
      try {
        const lcpObserver = new PerformanceObserver((list) => {
          const entries = list.getEntries();
          const lastEntry = entries[entries.length - 1];
          this.metrics.largestContentfulPaint = lastEntry.startTime;
          this.logMetric('largestContentfulPaint', lastEntry.startTime);
        });
        lcpObserver.observe({ entryTypes: ['largest-contentful-paint'] });
        this.observers.push(lcpObserver);
      } catch (e) {
        console.warn('LCP observer not supported');
      }

      // Observe First Input Delay (FID)
      try {
        const fidObserver = new PerformanceObserver((list) => {
          const entries = list.getEntries();
          entries.forEach((entry) => {
            const perfEntry = entry as PerformanceEventTiming & { processingStart: number };
            this.metrics.firstInputDelay = perfEntry.processingStart - entry.startTime;
            this.logMetric('firstInputDelay', this.metrics.firstInputDelay);
          });
        });
        fidObserver.observe({ entryTypes: ['first-input'] });
        this.observers.push(fidObserver);
      } catch (e) {
        console.warn('FID observer not supported');
      }

      // Observe Cumulative Layout Shift (CLS)
      try {
        const clsObserver = new PerformanceObserver((list) => {
          let clsValue = 0;
          const entries = list.getEntries();
          entries.forEach((entry) => {
            const layoutEntry = entry as unknown as { hadRecentInput?: boolean; value: number };
            if (!layoutEntry.hadRecentInput) {
              clsValue += layoutEntry.value;
            }
          });
          this.metrics.cumulativeLayoutShift = clsValue;
          this.logMetric('cumulativeLayoutShift', clsValue);
        });
        clsObserver.observe({ entryTypes: ['layout-shift'] });
        this.observers.push(clsObserver);
      } catch (e) {
        console.warn('CLS observer not supported');
      }
    }
  }

  private monitorResourceLoading(): void {
    if (typeof window !== 'undefined') {
      window.addEventListener('load', () => {
        const resources = performance.getEntriesByType('resource');
        const slowResources = resources.filter((r: any) => r.duration > 1000);

        if (slowResources.length > 0) {
          console.warn('Slow resources detected:', slowResources.map(r => ({
            name: r.name,
            duration: r.duration,
            size: (r as any).transferSize,
          })));
        }
      });
    }
  }

  private logMetric(name: keyof PerformanceMetrics, value: number): void {
    console.log(`Performance Metric - ${name}: ${value}ms`);

    // TODO: Send to analytics service
    // analytics.track('performance_metric', { name, value, timestamp: Date.now() });
  }

  getMetrics(): Partial<PerformanceMetrics> {
    return { ...this.metrics };
  }
}

// Image lazy loading utility
export class LazyImageLoader {
  private static instance: LazyImageLoader;
  private imageObserver?: IntersectionObserver;
  private loadedImages = new Set<string>();

  static getInstance(): LazyImageLoader {
    if (!LazyImageLoader.instance) {
      LazyImageLoader.instance = new LazyImageLoader();
    }
    return LazyImageLoader.instance;
  }

  initialize(): void {
    if (typeof window !== 'undefined' && 'IntersectionObserver' in window) {
      this.imageObserver = new IntersectionObserver(
        (entries) => {
          entries.forEach((entry) => {
            if (entry.isIntersecting) {
              this.loadImage(entry.target as HTMLImageElement);
              this.imageObserver?.unobserve(entry.target);
            }
          });
        },
        {
          rootMargin: '50px 0px',
          threshold: 0.01,
        }
      );
    }
  }

  observeImage(img: HTMLImageElement): void {
    if (this.imageObserver && !this.loadedImages.has(img.src)) {
      this.imageObserver.observe(img);
    }
  }

  private loadImage(img: HTMLImageElement): void {
    if (this.loadedImages.has(img.src)) return;

    const startTime = performance.now();

    img.onload = () => {
      const loadTime = performance.now() - startTime;
      console.log(`Image loaded: ${img.src} (${loadTime.toFixed(2)}ms)`);
      this.loadedImages.add(img.src);

      // Add fade-in effect
      img.style.opacity = '0';
      img.style.transition = 'opacity 0.3s ease';

      // Force reflow
      img.offsetHeight;

      img.style.opacity = '1';
    };

    img.onerror = () => {
      console.warn(`Failed to load image: ${img.src}`);
      this.loadedImages.add(img.src); // Don't retry
    };

    // Set src to trigger load
    if (img.dataset.src) {
      img.src = img.dataset.src;
    }
  }

  disconnect(): void {
    if (this.imageObserver) {
      this.imageObserver.disconnect();
    }
  }
}

// Bundle optimization utilities
export class BundleOptimizer {
  static async preloadCriticalResources(): Promise<void> {
    if (typeof window !== 'undefined') {
      const criticalImages = [
        '/assets/img/logo.jpg',
        '/assets/img/car/01.jpg',
        // Add other critical images
      ];

      const preloadPromises = criticalImages.map(src => {
        return new Promise<void>((resolve) => {
          const link = document.createElement('link');
          link.rel = 'preload';
          link.as = 'image';
          link.href = src;
          link.onload = () => resolve();
          link.onerror = () => resolve(); // Don't fail on missing images
          document.head.appendChild(link);
        });
      });

      await Promise.all(preloadPromises);
    }
  }

  static optimizeImages(): void {
    if (typeof window !== 'undefined') {
      const images = document.querySelectorAll('img[data-src]');

      images.forEach((img) => {
        const htmlImg = img as HTMLImageElement;
        if (htmlImg.dataset.src) {
          LazyImageLoader.getInstance().observeImage(htmlImg);
        }
      });
    }
  }
}

// Memory management utilities
export class MemoryManager {
  static forceGarbageCollection(): void {
    if (typeof window !== 'undefined' && (window as any).gc) {
      (window as any).gc();
    }
  }

  static cleanupEventListeners(): void {
    // Remove unused event listeners and clean up
    document.removeEventListener('scroll', () => {});
    document.removeEventListener('resize', () => {});
  }

  static optimizeMemoryUsage(): void {
    // Clear large caches
    if ('caches' in window) {
      caches.keys().then(names => {
        names.forEach(name => {
          if (name.includes('temp') || name.includes('old')) {
            caches.delete(name);
          }
        });
      });
    }
  }
}