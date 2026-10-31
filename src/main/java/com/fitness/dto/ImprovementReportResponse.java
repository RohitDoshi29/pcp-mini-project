package com.fitness.dto;

import java.time.LocalDate;

public class ImprovementReportResponse {
    public Long userId;
    public String userName;

    public LocalDate currentPeriodStart, currentPeriodEnd;
    public LocalDate previousPeriodStart, previousPeriodEnd;

    public long currentWorkouts, previousWorkouts, workoutChange;
    public int currentCalories, previousCalories, calorieChange;
    public int currentMinutes, previousMinutes, minuteChange;

    public double workoutImprovementPct, calorieImprovementPct, minuteImprovementPct;
    public String improvementStatus;
    public String motivationalMessage;
}
