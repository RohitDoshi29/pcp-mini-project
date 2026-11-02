package com.fitness.repository;

import com.fitness.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * ===================================================================
 * [PRESENTATION POINT] REPOSITORY LAYER: ActivityRepository
 * ===================================================================
 * - ROLE: Communicates directly with the SQL database for Workout Activity records.
 * - CONCEPTS:
 *   1. `JpaRepository<Activity, Long>`: Inherits standard save, find, and delete methods.
 *   2. Native SQL Aggregations: Uses SQL functions `SUM()`, `COUNT()`, and `COALESCE()`
 *      to calculate totals directly in the database for speed and scalability.
 *   3. `COALESCE(SUM(...), 0)`: SQL function returning 0 instead of NULL when no activities exist.
 *   4. Parameter Binding: `:userId`, `:startDate`, `:endDate` prevent SQL injection attacks.
 * ===================================================================
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    /**
     * [QUERY 1] findByUserIdOrderByActivityDateDescLoggedAtDesc
     * - PURPOSE: Fetches all workout activities for a user, sorted chronologically (newest first).
     * - SQL: SELECT * FROM activities WHERE user_id = :userId ORDER BY activity_date DESC, logged_at DESC
     */
    @Query(value = "SELECT * FROM activities WHERE user_id = :userId ORDER BY activity_date DESC, logged_at DESC", nativeQuery = true)
    List<Activity> findByUserIdOrderByActivityDateDescLoggedAtDesc(@Param("userId") Long userId);

    /**
     * [QUERY 2] findByUserIdAndActivityDateBetweenOrderByActivityDateAsc
     * - PURPOSE: Fetches activities logged within a date range (used for weekly/monthly reports and charts).
     * - SQL: SELECT * FROM activities WHERE user_id = :userId AND activity_date BETWEEN :startDate AND :endDate ORDER BY activity_date ASC
     */
    @Query(value = "SELECT * FROM activities WHERE user_id = :userId AND activity_date BETWEEN :startDate AND :endDate ORDER BY activity_date ASC", nativeQuery = true)
    List<Activity> findByUserIdAndActivityDateBetweenOrderByActivityDateAsc(
            @Param("userId") Long userId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);

    /**
     * [QUERY 3] sumCaloriesBurnedBetween
     * - PURPOSE: Aggregates total calories burned by a user within a date window.
     * - SQL: SELECT COALESCE(SUM(calories_burned), 0) FROM activities ...
     * - CONCEPT: `COALESCE` guarantees an integer 0 if the user has no workouts in that timeframe.
     */
    @Query(value = "SELECT COALESCE(SUM(calories_burned), 0) FROM activities WHERE user_id = :userId AND activity_date BETWEEN :startDate AND :endDate", nativeQuery = true)
    Integer sumCaloriesBurnedBetween(
            @Param("userId") Long userId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);

    /**
     * [QUERY 4] sumDurationMinutesBetween
     * - PURPOSE: Aggregates total workout minutes by a user within a date window.
     * - SQL: SELECT COALESCE(SUM(duration_minutes), 0) FROM activities ...
     */
    @Query(value = "SELECT COALESCE(SUM(duration_minutes), 0) FROM activities WHERE user_id = :userId AND activity_date BETWEEN :startDate AND :endDate", nativeQuery = true)
    Integer sumDurationMinutesBetween(
            @Param("userId") Long userId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);

    /**
     * [QUERY 5] countWorkoutsBetween
     * - PURPOSE: Counts total number of workouts logged within a date window.
     * - SQL: SELECT COUNT(*) FROM activities ...
     */
    @Query(value = "SELECT COUNT(*) FROM activities WHERE user_id = :userId AND activity_date BETWEEN :startDate AND :endDate", nativeQuery = true)
    Long countWorkoutsBetween(
            @Param("userId") Long userId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
}

