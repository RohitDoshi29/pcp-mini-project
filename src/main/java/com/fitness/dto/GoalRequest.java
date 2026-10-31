package com.fitness.dto;

import com.fitness.model.GoalType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Request payload for setting a new fitness goal.
 */
public class GoalRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Goal title is required (e.g. 'Burn 3000 kcal this week')")
    private String title;

    @NotNull(message = "Goal type is required (CALORIES_BURNED, WORKOUT_MINUTES, WORKOUT_COUNT)")
    private GoalType goalType;

    @NotNull(message = "Target value is required")
    @Min(value = 1, message = "Target value must be at least 1")
    private Double targetValue;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    public GoalRequest() {
    }

    public GoalRequest(Long userId, String title, GoalType goalType, Double targetValue, LocalDate startDate, LocalDate endDate) {
        this.userId = userId;
        this.title = title;
        this.goalType = goalType;
        this.targetValue = targetValue;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
}

