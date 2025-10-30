package com.cardealer.service;

import com.cardealer.dto.UserRegistrationDTO;
import com.cardealer.exception.DuplicateResourceException;
import com.cardealer.exception.ResourceNotFoundException;
import com.cardealer.model.Dealer;
import com.cardealer.model.User;
import com.cardealer.model.enums.UserRole;
import com.cardealer.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private DealerService dealerService;
    
    /**
     * Register a new user (buyer or seller)
     */
    public User registerUser(UserRegistrationDTO registrationDTO) {
        log.info("Registering new user with email: {}", registrationDTO.getEmail());
        
        // Check if email already exists
        if (emailExists(registrationDTO.getEmail())) {
            throw new DuplicateResourceException("El email ya está registrado: " + registrationDTO.getEmail());
        }
        
        // Validate password confirmation
        if (!registrationDTO.getPassword().equals(registrationDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }
        
        // Create user entity
        User user = new User();
        user.setName(registrationDTO.getName());
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setPhone(registrationDTO.getPhone());
        user.setRole(UserRole.valueOf(registrationDTO.getRole()));
        user.setEnabled(true);
        
        // Save user
        user = userRepository.save(user);
        log.info("User registered successfully with ID: {}", user.getId());
        
        // If user is a seller, create dealer profile
        if (user.getRole() == UserRole.VENDEDOR) {
            Dealer dealer = new Dealer();
            dealer.setName(registrationDTO.getDealerName());
            dealer.setEmail(registrationDTO.getEmail());
            dealer.setPhone(registrationDTO.getPhone());
            dealer.setAddress(registrationDTO.getDealerAddress());
            dealer.setCity(registrationDTO.getDealerCity());
            dealer.setPostalCode(registrationDTO.getDealerPostalCode());
            dealer.setDescription(registrationDTO.getDealerDescription());
            dealer.setUser(user);
            dealer.setActive(true);
            
            dealerService.createDealer(dealer, user);
            log.info("Dealer profile created for user: {}", user.getEmail());
        }
        
        return user;
    }
    
    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        log.debug("Fetching user by email: {}", email);
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
    }
    
    /**
     * Get user by ID
     */
    public User getUserById(Long id) {
        log.debug("Fetching user by ID: {}", id);
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }
    
    /**
     * Check if email exists
     */
    public Boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * Update user password
     */
    public void updatePassword(Long userId, String newPassword) {
        log.info("Updating password for user ID: {}", userId);
        User user = getUserById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password updated successfully for user ID: {}", userId);
    }
    
    /**
     * Update user profile
     */
    public User updateProfile(Long userId, User userDetails) {
        log.info("Updating profile for user ID: {}", userId);
        User user = getUserById(userId);
        
        user.setName(userDetails.getName());
        user.setPhone(userDetails.getPhone());
        
        // Don't allow email or role changes through this method
        
        user = userRepository.save(user);
        log.info("Profile updated successfully for user ID: {}", userId);
        return user;
    }
}