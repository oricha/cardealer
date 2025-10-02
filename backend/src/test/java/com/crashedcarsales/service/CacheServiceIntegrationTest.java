package com.crashedcarsales.service;

import com.crashedcarsales.dto.CarResponse;
import com.crashedcarsales.dto.CarSearchRequest;
import com.crashedcarsales.entity.Car;
import com.crashedcarsales.entity.Dealer;
import com.crashedcarsales.entity.User;
import com.crashedcarsales.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CacheServiceIntegrationTest {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CacheManager cacheManager;

    private Car testCar;
    private Dealer testDealer;

    @BeforeEach
    void setUp() {
        // Create test dealer
        testDealer = new Dealer();
        testDealer.setName("Test Dealer");
        testDealer.setUser(new User());

        // Create test car
        testCar = new Car();
        testCar.setDealer(testDealer);
        testCar.setMake("Toyota");
        testCar.setModel("Camry");
        testCar.setYear(2020);
        testCar.setFuelType(Car.FuelType.GAS);
        testCar.setTransmission(Car.Transmission.AUTOMATIC);
        testCar.setVehicleType(Car.VehicleType.PASSENGER);
        testCar.setCondition(Car.Condition.USED);
        testCar.setPrice(new BigDecimal("25000.00"));
        testCar.setMileage(50000);
        testCar.setDescription("A reliable used car");
        testCar.setIsFeatured(true);
        testCar.setIsActive(true);
        testCar.setCreatedAt(LocalDateTime.now());
        testCar.setUpdatedAt(LocalDateTime.now());

        // Save test car
        carRepository.save(testCar);
    }

    @Test
    void cacheCarDetails_ShouldStoreAndRetrieveCorrectly() {
        // Given
        String carId = testCar.getId().toString();
        CarResponse carResponse = CarResponse.fromEntity(testCar);

        // When
        cacheService.cacheCarDetails(carId, carResponse);
        Object cachedData = cacheService.getCachedCarDetails(carId);

        // Then
        assertNotNull(cachedData);
        assertTrue(cachedData instanceof CarResponse);
        CarResponse cachedCar = (CarResponse) cachedData;
        assertEquals(testCar.getId(), cachedCar.getId());
        assertEquals(testCar.getMake(), cachedCar.getMake());
    }

    @Test
    void cacheFeaturedCars_ShouldStoreAndRetrieveCorrectly() {
        // Given
        List<CarResponse> featuredCars = List.of(CarResponse.fromEntity(testCar));

        // When
        cacheService.cacheFeaturedCars(featuredCars);
        Object cachedData = cacheService.getCachedFeaturedCars();

        // Then
        assertNotNull(cachedData);
        assertTrue(cachedData instanceof List);
        @SuppressWarnings("unchecked")
        List<CarResponse> cachedCars = (List<CarResponse>) cachedData;
        assertEquals(1, cachedCars.size());
        assertEquals(testCar.getId(), cachedCars.get(0).getId());
    }

    @Test
    void cacheSearchResults_ShouldStoreAndRetrieveCorrectly() {
        // Given
        String searchKey = "Toyota-Camry-20000-30000";
        CarSearchRequest searchRequest = new CarSearchRequest();
        searchRequest.setMake("Toyota");
        searchRequest.setModel("Camry");
        searchRequest.setMinPrice(new BigDecimal("20000"));
        searchRequest.setMaxPrice(new BigDecimal("30000"));

        List<CarResponse> searchResults = List.of(CarResponse.fromEntity(testCar));

        // When
        cacheService.cacheCarSearch(searchKey, searchResults);
        Object cachedData = cacheService.getCachedCarSearch(searchKey);

        // Then
        assertNotNull(cachedData);
        assertTrue(cachedData instanceof List);
        @SuppressWarnings("unchecked")
        List<CarResponse> cachedResults = (List<CarResponse>) cachedData;
        assertEquals(1, cachedResults.size());
    }

    @Test
    void invalidateCarCaches_ShouldRemoveCarRelatedEntries() {
        // Given
        String carId = testCar.getId().toString();
        cacheService.cacheCarDetails(carId, CarResponse.fromEntity(testCar));
        cacheService.cacheFeaturedCars(List.of(CarResponse.fromEntity(testCar)));

        // Verify data is cached
        assertNotNull(cacheService.getCachedCarDetails(carId));
        assertNotNull(cacheService.getCachedFeaturedCars());

        // When
        cacheService.invalidateCarCaches(carId);

        // Then
        assertNull(cacheService.getCachedCarDetails(carId));
        // Featured cars cache should be cleared
        assertNull(cacheService.getCachedFeaturedCars());
    }

    @Test
    void sessionManagement_ShouldStoreAndRetrieveSessionData() {
        // Given
        String sessionId = "test-session-123";
        Map<String, Object> sessionData = Map.of(
            "userId", "user123",
            "loginTime", LocalDateTime.now(),
            "preferences", Map.of("theme", "dark")
        );

        // When
        cacheService.storeSession(sessionId, sessionData);
        Map<String, Object> retrievedSession = cacheService.getSession(sessionId);

        // Then
        assertNotNull(retrievedSession);
        assertEquals("user123", retrievedSession.get("userId"));
        assertNotNull(retrievedSession.get("loginTime"));
        @SuppressWarnings("unchecked")
        Map<String, String> preferences = (Map<String, String>) retrievedSession.get("preferences");
        assertEquals("dark", preferences.get("theme"));
    }

    @Test
    void updateSession_ShouldModifyExistingSession() {
        // Given
        String sessionId = "test-session-456";
        Map<String, Object> initialData = Map.of("userId", "user456");
        cacheService.storeSession(sessionId, initialData);

        // When
        cacheService.updateSession(sessionId, "lastActivity", LocalDateTime.now());

        // Then
        Map<String, Object> updatedSession = cacheService.getSession(sessionId);
        assertNotNull(updatedSession);
        assertEquals("user456", updatedSession.get("userId"));
        assertNotNull(updatedSession.get("lastActivity"));
    }

    @Test
    void deleteSession_ShouldRemoveSessionData() {
        // Given
        String sessionId = "test-session-789";
        Map<String, Object> sessionData = Map.of("userId", "user789");
        cacheService.storeSession(sessionId, sessionData);

        // Verify session exists
        assertNotNull(cacheService.getSession(sessionId));

        // When
        cacheService.deleteSession(sessionId);

        // Then
        assertNull(cacheService.getSession(sessionId));
    }

    @Test
    void getCacheStatistics_ShouldReturnCacheInformation() {
        // Given
        cacheService.cacheCarDetails(testCar.getId().toString(), CarResponse.fromEntity(testCar));

        // When
        Map<String, Object> stats = cacheService.getCacheStatistics();

        // Then
        assertNotNull(stats);
        assertTrue(stats.containsKey("cacheNames"));
        assertTrue(stats.containsKey("totalCaches"));
        assertTrue(stats.containsKey("redisInfo"));

        @SuppressWarnings("unchecked")
        List<String> cacheNames = (List<String>) stats.get("cacheNames");
        assertTrue(cacheNames.contains("car-details"));
    }

    @Test
    void getCacheKeys_ShouldReturnKeysForSpecificCache() {
        // Given
        String carId = testCar.getId().toString();
        cacheService.cacheCarDetails(carId, CarResponse.fromEntity(testCar));

        // When
        Set<String> keys = cacheService.getCacheKeys("car-details");

        // Then
        assertNotNull(keys);
        assertTrue(keys.contains(carId));
    }

    @Test
    void evictAll_ShouldClearAllEntriesInCache() {
        // Given
        String carId = testCar.getId().toString();
        cacheService.cacheCarDetails(carId, CarResponse.fromEntity(testCar));
        cacheService.cacheFeaturedCars(List.of(CarResponse.fromEntity(testCar)));

        // Verify data is cached
        assertNotNull(cacheService.getCachedCarDetails(carId));
        assertNotNull(cacheService.getCachedFeaturedCars());

        // When
        cacheService.evictAll("car-details");

        // Then
        assertNull(cacheService.getCachedCarDetails(carId));
        // Other caches should remain intact
        assertNotNull(cacheService.getCachedFeaturedCars());
    }

    @Test
    void cacheTtl_ShouldExpireAfterTimeout() throws InterruptedException {
        // Given
        String carId = testCar.getId().toString();
        CarResponse carResponse = CarResponse.fromEntity(testCar);

        // Cache with short TTL (1 second for testing)
        cacheService.put("car-details", carId, carResponse, 1, java.util.concurrent.TimeUnit.SECONDS);

        // Verify data is cached
        assertNotNull(cacheService.getCachedCarDetails(carId));

        // Wait for expiration
        Thread.sleep(1100);

        // Then
        assertNull(cacheService.getCachedCarDetails(carId));
    }

    @Test
    void cacheExists_ShouldReturnCorrectExistenceStatus() {
        // Given
        String carId = testCar.getId().toString();
        CarResponse carResponse = CarResponse.fromEntity(testCar);

        // When
        boolean existsBefore = cacheService.exists("car-details", carId);
        cacheService.cacheCarDetails(carId, carResponse);
        boolean existsAfter = cacheService.exists("car-details", carId);

        // Then
        assertFalse(existsBefore);
        assertTrue(existsAfter);
    }
}