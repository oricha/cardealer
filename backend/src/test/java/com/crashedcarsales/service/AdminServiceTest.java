package com.crashedcarsales.service;

import com.crashedcarsales.dto.AdminStatsResponse;
import com.crashedcarsales.dto.AdminUserResponse;
import com.crashedcarsales.dto.UserStatusUpdateRequest;
import com.crashedcarsales.entity.User;
import com.crashedcarsales.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DealerRepository dealerRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private FavoritesRepository favoritesRepository;

    @InjectMocks
    private AdminService adminService;

    private User testUser;
    private User testAdmin;
    private User testDealer;
    private UUID testUserId;
    private Pageable pageable;
    private List<User> userList;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();

        // Create test users
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashedpassword");
        testUser.setRole(User.Role.BUYER);
        testUser.setIsActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testAdmin = new User();
        testAdmin.setId(UUID.randomUUID());
        testAdmin.setEmail("admin@example.com");
        testAdmin.setPasswordHash("hashedpassword");
        testAdmin.setRole(User.Role.ADMIN);
        testAdmin.setIsActive(true);
        testAdmin.setCreatedAt(LocalDateTime.now());
        testAdmin.setUpdatedAt(LocalDateTime.now());

        testDealer = new User();
        testDealer.setId(UUID.randomUUID());
        testDealer.setEmail("dealer@example.com");
        testDealer.setPasswordHash("hashedpassword");
        testDealer.setRole(User.Role.DEALER);
        testDealer.setIsActive(true);
        testDealer.setCreatedAt(LocalDateTime.now());
        testDealer.setUpdatedAt(LocalDateTime.now());

        userList = List.of(testUser, testAdmin, testDealer);
        pageable = PageRequest.of(0, 20);
    }

    @Test
    void getAllUsers_WithValidPageable_ShouldReturnUsersPage() {
        // Given
        Page<User> userPage = new PageImpl<>(userList, pageable, userList.size());
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // When
        Page<AdminUserResponse> result = adminService.getAllUsers(pageable);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals(3, result.getTotalElements());
        assertEquals(testUser.getEmail(), result.getContent().get(0).getEmail());

        verify(userRepository).findAll(pageable);
    }

    @Test
    void getAllUsers_WithEmptyResult_ShouldReturnEmptyPage() {
        // Given
        Page<User> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(userRepository.findAll(pageable)).thenReturn(emptyPage);

        // When
        Page<AdminUserResponse> result = adminService.getAllUsers(pageable);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        assertEquals(0, result.getTotalElements());

        verify(userRepository).findAll(pageable);
    }

    @Test
    void getUserById_WithExistingUser_ShouldReturnUserResponse() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When
        AdminUserResponse result = adminService.getUserById(testUserId);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getRole(), result.getRole());
        assertEquals(testUser.getIsActive(), result.getIsActive());

        verify(userRepository).findById(testUserId);
    }

    @Test
    void getUserById_WithNonExistentUser_ShouldThrowException() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminService.getUserById(nonExistentId);
        });

        assertEquals("User not found with ID: " + nonExistentId, exception.getMessage());
        verify(userRepository).findById(nonExistentId);
    }

    @Test
    void updateUserStatus_WithValidRequest_ShouldUpdateAndReturnUser() {
        // Given
        UserStatusUpdateRequest request = new UserStatusUpdateRequest(false);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        AdminUserResponse result = adminService.updateUserStatus(testUserId, request);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        verify(userRepository).findById(testUserId);
        verify(userRepository).save(testUser);
        assertFalse(testUser.getIsActive());
    }

    @Test
    void updateUserStatus_WithNonExistentUser_ShouldThrowException() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        UserStatusUpdateRequest request = new UserStatusUpdateRequest(false);
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminService.updateUserStatus(nonExistentId, request);
        });

        assertEquals("User not found with ID: " + nonExistentId, exception.getMessage());
        verify(userRepository).findById(nonExistentId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_WithExistingUser_ShouldDeactivateUser() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        adminService.deleteUser(testUserId);

        // Then
        verify(userRepository).findById(testUserId);
        verify(userRepository).save(testUser);
        assertFalse(testUser.getIsActive());
    }

    @Test
    void deleteUser_WithNonExistentUser_ShouldThrowException() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            adminService.deleteUser(nonExistentId);
        });

        assertEquals("User not found with ID: " + nonExistentId, exception.getMessage());
        verify(userRepository).findById(nonExistentId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getSystemStats_ShouldReturnComprehensiveStatistics() {
        // Given
        when(userRepository.count()).thenReturn(10L);
        when(userRepository.countByIsActiveTrue()).thenReturn(8L);
        when(userRepository.countByRole(User.Role.DEALER)).thenReturn(3L);
        when(userRepository.countByRoleAndIsActiveTrue(User.Role.DEALER)).thenReturn(2L);
        when(carRepository.count()).thenReturn(15L);
        when(carRepository.countByIsActiveTrue()).thenReturn(12L);
        when(favoritesRepository.count()).thenReturn(25L);

        // When
        AdminStatsResponse result = adminService.getSystemStats();

        // Then
        assertNotNull(result);
        assertEquals(10L, result.getTotalUsers());
        assertEquals(8L, result.getActiveUsers());
        assertEquals(3L, result.getTotalDealers());
        assertEquals(2L, result.getActiveDealers());
        assertEquals(15L, result.getTotalCars());
        assertEquals(12L, result.getActiveCars());
        assertEquals(25L, result.getTotalFavorites());
        assertEquals(5.0, result.getAverageCarsPerDealer()); // 15 total cars / 3 total dealers

        verify(userRepository).count();
        verify(userRepository).countByIsActiveTrue();
        verify(userRepository).countByRole(User.Role.DEALER);
        verify(userRepository).countByRoleAndIsActiveTrue(User.Role.DEALER);
        verify(carRepository).count();
        verify(carRepository).countByIsActiveTrue();
        verify(favoritesRepository).count();
    }

    @Test
    void getSystemStats_WithNoDealers_ShouldHandleZeroDivision() {
        // Given
        when(userRepository.count()).thenReturn(5L);
        when(userRepository.countByIsActiveTrue()).thenReturn(4L);
        when(userRepository.countByRole(User.Role.DEALER)).thenReturn(0L);
        when(userRepository.countByRoleAndIsActiveTrue(User.Role.DEALER)).thenReturn(0L);
        when(carRepository.count()).thenReturn(10L);
        when(carRepository.countByIsActiveTrue()).thenReturn(8L);
        when(favoritesRepository.count()).thenReturn(15L);

        // When
        AdminStatsResponse result = adminService.getSystemStats();

        // Then
        assertNotNull(result);
        assertEquals(0.0, result.getAverageCarsPerDealer()); // Should be 0.0 when no dealers

        verify(userRepository).count();
        verify(userRepository).countByIsActiveTrue();
        verify(userRepository).countByRole(User.Role.DEALER);
        verify(userRepository).countByRoleAndIsActiveTrue(User.Role.DEALER);
        verify(carRepository).count();
        verify(carRepository).countByIsActiveTrue();
        verify(favoritesRepository).count();
    }

    @Test
    void searchUsersByEmail_WithMatchingUsers_ShouldReturnUserList() {
        // Given
        String emailPattern = "example";
        when(userRepository.findByEmailContainingIgnoreCase(emailPattern)).thenReturn(userList);

        // When
        List<AdminUserResponse> result = adminService.searchUsersByEmail(emailPattern);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(testUser.getEmail(), result.get(0).getEmail());

        verify(userRepository).findByEmailContainingIgnoreCase(emailPattern);
    }

    @Test
    void searchUsersByEmail_WithNoMatches_ShouldReturnEmptyList() {
        // Given
        String emailPattern = "nonexistent";
        when(userRepository.findByEmailContainingIgnoreCase(emailPattern)).thenReturn(List.of());

        // When
        List<AdminUserResponse> result = adminService.searchUsersByEmail(emailPattern);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());

        verify(userRepository).findByEmailContainingIgnoreCase(emailPattern);
    }

    @Test
    void getUsersByRole_WithValidRole_ShouldReturnUsersWithRole() {
        // Given
        User.Role role = User.Role.DEALER;
        when(userRepository.findByRole(role)).thenReturn(List.of(testDealer));

        // When
        List<AdminUserResponse> result = adminService.getUsersByRole(role);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDealer.getRole(), result.get(0).getRole());
        assertEquals(testDealer.getEmail(), result.get(0).getEmail());

        verify(userRepository).findByRole(role);
    }

    @Test
    void getUsersByRole_WithNoUsers_ShouldReturnEmptyList() {
        // Given
        User.Role role = User.Role.ADMIN;
        when(userRepository.findByRole(role)).thenReturn(List.of());

        // When
        List<AdminUserResponse> result = adminService.getUsersByRole(role);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());

        verify(userRepository).findByRole(role);
    }
}