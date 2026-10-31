package com.fitness.dto;

import com.fitness.model.ActivityType;
import java.time.LocalDate;

public class ActivityRequest {
    public Long userId;
    public ActivityType activityType;
    public Integer durationMinutes;
    public Integer caloriesBurned;
    public Double distanceKm;
    public LocalDate activityDate;
    public String notes;
}
