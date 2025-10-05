// Service Worker for caching static assets and improving performance

const CACHE_NAME = 'crashed-car-sales-v1';
const STATIC_CACHE_NAME = 'crashed-car-sales-static-v1';
const DYNAMIC_CACHE_NAME = 'crashed-car-sales-dynamic-v1';

// Assets to cache immediately
const STATIC_ASSETS = [
  '/',
  '/manifest.json',
  '/assets/css/bootstrap.min.css',
  '/assets/css/style.css',
  '/assets/js/jquery-3.6.0.min.js',
  '/assets/js/bootstrap.bundle.min.js',
  '/assets/js/main.js',
  '/assets/img/logo.jpg',
  '/assets/img/car/01.jpg',
  '/assets/img/car/02.jpg',
  '/assets/img/car/03.jpg',
];

// API endpoints to cache
const API_CACHE_PATTERNS = [
  /\/api\/public\/cars/,
  /\/api\/public\/dealers/,
  /\/locales\//,
];

// Install event - cache static assets
self.addEventListener('install', (event) => {
  console.log('Service Worker: Installing...');

  event.waitUntil(
    caches.open(STATIC_CACHE_NAME)
      .then((cache) => {
        console.log('Service Worker: Caching static assets');
        return cache.addAll(STATIC_ASSETS);
      })
      .then(() => {
        console.log('Service Worker: Static assets cached successfully');
        return self.skipWaiting();
      })
      .catch((error) => {
        console.error('Service Worker: Failed to cache static assets', error);
      })
  );
});

// Activate event - clean up old caches
self.addEventListener('activate', (event) => {
  console.log('Service Worker: Activating...');

  event.waitUntil(
    caches.keys()
      .then((cacheNames) => {
        return Promise.all(
          cacheNames.map((cacheName) => {
            if (cacheName !== STATIC_CACHE_NAME && cacheName !== DYNAMIC_CACHE_NAME) {
              console.log('Service Worker: Deleting old cache', cacheName);
              return caches.delete(cacheName);
            }
          })
        );
      })
      .then(() => {
        console.log('Service Worker: Activated successfully');
        return self.clients.claim();
      })
  );
});

// Fetch event - serve from cache with network fallback
self.addEventListener('fetch', (event) => {
  const { request } = event;
  const url = new URL(request.url);

  // Skip non-GET requests
  if (request.method !== 'GET') {
    return;
  }

  // Skip chrome-extension and other non-http(s) requests
  if (!url.protocol.startsWith('http')) {
    return;
  }

  // Handle API requests
  if (API_CACHE_PATTERNS.some(pattern => pattern.test(url.pathname))) {
    event.respondWith(handleApiRequest(request));
    return;
  }

  // Handle static assets and pages
  event.respondWith(handleStaticRequest(request));
});

async function handleApiRequest(request) {
  try {
    // Try network first for API requests
    const networkResponse = await fetch(request);

    // Cache successful responses
    if (networkResponse.ok) {
      const cache = await caches.open(DYNAMIC_CACHE_NAME);
      cache.put(request, networkResponse.clone());
    }

    return networkResponse;
  } catch (error) {
    console.log('Service Worker: Network failed for API request, trying cache', request.url);

    // Fallback to cache
    const cachedResponse = await caches.match(request);

    if (cachedResponse) {
      return cachedResponse;
    }

    // Return error response if no cache available
    return new Response(JSON.stringify({
      error: 'Network unavailable and no cached data available',
      offline: true
    }), {
      status: 503,
      headers: { 'Content-Type': 'application/json' }
    });
  }
}

async function handleStaticRequest(request) {
  try {
    // Try cache first for static assets
    const cachedResponse = await caches.match(request);

    if (cachedResponse) {
      return cachedResponse;
    }

    // Fallback to network
    const networkResponse = await fetch(request);

    // Cache successful responses for future use
    if (networkResponse.ok) {
      const cache = await caches.open(STATIC_CACHE_NAME);
      cache.put(request, networkResponse.clone());
    }

    return networkResponse;
  } catch (error) {
    console.log('Service Worker: Both cache and network failed for', request.url);

    // Return offline page for navigation requests
    if (request.mode === 'navigate') {
      return caches.match('/');
    }

    // Return a generic offline response for other requests
    return new Response('Offline', {
      status: 503,
      statusText: 'Service Unavailable'
    });
  }
}

// Background sync for offline actions
self.addEventListener('sync', (event) => {
  console.log('Service Worker: Background sync triggered', event.tag);

  if (event.tag === 'background-sync') {
    event.waitUntil(syncOfflineActions());
  }
});

async function syncOfflineActions() {
  // TODO: Implement offline action sync when backend supports it
  console.log('Service Worker: Syncing offline actions...');
}

// Push notifications (for future features)
self.addEventListener('push', (event) => {
  console.log('Service Worker: Push notification received');

  if (event.data) {
    const data = event.data.json();

    const options = {
      body: data.body,
      icon: '/assets/img/logo.jpg',
      badge: '/assets/img/logo.jpg',
      vibrate: [200, 100, 200],
      data: data.data || {},
      actions: [
        {
          action: 'view',
          title: 'View',
          icon: '/assets/img/logo.jpg'
        },
        {
          action: 'close',
          title: 'Close',
          icon: '/assets/img/logo.jpg'
        }
      ]
    };

    event.waitUntil(
      self.registration.showNotification(data.title, options)
    );
  }
});

// Notification click handling
self.addEventListener('notificationclick', (event) => {
  console.log('Service Worker: Notification clicked');

  event.notification.close();

  if (event.action === 'view') {
    // Open the app
    event.waitUntil(
      clients.openWindow('/')
    );
  }
});

// Message handling for communication with main thread
self.addEventListener('message', (event) => {
  console.log('Service Worker: Message received', event.data);

  if (event.data && event.data.type === 'SKIP_WAITING') {
    self.skipWaiting();
  }

  if (event.data && event.data.type === 'GET_VERSION') {
    event.ports[0].postMessage({ version: CACHE_NAME });
  }
});