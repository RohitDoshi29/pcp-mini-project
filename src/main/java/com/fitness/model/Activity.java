package com.fitness.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ===================================================================
 * [PRESENTATION POINT] MODEL / ENTITY LAYER: Activity
 * ===================================================================
 * - ROLE: Represents the `activities` table in the SQL database.
 * - CONCEPTS:
 *   1. Relational Foreign Key (`@ManyToOne`): Multiple workouts belong to one single `User`.
 *   2. `@JoinColumn(name = "user_id")`: Sets the foreign key column name in SQL pointing to `users.id`.
 *   3. `@Enumerated(EnumType.STRING)`: Stores enum values (RUNNING, CYCLING, etc.) as clean text in SQL
 *      rather than fragile numeric indices (0, 1, 2) which break if enum order changes.
 *   4. Date & Time types: Uses Java 8 `LocalDate` for the activity date and `LocalDateTime` for the log timestamp.
 * ===================================================================
 */
@Entity
@Table(name = "activities")
public class Activity {

    // Primary Key: Unique ID generated automatically by SQL database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign Key: Links each workout to a specific registered User
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    // Type of workout (RUNNING, WALKING, CYCLING, etc.)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType activityType;

    // Duration in minutes (e.g. 45 min)
    @Column(nullable = false)
    private Integer durationMinutes;

    // Calories burned in kcal (e.g. 350 kcal)
    @Column(nullable = false)
    private Integer caloriesBurned;

    // Distance covered in kilometers (optional, e.g. 5.2 km)
    private Double distanceKm;

    // The date when the workout happened
    @Column(nullable = false)
    private LocalDate activityDate;

    // Optional user notes (e.g. "Morning park run, felt energetic")
    private String notes;

    // Audit timestamp when the activity record was entered into the system
    @Column(nullable = false, updatable = false)
    private LocalDateTime loggedAt = LocalDateTime.now();

    // Default constructor for JPA/Hibernate reflection
    public Activity() {}

    // Parameterized constructor
    public Activity(User user, ActivityType activityType, Integer durationMinutes, 
                    Integer caloriesBurned, Double distanceKm, LocalDate activityDate, String notes) {
        this.user = user;
        this.activityType = activityType;
        this.durationMinutes = durationMinutes;
        this.caloriesBurned = caloriesBurned;
        this.distanceKm = distanceKm;
        this.activityDate = activityDate != null ? activityDate : LocalDate.now();
        this.notes = notes;
        this.loggedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public ActivityType getActivityType() { return activityType; }
    public void setActivityType(ActivityType activityType) { this.activityType = activityType; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public Integer getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(Integer caloriesBurned) { this.caloriesBurned = caloriesBurned; }
    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
    public LocalDate getActivityDate() { return activityDate; }
    public void setActivityDate(LocalDate activityDate) { this.activityDate = activityDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getLoggedAt() { return loggedAt; }
    public void setLoggedAt(LocalDateTime loggedAt) { this.loggedAt = loggedAt; }
}

