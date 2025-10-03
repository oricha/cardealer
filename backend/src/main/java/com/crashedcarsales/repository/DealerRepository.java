package com.crashedcarsales.repository;

import com.crashedcarsales.entity.Dealer;
import com.crashedcarsales.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DealerRepository extends JpaRepository<Dealer, UUID> {

    /**
     * Find dealer by associated user
     * @param user the user to search for
     * @return Optional containing the dealer if found
     */
    Optional<Dealer> findByUser(User user);

    /**
     * Find dealer by user ID
     * @param userId the user ID to search for
     * @return Optional containing the dealer if found
     */
    Optional<Dealer> findByUserId(UUID userId);

    /**
     * Find dealer by user email
     * @param email the email to search for
     * @return Optional containing the dealer if found
     */
    @Query("SELECT d FROM Dealer d WHERE d.user.email = :email")
    Optional<Dealer> findByUserEmail(@Param("email") String email);

    /**
     * Check if dealer exists for a user
     * @param userId the user ID to check
     * @return true if dealer exists, false otherwise
     */
    boolean existsByUserId(UUID userId);

    /**
     * Find dealers by name pattern (case-insensitive)
     * @param namePattern the name pattern to search for
     * @return List of dealers matching the name pattern
     */
    @Query("SELECT d FROM Dealer d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :namePattern, '%'))")
    List<Dealer> findDealersByNamePattern(@Param("namePattern") String namePattern);

    /**
     * Find recently created dealers
     * @param limit the maximum number of dealers to return
     * @return List of recently created dealers
     */
    @Query(value = "SELECT * FROM dealers ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<Dealer> findRecentlyCreatedDealers(@Param("limit") int limit);

    /**
     * Count total number of dealers
     * @return number of dealers
     */
    long count();

    /**
     * Get dealer statistics including car count and sales data
     * @param dealerId the dealer ID
     * @return Object array with [carCount, soldCount, totalValue]
     */
    @Query(value = """
        SELECT
            COUNT(c.id) as car_count,
            COUNT(s.id) as sold_count,
            COALESCE(SUM(s.sale_price), 0) as total_value
        FROM dealers d
        LEFT JOIN cars c ON d.id = c.dealer_id AND c.is_active = true
        LEFT JOIN sales s ON c.id = s.car_id AND s.status = 'COMPLETED'
        WHERE d.id = :dealerId
        """, nativeQuery = true)
    Object[] getDealerStatistics(@Param("dealerId") UUID dealerId);
}