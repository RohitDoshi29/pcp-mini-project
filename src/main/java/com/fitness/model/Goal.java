package com.fitness.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a fitness goal set by a user.
 * 
 * Goals allow users to challenge themselves and track their progress over time.
 */
@Entity
@Table(name = "goals")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    @NotNull(message = "Goal title/description is required")
    @Column(nullable = false)
    private String title;

    @NotNull(message = "Goal type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalType goalType;

    @NotNull(message = "Target value is required")
    @Min(value = 1, message = "Target value must be greater than 0")
    @Column(nullable = false)
    private Double targetValue;

    // Transient or persisted current progress value (e.g., calories burned so far)
    private Double currentValue = 0.0;

    @NotNull(message = "Start date is required")
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalStatus status = GoalStatus.IN_PROGRESS;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Goal() {
    }

    public Goal(User user, String title, GoalType goalType, Double targetValue, 
                LocalDate startDate, LocalDate endDate) {
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
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public GoalType getGoalType() {
        return goalType;
    }

    public void setGoalType(GoalType goalType) {
        this.goalType = goalType;
    }

    public Double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(Double targetValue) {
        this.targetValue = targetValue;
    }

    public Double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Double currentValue) {
        this.currentValue = currentValue;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Helper to get percentage completed (capped at 100% for display)
     */
    public double getProgressPercentage() {
        if (targetValue == null || targetValue <= 0) {
            return 0.0;
        }
        double current = currentValue != null ? currentValue : 0.0;
        double pct = (current / targetValue) * 100.0;
        return Math.min(Math.round(pct * 10.0) / 10.0, 100.0);
    }
}

