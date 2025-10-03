package com.crashedcarsales.service;

import com.crashedcarsales.dto.DealerProfile;
import com.crashedcarsales.dto.DealerRegistrationRequest;
import com.crashedcarsales.dto.DealerStats;
import com.crashedcarsales.entity.Dealer;
import com.crashedcarsales.entity.User;
import com.crashedcarsales.repository.DealerRepository;
import com.crashedcarsales.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealerServiceTest {

    @Mock
    private DealerRepository dealerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DealerService dealerService;

    private User testUser;
    private Dealer testDealer;
    private DealerRegistrationRequest testRegistrationRequest;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("dealer@example.com");
        testUser.setPasswordHash("hashedpassword");
        testUser.setRole(User.Role.DEALER);
        testUser.setIsActive(true);

        // Create test dealer
        testDealer = new Dealer();
        testDealer.setId(UUID.randomUUID());
        testDealer.setUser(testUser);
        testDealer.setName("Test Dealer");
        testDealer.setAddress("123 Test Street");
        testDealer.setPhone("+1234567890");
        testDealer.setWebsite("https://testdealer.com");
        testDealer.setCreatedAt(LocalDateTime.now());
        testDealer.setUpdatedAt(LocalDateTime.now());

        // Create test registration request
        testRegistrationRequest = new DealerRegistrationRequest();
        testRegistrationRequest.setEmail("newdealer@example.com");
        testRegistrationRequest.setPassword("password123");
        testRegistrationRequest.setName("New Test Dealer");
        testRegistrationRequest.setAddress("456 New Street");
        testRegistrationRequest.setPhone("+0987654321");
        testRegistrationRequest.setWebsite("https://newdealer.com");
    }

    @Test
    void registerDealer_WithValidData_ShouldCreateDealerSuccessfully() {
        // Given
        when(userRepository.existsByEmail(testRegistrationRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(testRegistrationRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(dealerRepository.save(any(Dealer.class))).thenReturn(testDealer);

        // When
        DealerProfile result = dealerService.registerDealer(testRegistrationRequest);

        // Then
        assertNotNull(result);
        assertEquals(testDealer.getId(), result.getId());
        assertEquals(testDealer.getName(), result.getName());
        assertEquals(testDealer.getUser().getEmail(), result.getEmail());

        verify(userRepository).existsByEmail(testRegistrationRequest.getEmail());
        verify(passwordEncoder).encode(testRegistrationRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(dealerRepository).save(any(Dealer.class));
    }

    @Test
    void registerDealer_WithExistingEmail_ShouldThrowException() {
        // Given
        when(userRepository.existsByEmail(testRegistrationRequest.getEmail())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> dealerService.registerDealer(testRegistrationRequest));

        assertEquals("User with this email already exists", exception.getMessage());
        verify(userRepository).existsByEmail(testRegistrationRequest.getEmail());
        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any(User.class));
        verify(dealerRepository, never()).save(any(Dealer.class));
    }

    @Test
    void getDealerProfile_WithValidUserId_ShouldReturnProfile() {
        // Given
        UUID userId = testUser.getId();
        when(dealerRepository.findByUserId(userId)).thenReturn(Optional.of(testDealer));

        // When
        Optional<DealerProfile> result = dealerService.getDealerProfile(userId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testDealer.getId(), result.get().getId());
        assertEquals(testDealer.getName(), result.get().getName());
        verify(dealerRepository).findByUserId(userId);
    }

    @Test
    void getDealerProfile_WithInvalidUserId_ShouldReturnEmpty() {
        // Given
        UUID userId = UUID.randomUUID();
        when(dealerRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When
        Optional<DealerProfile> result = dealerService.getDealerProfile(userId);

        // Then
        assertFalse(result.isPresent());
        verify(dealerRepository).findByUserId(userId);
    }

    @Test
    void updateDealerProfile_WithValidData_ShouldUpdateSuccessfully() {
        // Given
        UUID userId = testUser.getId();
        String newName = "Updated Dealer Name";
        when(dealerRepository.findByUserId(userId)).thenReturn(Optional.of(testDealer));
        when(dealerRepository.save(any(Dealer.class))).thenReturn(testDealer);

        // When
        DealerProfile result = dealerService.updateDealerProfile(userId, newName, null, null, null);

        // Then
        assertNotNull(result);
        verify(dealerRepository).findByUserId(userId);
        verify(dealerRepository).save(testDealer);
    }

    @Test
    void updateDealerProfile_WithInvalidUserId_ShouldThrowException() {
        // Given
        UUID userId = UUID.randomUUID();
        when(dealerRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> dealerService.updateDealerProfile(userId, "New Name", null, null, null));

        assertEquals("Dealer not found for user ID: " + userId, exception.getMessage());
        verify(dealerRepository).findByUserId(userId);
        verify(dealerRepository, never()).save(any(Dealer.class));
    }

    @Test
    void getDealerStatistics_WithValidDealerId_ShouldReturnStats() {
        // Given
        UUID dealerId = testDealer.getId();
        Object[] statsData = {10L, 5L, new BigDecimal("50000.00")}; // carsListed, carsSold, totalValue
        when(dealerRepository.getDealerStatistics(dealerId)).thenReturn(statsData);

        // When
        DealerStats result = dealerService.getDealerStatistics(dealerId);

        // Then
        assertNotNull(result);
        assertEquals(dealerId, result.getDealerId());
        assertEquals(10L, result.getCarsListed());
        assertEquals(5L, result.getCarsSold());
        assertEquals(new BigDecimal("50000.00"), result.getTotalSalesValue());
        assertEquals(new BigDecimal("10000.00"), result.getAverageSalePrice()); // 50000/5

        verify(dealerRepository).getDealerStatistics(dealerId);
    }

    @Test
    void findByUserEmail_WithValidEmail_ShouldReturnDealer() {
        // Given
        String email = testUser.getEmail();
        when(dealerRepository.findByUserEmail(email)).thenReturn(Optional.of(testDealer));

        // When
        Optional<Dealer> result = dealerService.findByUserEmail(email);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testDealer.getId(), result.get().getId());
        verify(dealerRepository).findByUserEmail(email);
    }

    @Test
    void searchDealersByName_WithValidPattern_ShouldReturnDealers() {
        // Given
        String namePattern = "Test";
        List<Dealer> dealers = List.of(testDealer);
        when(dealerRepository.findDealersByNamePattern(namePattern)).thenReturn(dealers);

        // When
        List<DealerProfile> result = dealerService.searchDealersByName(namePattern);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDealer.getId(), result.get(0).getId());
        verify(dealerRepository).findDealersByNamePattern(namePattern);
    }

    @Test
    void getRecentlyCreatedDealers_WithValidLimit_ShouldReturnDealers() {
        // Given
        int limit = 5;
        List<Dealer> dealers = List.of(testDealer);
        when(dealerRepository.findRecentlyCreatedDealers(limit)).thenReturn(dealers);

        // When
        List<DealerProfile> result = dealerService.getRecentlyCreatedDealers(limit);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDealer.getId(), result.get(0).getId());
        verify(dealerRepository).findRecentlyCreatedDealers(limit);
    }

    @Test
    void dealerExistsForUser_WithExistingDealer_ShouldReturnTrue() {
        // Given
        UUID userId = testUser.getId();
        when(dealerRepository.existsByUserId(userId)).thenReturn(true);

        // When
        boolean result = dealerService.dealerExistsForUser(userId);

        // Then
        assertTrue(result);
        verify(dealerRepository).existsByUserId(userId);
    }

    @Test
    void getDealerCount_ShouldReturnCorrectCount() {
        // Given
        long expectedCount = 42L;
        when(dealerRepository.count()).thenReturn(expectedCount);

        // When
        long result = dealerService.getDealerCount();

        // Then
        assertEquals(expectedCount, result);
        verify(dealerRepository).count();
    }
}