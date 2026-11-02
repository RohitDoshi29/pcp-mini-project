package com.fitness.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ===================================================================
 * [PRESENTATION POINT] MODEL / ENTITY LAYER: Goal
 * ===================================================================
 * - ROLE: Represents the `goals` table in the SQL database.
 * - CONCEPTS:
 *   1. Foreign Key Relationship: Each goal belongs to one `User`.
 *   2. State Tracking: Tracks progress with `targetValue`, `currentValue`, and `status`.
 *   3. Enums: Stored as VARCHAR via `@Enumerated(EnumType.STRING)` (GoalType & GoalStatus).
 *   4. Computed Methods: `getProgressPercentage()` calculates dynamic completion percentage for frontend progress bars.
 * ===================================================================
 */
@Entity
@Table(name = "goals")
public class Goal {

    // Primary Key: Unique ID generated automatically by SQL database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign Key: Links each fitness target to a registered user
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    // Title / description (e.g. "Burn 3000 kcal this week")
    @Column(nullable = false)
    private String title;

    // Type of goal: CALORIES_BURNED, WORKOUT_MINUTES, or WORKOUT_COUNT
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalType goalType;

    // The target number to hit (e.g. 3000.0 kcal or 150.0 mins)
    @Column(nullable = false)
    private Double targetValue;

    // Current accumulated progress (starts at 0.0, updated on every workout log)
    private Double currentValue = 0.0;

    // Beginning date of the goal window
    @Column(nullable = false)
    private LocalDate startDate;

    // Deadline date for the goal window
    @Column(nullable = false)
    private LocalDate endDate;

    // Current status: IN_PROGRESS, ACHIEVED, or EXPIRED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalStatus status = GoalStatus.IN_PROGRESS;

    // Timestamp when the goal was created
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Default constructor for JPA/Hibernate reflection
    public Goal() {}

    // Parameterized constructor
    public Goal(User user, String title, GoalType goalType, Double targetValue, LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.title = title;
        this.goalType = goalType;
        this.targetValue = targetValue;
        this.currentValue = 0.0;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = GoalStatus.IN_PROGRESS;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public GoalType getGoalType() { return goalType; }
    public void setGoalType(GoalType goalType) { this.goalType = goalType; }
    public Double getTargetValue() { return targetValue; }
    public void setTargetValue(Double targetValue) { this.targetValue = targetValue; }
    public Double getCurrentValue() { return currentValue; }
    public void setCurrentValue(Double currentValue) { this.currentValue = currentValue; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public GoalStatus getStatus() { return status; }
    public void setStatus(GoalStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * [HELPER METHOD] getProgressPercentage
     * - PURPOSE: Calculates current completion percentage capped at 100.0%.
     * - FORMULA: (currentValue / targetValue) * 100 rounded to 1 decimal place.
     * - USAGE: Used directly by HTML/Bootstrap progress bars in frontend UI.
     */
    public double getProgressPercentage() {
        if (targetValue == null || targetValue <= 0) return 0.0;
        double current = currentValue != null ? currentValue : 0.0;
        return Math.min(Math.round((current / targetValue) * 1000.0) / 10.0, 100.0);
    }
}

