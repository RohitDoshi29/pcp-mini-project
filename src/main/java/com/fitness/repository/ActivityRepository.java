package com.fitness.repository;

import com.fitness.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Activity entity.
 * 
 * Spring Boot Concepts:
 * - Method name conventions (like findByUserId) automatically create SQL queries.
 * - @Query allows writing clean JPQL (Java Persistence Query Language) queries.
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // Fetch all activities for a user, newest first
    List<Activity> findByUserIdOrderByActivityDateDescLoggedAtDesc(Long userId);

    // Fetch activities for a user within a specific date range (for weekly/monthly reports)
    List<Activity> findByUserIdAndActivityDateBetweenOrderByActivityDateAsc(Long userId, LocalDate startDate, LocalDate endDate);

    // Sum of calories burned in a date range
    @Query("SELECT COALESCE(SUM(a.caloriesBurned), 0) FROM Activity a WHERE a.user.id = :userId AND a.activityDate BETWEEN :startDate AND :endDate")
    Integer sumCaloriesBurnedBetween(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Sum of workout minutes in a date range
    @Query("SELECT COALESCE(SUM(a.durationMinutes), 0) FROM Activity a WHERE a.user.id = :userId AND a.activityDate BETWEEN :startDate AND :endDate")
    Integer sumDurationMinutesBetween(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Count workouts in a date range
    @Query("SELECT COUNT(a) FROM Activity a WHERE a.user.id = :userId AND a.activityDate BETWEEN :startDate AND :endDate")
    Long countWorkoutsBetween(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

