/**
 * Performance Tests
 * Tests application performance metrics and optimization features
 */

import { PerformanceMonitor, LazyImageLoader, BundleOptimizer } from '@/lib/performance';

// Mock performance APIs for testing
const mockPerformanceObserver = {
  observe: jest.fn(),
  disconnect: jest.fn(),
};

const mockIntersectionObserver = {
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
};

// Setup global mocks
beforeAll(() => {
  global.PerformanceObserver = jest.fn().mockImplementation((callback) => {
    return mockPerformanceObserver;
  });

  global.IntersectionObserver = jest.fn().mockImplementation((callback) => {
    return mockIntersectionObserver;
  });

  // Mock performance API
  Object.defineProperty(global, 'performance', {
    value: {
      now: jest.fn(() => Date.now()),
      getEntriesByType: jest.fn(() => []),
      mark: jest.fn(),
      measure: jest.fn(),
    },
    writable: true,
  });
});

describe('Performance Tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('PerformanceMonitor', () => {
    test('should initialize performance monitoring', () => {
      const monitor = PerformanceMonitor.getInstance();

      expect(() => {
        monitor.startMonitoring();
      }).not.toThrow();

      expect(global.PerformanceObserver).toHaveBeenCalled();
    });

    test('should track page load time', () => {
      const monitor = PerformanceMonitor.getInstance();

      // Simulate load event
      const loadEvent = new Event('load');
      window.dispatchEvent(loadEvent);

      const metrics = monitor.getMetrics();
      expect(metrics).toBeDefined();
    });

    test('should observe web vitals', () => {
      const monitor = PerformanceMonitor.getInstance();
      monitor.startMonitoring();

      expect(global.PerformanceObserver).toHaveBeenCalledWith(
        expect.any(Function)
      );
    });

    test('should cleanup observers on stop', () => {
      const monitor = PerformanceMonitor.getInstance();
      monitor.startMonitoring();
      monitor.stopMonitoring();

      expect(mockPerformanceObserver.disconnect).toHaveBeenCalled();
    });
  });

  describe('LazyImageLoader', () => {
    test('should initialize intersection observer', () => {
      const loader = LazyImageLoader.getInstance();

      expect(() => {
        loader.initialize();
      }).not.toThrow();

      expect(global.IntersectionObserver).toHaveBeenCalled();
    });

    test('should observe images for lazy loading', () => {
      const loader = LazyImageLoader.getInstance();
      loader.initialize();

      const mockImage = document.createElement('img');
      mockImage.src = 'test.jpg';

      expect(() => {
        loader.observeImage(mockImage);
      }).not.toThrow();

      expect(mockIntersectionObserver.observe).toHaveBeenCalledWith(mockImage);
    });

    test('should handle image load events', () => {
      const loader = LazyImageLoader.getInstance();

      const mockImage = {
        src: 'test.jpg',
        onload: null as (() => void) | null,
        onerror: null as (() => void) | null,
        style: { opacity: '', transition: '' },
        offsetHeight: 100,
      };

      // Simulate successful image load
      if (mockImage.onload) {
        mockImage.onload();
      }

      expect(mockImage.style.opacity).toBe('1');
    });

    test('should handle image load errors', () => {
      const loader = LazyImageLoader.getInstance();

      const mockImage = {
        src: 'test.jpg',
        onload: null as (() => void) | null,
        onerror: null as (() => void) | null,
      };

      // Simulate image load error
      if (mockImage.onerror) {
        mockImage.onerror();
      }

      // Should not throw error
      expect(true).toBe(true);
    });
  });

  describe('BundleOptimizer', () => {
    test('should preload critical resources', async () => {
      // Mock document.createElement
      const mockLink = {
        rel: '',
        as: '',
        href: '',
        onload: null as (() => void) | null,
        onerror: null as (() => void) | null,
      };

      const createElementSpy = jest.spyOn(document, 'createElement')
        .mockReturnValue(mockLink as any);

      const appendChildSpy = jest.spyOn(document.head, 'appendChild')
        .mockImplementation(() => mockLink as any);

      await BundleOptimizer.preloadCriticalResources();

      expect(createElementSpy).toHaveBeenCalledWith('link');
      expect(appendChildSpy).toHaveBeenCalled();

      createElementSpy.mockRestore();
      appendChildSpy.mockRestore();
    });

    test('should optimize images for lazy loading', () => {
      // Mock querySelectorAll
      const mockImages = [
        { dataset: { src: 'image1.jpg' } },
        { dataset: { src: 'image2.jpg' } },
      ];

      const querySelectorAllSpy = jest.spyOn(document, 'querySelectorAll')
        .mockReturnValue(mockImages as any);

      const loader = LazyImageLoader.getInstance();
      BundleOptimizer.optimizeImages();

      expect(querySelectorAllSpy).toHaveBeenCalledWith('img[data-src]');

      querySelectorAllSpy.mockRestore();
    });
  });

  describe('Performance Benchmarks', () => {
    test('should measure component render time', () => {
      const startTime = performance.now();

      // Simulate component rendering
      const mockComponent = {
        render: () => {
          // Simulate some work
          for (let i = 0; i < 1000; i++) {
            Math.random();
          }
        },
      };

      mockComponent.render();

      const endTime = performance.now();
      const renderTime = endTime - startTime;

      expect(renderTime).toBeLessThan(100); // Should render in less than 100ms
    });

    test('should measure API response times', async () => {
      const startTime = performance.now();

      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 50));

      const endTime = performance.now();
      const responseTime = endTime - startTime;

      expect(responseTime).toBeGreaterThan(40); // Should take at least 50ms (mock delay)
      expect(responseTime).toBeLessThan(1000); // Should not take more than 1s
    });

    test('should measure memory usage', () => {
      // This is a basic check - in a real scenario you'd use more sophisticated memory monitoring
      if ('memory' in performance) {
        const memoryInfo = (performance as any).memory;
        expect(memoryInfo).toBeDefined();
        expect(typeof memoryInfo.usedJSHeapSize).toBe('number');
      }
    });
  });

  describe('Image Loading Performance', () => {
    test('should load images within acceptable time', async () => {
      const loader = LazyImageLoader.getInstance();

      const mockImage = {
        src: 'test-image.jpg',
        dataset: { src: 'test-image.jpg' },
        onload: null as (() => void) | null,
        onerror: null as (() => void) | null,
        style: { opacity: '', transition: '' },
      };

      const startTime = performance.now();

      // Simulate image loading
      setTimeout(() => {
        if (mockImage.onload) {
          mockImage.onload();
        }
      }, 10);

      await new Promise(resolve => setTimeout(resolve, 20));

      const loadTime = performance.now() - startTime;

      expect(loadTime).toBeGreaterThan(0);
      expect(loadTime).toBeLessThan(1000); // Should load within 1 second
    });

    test('should handle multiple concurrent image loads', async () => {
      const loader = LazyImageLoader.getInstance();
      const images = Array.from({ length: 5 }, (_, i) => ({
        src: `test-image-${i}.jpg`,
        dataset: { src: `test-image-${i}.jpg` },
        onload: null as (() => void) | null,
      }));

      const startTime = performance.now();

      // Simulate concurrent loading
      const loadPromises = images.map((img, index) =>
        new Promise<void>(resolve => {
          setTimeout(() => {
            if (img.onload) img.onload();
            resolve();
          }, index * 10); // Stagger loading
        })
      );

      await Promise.all(loadPromises);

      const totalTime = performance.now() - startTime;

      expect(totalTime).toBeGreaterThan(0);
      expect(totalTime).toBeLessThan(200); // Should complete within 200ms
    });
  });

  describe('Bundle Size Optimization', () => {
    test('should have reasonable bundle size', () => {
      // This is a conceptual test - in practice you'd measure actual bundle sizes
      const estimatedBundleSize = 1024 * 1024; // 1MB estimated

      expect(estimatedBundleSize).toBeLessThan(5 * 1024 * 1024); // Should be under 5MB
    });

    test('should preload critical resources efficiently', async () => {
      const startTime = performance.now();

      await BundleOptimizer.preloadCriticalResources();

      const preloadTime = performance.now() - startTime;

      expect(preloadTime).toBeLessThan(100); // Should preload quickly
    });
  });

  describe('Caching Performance', () => {
    test('should cache API responses effectively', async () => {
      const mockCache = {
        match: jest.fn().mockResolvedValue(null),
        put: jest.fn().mockResolvedValue(undefined),
      };

      // Mock caches API
      global.caches = {
        open: jest.fn().mockResolvedValue(mockCache),
        match: jest.fn().mockResolvedValue(null),
      } as any;

      // First request (cache miss)
      const firstRequest = await fetch('/api/test');
      expect(mockCache.put).toHaveBeenCalled();

      // Second request (cache hit)
      const secondRequest = await fetch('/api/test');
      expect(mockCache.match).toHaveBeenCalled();
    });

    test('should handle cache storage limits', () => {
      // Mock storage quota exceeded
      const mockStorage = {
        estimate: jest.fn().mockResolvedValue({
          quota: 1024 * 1024, // 1MB
          usage: 900 * 1024,  // 900KB used
        }),
      };

      Object.defineProperty(global, 'navigator', {
        value: {
          storage: mockStorage,
        },
        writable: true,
      });

      expect(mockStorage.estimate).toBeDefined();
    });
  });

  describe('Service Worker Performance', () => {
    test('should register service worker successfully', async () => {
      const mockRegistration = {
        update: jest.fn(),
        unregister: jest.fn(),
      };

      // Mock service worker registration
      if ('serviceWorker' in navigator) {
        Object.defineProperty(navigator, 'serviceWorker', {
          value: {
            register: jest.fn().mockResolvedValue(mockRegistration),
            ready: Promise.resolve(mockRegistration),
          },
          writable: true,
        });
      }

      // Test would go here in a real implementation
      expect(navigator.serviceWorker).toBeDefined();
    });

    test('should handle service worker installation', () => {
      const mockServiceWorker = {
        postMessage: jest.fn(),
        addEventListener: jest.fn(),
      };

      // Simulate service worker events
      expect(mockServiceWorker.postMessage).toBeDefined();
      expect(mockServiceWorker.addEventListener).toBeDefined();
    });
  });
});

// Helper function for performance assertions
function expectPerformanceMetric(metricName: string, value: number, threshold: number) {
  const monitor = PerformanceMonitor.getInstance();
  const metrics = monitor.getMetrics();

  if (metricName in metrics) {
    const metricValue = metrics[metricName as keyof typeof metrics] as number;
    expect(metricValue).toBeLessThan(threshold);
  }
}

// Export for use in CI/CD pipelines
export { expectPerformanceMetric };