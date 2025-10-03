package com.crashedcarsales.service;

import com.crashedcarsales.dto.AuthResponse;
import com.crashedcarsales.dto.LoginRequest;
import com.crashedcarsales.dto.RegisterRequest;
import com.crashedcarsales.entity.User;
import com.crashedcarsales.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashedpassword");
        testUser.setRole(User.Role.DEALER);
        testUser.setIsActive(true);

        // Create test requests
        registerRequest = new RegisterRequest("test@example.com", "password123", User.Role.DEALER);
        loginRequest = new LoginRequest("test@example.com", "password123");
    }

    @Test
    void registerUser_WithValidData_ShouldCreateUserAndReturnAuthResponse() {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("hashedpassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateAccessToken(testUser)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(testUser)).thenReturn("refresh-token");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        // When
        AuthResponse response = userService.registerUser(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(testUser.getRole(), response.getRole());
        assertEquals(testUser.getEmail(), response.getEmail());

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(registerRequest.getPassword());
    }

    @Test
    void registerUser_WithExistingEmail_ShouldThrowException() {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(registerRequest);
        });

        assertEquals("User with this email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticateUser_WithValidCredentials_ShouldReturnAuthResponse() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(jwtService.generateAccessToken(testUser)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(testUser)).thenReturn("refresh-token");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        // When
        AuthResponse response = userService.authenticateUser(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(testUser.getRole(), response.getRole());
        assertEquals(testUser.getEmail(), response.getEmail());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticateUser_WithInvalidCredentials_ShouldThrowException() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.authenticateUser(loginRequest);
        });

        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    void refreshToken_WithValidRefreshToken_ShouldReturnNewAuthResponse() {
        // Given
        String refreshToken = "valid-refresh-token";
        when(jwtService.extractUsername(refreshToken)).thenReturn(testUser.getEmail());
        when(userRepository.findByEmailAndIsActive(testUser.getEmail(), true)).thenReturn(Optional.of(testUser));
        when(jwtService.refreshAccessToken(refreshToken)).thenReturn("new-access-token");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        // When
        AuthResponse response = userService.refreshToken(refreshToken);

        // Then
        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());
        assertEquals(testUser.getRole(), response.getRole());
        assertEquals(testUser.getEmail(), response.getEmail());
    }

    @Test
    void refreshToken_WithInvalidToken_ShouldThrowException() {
        // Given
        String invalidToken = "invalid-token";
        when(jwtService.extractUsername(invalidToken)).thenThrow(new RuntimeException("Invalid token"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.refreshToken(invalidToken);
        });

        assertEquals("Invalid refresh token", exception.getMessage());
    }

    @Test
    void refreshToken_WithNonExistentUser_ShouldThrowException() {
        // Given
        String refreshToken = "valid-refresh-token";
        when(jwtService.extractUsername(refreshToken)).thenReturn("nonexistent@example.com");
        when(userRepository.findByEmailAndIsActive("nonexistent@example.com", true)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.refreshToken(refreshToken);
        });

        assertEquals("Invalid refresh token", exception.getMessage());
    }

    @Test
    void findByEmail_WithExistingUser_ShouldReturnUser() {
        // Given
        when(userRepository.findByEmailAndIsActive(testUser.getEmail(), true)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findByEmail(testUser.getEmail());

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getEmail(), result.get().getEmail());
    }

    @Test
    void findByEmail_WithNonExistentUser_ShouldReturnEmpty() {
        // Given
        when(userRepository.findByEmailAndIsActive("nonexistent@example.com", true)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findByEmail("nonexistent@example.com");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findById_WithExistingUser_ShouldReturnUser() {
        // Given
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findById(testUser.getId());

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
    }

    @Test
    void findById_WithInactiveUser_ShouldReturnEmpty() {
        // Given
        testUser.setIsActive(false);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.findById(testUser.getId());

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void updatePassword_ShouldEncodeAndSaveNewPassword() {
        // Given
        String newPassword = "newpassword123";
        String encodedPassword = "newhashedpassword";
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        // When
        userService.updatePassword(testUser.getId(), newPassword);

        // Then
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(testUser);
        assertEquals(encodedPassword, testUser.getPasswordHash());
    }

    @Test
    void updatePassword_WithNonExistentUser_ShouldThrowException() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updatePassword(nonExistentId, "newpassword");
        });

        assertEquals("User not found", exception.getMessage());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void deactivateUser_ShouldSetUserInactive() {
        // Given
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // When
        userService.deactivateUser(testUser.getId());

        // Then
        verify(userRepository).save(testUser);
        assertFalse(testUser.getIsActive());
    }

    @Test
    void activateUser_ShouldSetUserActive() {
        // Given
        testUser.setIsActive(false);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // When
        userService.activateUser(testUser.getId());

        // Then
        verify(userRepository).save(testUser);
        assertTrue(testUser.getIsActive());
    }

    @Test
    void getUsersByRole_ShouldReturnUsersWithRole() {
        // Given
        when(userRepository.findByRoleAndIsActive(User.Role.DEALER, true)).thenReturn(java.util.List.of(testUser));

        // When
        java.util.List<User> result = userService.getUsersByRole(User.Role.DEALER);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getRole(), result.get(0).getRole());
    }

    @Test
    void emailExists_WithExistingEmail_ShouldReturnTrue() {
        // Given
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

        // When
        boolean exists = userService.emailExists(testUser.getEmail());

        // Then
        assertTrue(exists);
    }

    @Test
    void emailExists_WithNonExistentEmail_ShouldReturnFalse() {
        // Given
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        // When
        boolean exists = userService.emailExists("nonexistent@example.com");

        // Then
        assertFalse(exists);
    }

    @Test
    void getUserCountByRole_ShouldReturnCorrectCount() {
        // Given
        when(userRepository.countByRoleAndIsActive(User.Role.DEALER, true)).thenReturn(5L);

        // When
        long count = userService.getUserCountByRole(User.Role.DEALER);

        // Then
        assertEquals(5L, count);
    }
}