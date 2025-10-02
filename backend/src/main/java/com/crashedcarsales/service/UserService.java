package com.crashedcarsales.service;

import com.crashedcarsales.dto.AuthResponse;
import com.crashedcarsales.dto.LoginRequest;
import com.crashedcarsales.dto.RegisterRequest;
import com.crashedcarsales.entity.User;
import com.crashedcarsales.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserService(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      JwtService jwtService,
                      AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Register a new user
     */
    public AuthResponse registerUser(RegisterRequest registerRequest) {
        logger.info("Registering new user with email: {}", registerRequest.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            logger.warn("Registration failed: Email already exists: {}", registerRequest.getEmail());
            throw new RuntimeException("User with this email already exists");
        }

        // Create new user
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(registerRequest.getRole());
        user.setIsActive(true);

        // Save user
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with ID: {}", savedUser.getId());

        // Generate tokens
        return AuthResponse.success(
            jwtService.generateAccessToken(savedUser),
            jwtService.generateRefreshToken(savedUser),
            jwtService.getExpirationTime(),
            savedUser
        );
    }

    /**
     * Authenticate user and generate tokens
     */
    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        logger.info("Authenticating user: {}", loginRequest.getEmail());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );

            // Get user details
            User user = (User) authentication.getPrincipal();

            logger.info("User authenticated successfully: {}", user.getEmail());

            // Generate tokens
            return AuthResponse.success(
                jwtService.generateAccessToken(user),
                jwtService.generateRefreshToken(user),
                jwtService.getExpirationTime(),
                user
            );

        } catch (BadCredentialsException e) {
            logger.warn("Authentication failed for user: {}", loginRequest.getEmail());
            throw new RuntimeException("Invalid email or password");
        }
    }

    /**
     * Refresh access token using refresh token
     */
    public AuthResponse refreshToken(String refreshToken) {
        logger.info("Refreshing access token");

        try {
            // Extract user info from refresh token
            String email = jwtService.extractUsername(refreshToken);
            User user = userRepository.findByEmailAndIsActive(email, true)
                .orElseThrow(() -> new RuntimeException("User not found or inactive"));

            // Generate new access token
            String newAccessToken = jwtService.refreshAccessToken(refreshToken);

            logger.info("Token refreshed successfully for user: {}", email);

            return AuthResponse.success(
                newAccessToken,
                refreshToken, // Keep the same refresh token
                jwtService.getExpirationTime(),
                user
            );

        } catch (Exception e) {
            logger.warn("Token refresh failed: {}", e.getMessage());
            throw new RuntimeException("Invalid refresh token");
        }
    }

    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailAndIsActive(email, true);
    }

    /**
     * Find user by ID
     */
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id)
            .filter(User::getIsActive);
    }

    /**
     * Get all users by role
     */
    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRoleAndIsActive(role, true);
    }

    /**
     * Update user password
     */
    public void updatePassword(UUID userId, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        logger.info("Password updated for user: {}", user.getEmail());
    }

    /**
     * Deactivate user
     */
    public void deactivateUser(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsActive(false);
        userRepository.save(user);

        logger.info("User deactivated: {}", user.getEmail());
    }

    /**
     * Activate user
     */
    public void activateUser(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsActive(true);
        userRepository.save(user);

        logger.info("User activated: {}", user.getEmail());
    }

    /**
     * Load user by username for Spring Security
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndIsActive(email, true)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPasswordHash(),
            new ArrayList<>() // No authorities for now, can be extended later
        );
    }

    /**
     * Check if email exists
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Get user statistics
     */
    public long getUserCountByRole(User.Role role) {
        return userRepository.countByRoleAndIsActive(role, true);
    }
}