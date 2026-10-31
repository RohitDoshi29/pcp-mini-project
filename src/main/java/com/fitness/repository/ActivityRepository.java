package com.fitness.repository;

import com.fitness.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Activity entity using explicit Native SQL queries.
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // Native SQL: Fetch all activities for a user, newest first
    @Query(value = "SELECT * FROM activities WHERE user_id = :userId ORDER BY activity_date DESC, logged_at DESC", nativeQuery = true)
    List<Activity> findByUserIdOrderByActivityDateDescLoggedAtDesc(@Param("userId") Long userId);

    // Native SQL: Fetch activities for a user within a specific date range
    @Query(value = "SELECT * FROM activities WHERE user_id = :userId AND activity_date BETWEEN :startDate AND :endDate ORDER BY activity_date ASC", nativeQuery = true)
    List<Activity> findByUserIdAndActivityDateBetweenOrderByActivityDateAsc(
            @Param("userId") Long userId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);

    // Native SQL: Sum of calories burned in a date range
    @Query(value = "SELECT COALESCE(SUM(calories_burned), 0) FROM activities WHERE user_id = :userId AND activity_date BETWEEN :startDate AND :endDate", nativeQuery = true)
    Integer sumCaloriesBurnedBetween(
            @Param("userId") Long userId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);

    // Native SQL: Sum of workout minutes in a date range
    @Query(value = "SELECT COALESCE(SUM(duration_minutes), 0) FROM activities WHERE user_id = :userId AND activity_date BETWEEN :startDate AND :endDate", nativeQuery = true)
    Integer sumDurationMinutesBetween(
            @Param("userId") Long userId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);

    // Native SQL: Count workouts in a date range
    @Query(value = "SELECT COUNT(*) FROM activities WHERE user_id = :userId AND activity_date BETWEEN :startDate AND :endDate", nativeQuery = true)
    Long countWorkoutsBetween(
            @Param("userId") Long userId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
}
