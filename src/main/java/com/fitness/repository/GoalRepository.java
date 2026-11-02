package com.fitness.repository;

import com.fitness.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ===================================================================
 * [PRESENTATION POINT] REPOSITORY LAYER: GoalRepository
 * ===================================================================
 * - ROLE: Communicates directly with the SQL database for Goal records.
 * - CONCEPTS:
 *   1. `JpaRepository<Goal, Long>`: Inherits standard database CRUD operations.
 *   2. Native SQL `@Query`: Uses exact SQL table names (`goals`) and column names (`user_id`, `status`).
 *   3. Filter by Foreign Key (`user_id`): Ensures users only see their own goals.
 * ===================================================================
 */
@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    /**
     * [QUERY 1] findByUserId
     * - PURPOSE: Retrieves all fitness goals set by a specific user, newest first.
     * - SQL: SELECT * FROM goals WHERE user_id = :userId ORDER BY start_date DESC
     */
    @Query(value = "SELECT * FROM goals WHERE user_id = :userId ORDER BY start_date DESC", nativeQuery = true)
    List<Goal> findByUserId(@Param("userId") Long userId);

    /**
     * [QUERY 2] findByUserIdAndStatus
     * - PURPOSE: Retrieves only goals matching a specific status (e.g. IN_PROGRESS, COMPLETED, FAILED).
     * - SQL: SELECT * FROM goals WHERE user_id = :userId AND status = :status
     * - USAGE: Used during activity logging to evaluate only active (IN_PROGRESS) goals.
     */
    @Query(value = "SELECT * FROM goals WHERE user_id = :userId AND status = :status", nativeQuery = true)
    List<Goal> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);
}

