package com.crashedcarsales.service;

import com.crashedcarsales.entity.User;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private User testUser;
    private String testSecret = "dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdG9rZW4tZ2VuZXJhdGlvbi1hbmQtdmFsaWRhdGlvbi10ZXN0aW5n";
    private Long testExpiration = 3600000L; // 1 hour
    private Long testRefreshExpiration = 604800000L; // 7 days

    @BeforeEach
    void setUp() {
        // Set test values using reflection
        ReflectionTestUtils.setField(jwtService, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", testExpiration);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", testRefreshExpiration);

        // Create test user
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashedpassword");
        testUser.setRole(User.Role.DEALER);
        testUser.setIsActive(true);
    }

    @Test
    void generateAccessToken_ShouldCreateValidToken() {
        // When
        String token = jwtService.generateAccessToken(testUser);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Verify token contains correct claims
        assertEquals(testUser.getEmail(), jwtService.extractUsername(token));
        assertEquals(testUser.getId().toString(), jwtService.extractUserId(token));
        assertEquals(testUser.getRole(), jwtService.extractUserRole(token));
    }

    @Test
    void generateRefreshToken_ShouldCreateValidToken() {
        // When
        String token = jwtService.generateRefreshToken(testUser);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Verify token contains correct claims
        assertEquals(testUser.getEmail(), jwtService.extractUsername(token));
        assertEquals(testUser.getId().toString(), jwtService.extractUserId(token));
        assertEquals(testUser.getRole(), jwtService.extractUserRole(token));
    }

    @Test
    void generateTokens_ShouldCreateBothTokens() {
        // When
        Map<String, String> tokens = jwtService.generateTokens(testUser);

        // Then
        assertNotNull(tokens);
        assertTrue(tokens.containsKey("accessToken"));
        assertTrue(tokens.containsKey("refreshToken"));
        assertNotNull(tokens.get("accessToken"));
        assertNotNull(tokens.get("refreshToken"));
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        // Given
        String token = jwtService.generateAccessToken(testUser);

        // When
        String extractedUsername = jwtService.extractUsername(token);

        // Then
        assertEquals(testUser.getEmail(), extractedUsername);
    }

    @Test
    void extractUserId_ShouldReturnCorrectUserId() {
        // Given
        String token = jwtService.generateAccessToken(testUser);

        // When
        UUID extractedUserId = jwtService.extractUserId(token);

        // Then
        assertEquals(testUser.getId(), extractedUserId);
    }

    @Test
    void extractUserRole_ShouldReturnCorrectRole() {
        // Given
        String token = jwtService.generateAccessToken(testUser);

        // When
        User.Role extractedRole = jwtService.extractUserRole(token);

        // Then
        assertEquals(testUser.getRole(), extractedRole);
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Given
        String token = jwtService.generateAccessToken(testUser);
        org.springframework.security.core.userdetails.User userDetails =
            new org.springframework.security.core.userdetails.User(
                testUser.getEmail(),
                testUser.getPasswordHash(),
                new java.util.ArrayList<>()
            );

        // When
        Boolean isValid = jwtService.validateToken(token, userDetails);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        // Given
        String invalidToken = "invalid.jwt.token";
        org.springframework.security.core.userdetails.User userDetails =
            new org.springframework.security.core.userdetails.User(
                testUser.getEmail(),
                testUser.getPasswordHash(),
                new java.util.ArrayList<>()
            );

        // When
        Boolean isValid = jwtService.validateToken(invalidToken, userDetails);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithWrongUsername_ShouldReturnFalse() {
        // Given
        String token = jwtService.generateAccessToken(testUser);
        org.springframework.security.core.userdetails.User userDetails =
            new org.springframework.security.core.userdetails.User(
                "wrong@example.com",
                testUser.getPasswordHash(),
                new java.util.ArrayList<>()
            );

        // When
        Boolean isValid = jwtService.validateToken(token, userDetails);

        // Then
        assertFalse(isValid);
    }

    @Test
    void refreshAccessToken_WithValidRefreshToken_ShouldCreateNewAccessToken() {
        // Given
        String refreshToken = jwtService.generateRefreshToken(testUser);

        // When
        String newAccessToken = jwtService.refreshAccessToken(refreshToken);

        // Then
        assertNotNull(newAccessToken);
        assertFalse(newAccessToken.isEmpty());
        assertNotEquals(refreshToken, newAccessToken);

        // Verify new token has correct claims
        assertEquals(testUser.getEmail(), jwtService.extractUsername(newAccessToken));
        assertEquals(testUser.getId().toString(), jwtService.extractUserId(newAccessToken));
        assertEquals(testUser.getRole(), jwtService.extractUserRole(newAccessToken));
    }

    @Test
    void refreshAccessToken_WithInvalidToken_ShouldThrowException() {
        // Given
        String invalidToken = "invalid.refresh.token";

        // When & Then
        assertThrows(JwtException.class, () -> {
            jwtService.refreshAccessToken(invalidToken);
        });
    }

    @Test
    void refreshAccessToken_WithAccessToken_ShouldThrowException() {
        // Given
        String accessToken = jwtService.generateAccessToken(testUser);

        // When & Then
        assertThrows(JwtException.class, () -> {
            jwtService.refreshAccessToken(accessToken);
        });
    }

    @Test
    void getExpirationTime_ShouldReturnCorrectValue() {
        // When
        Long expirationTime = jwtService.getExpirationTime();

        // Then
        assertEquals(testExpiration, expirationTime);
    }

    @Test
    void getRefreshExpirationTime_ShouldReturnCorrectValue() {
        // When
        Long refreshExpirationTime = jwtService.getRefreshExpirationTime();

        // Then
        assertEquals(testRefreshExpiration, refreshExpirationTime);
    }
}