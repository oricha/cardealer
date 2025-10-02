package com.crashedcarsales.repository;

import com.crashedcarsales.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by email address
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by email and active status
     * @param email the email to search for
     * @param isActive the active status
     * @return Optional containing the user if found and active
     */
    Optional<User> findByEmailAndIsActive(String email, Boolean isActive);

    /**
     * Check if email already exists
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find users by role
     * @param role the role to search for
     * @return List of users with the specified role
     */
    List<User> findByRole(User.Role role);

    /**
     * Find active users by role
     * @param role the role to search for
     * @param isActive the active status
     * @return List of active users with the specified role
     */
    List<User> findByRoleAndIsActive(User.Role role, Boolean isActive);

    /**
     * Count users by role
     * @param role the role to count
     * @return number of users with the specified role
     */
    long countByRole(User.Role role);

    /**
     * Count active users by role
     * @param role the role to count
     * @param isActive the active status
     * @return number of active users with the specified role
     */
    long countByRoleAndIsActive(User.Role role, Boolean isActive);

    /**
     * Find users created after a specific date
     * @param createdAt the date to search after
     * @return List of users created after the specified date
     */
    @Query("SELECT u FROM User u WHERE u.createdAt > :createdAt")
    List<User> findUsersCreatedAfter(@Param("createdAt") java.time.LocalDateTime createdAt);

    /**
     * Find active users created between two dates
     * @param startDate the start date
     * @param endDate the end date
     * @param isActive the active status
     * @return List of active users created between the specified dates
     */
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate AND u.isActive = :isActive")
    List<User> findActiveUsersCreatedBetween(
        @Param("startDate") java.time.LocalDateTime startDate,
        @Param("endDate") java.time.LocalDateTime endDate,
        @Param("isActive") Boolean isActive
    );

    /**
     * Search users by email pattern (case-insensitive)
     * @param emailPattern the email pattern to search for
     * @return List of users matching the email pattern
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :emailPattern, '%'))")
    List<User> findUsersByEmailPattern(@Param("emailPattern") String emailPattern);

    /**
     * Find recently created users
     * @param limit the maximum number of users to return
     * @return List of recently created users
     */
    @Query(value = "SELECT * FROM users ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<User> findRecentlyCreatedUsers(@Param("limit") int limit);
}