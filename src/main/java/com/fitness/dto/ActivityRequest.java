package com.fitness.dto;

import com.fitness.model.ActivityType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Request payload for logging a workout activity.
 */
public class ActivityRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Activity type is required (e.g. RUNNING, CYCLING, WALKING, YOGA, WEIGHTLIFTING)")
    private ActivityType activityType;

    @NotNull(message = "Duration in minutes is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    @NotNull(message = "Calories burned is required")
    @Min(value = 0, message = "Calories burned must be 0 or more")
    private Integer caloriesBurned;

    private Double distanceKm;

    private LocalDate activityDate;

    private String notes;

    public ActivityRequest() {
    }

    public ActivityRequest(Long userId, ActivityType activityType, Integer durationMinutes, 
                           Integer caloriesBurned, Double distanceKm, LocalDate activityDate, String notes) {
        this.userId = userId;
        this.activityType = activityType;
        this.durationMinutes = durationMinutes;
        this.caloriesBurned = caloriesBurned;
        this.distanceKm = distanceKm;
        this.activityDate = activityDate;
        this.notes = notes;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(Integer caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public LocalDate getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(LocalDate activityDate) {
        this.activityDate = activityDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

