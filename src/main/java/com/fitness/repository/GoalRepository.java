package com.fitness.repository;

import com.fitness.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Goal entity using explicit Native SQL queries.
 */
@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    // Native SQL: Fetch all goals for a user
    @Query(value = "SELECT * FROM goals WHERE user_id = :userId ORDER BY start_date DESC", nativeQuery = true)
    List<Goal> findByUserId(@Param("userId") Long userId);

    // Native SQL: Fetch goals filtered by user and status
    @Query(value = "SELECT * FROM goals WHERE user_id = :userId AND status = :status", nativeQuery = true)
    List<Goal> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);
}
