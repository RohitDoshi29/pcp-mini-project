package com.fitness.dto;

import com.fitness.model.Goal;
import java.time.LocalDate;
import java.util.*;

public class ProgressReportResponse {
    public Long userId;
    public String userName;
    public String period;
    public LocalDate startDate;
    public LocalDate endDate;

    public long totalWorkouts;
    public int totalDurationMinutes;
    public int totalCaloriesBurned;

    public double averageCaloriesPerWorkout;
    public double averageMinutesPerWorkout;
    public double averageCaloriesPerDay;

    public List<DailySummary> dailyBreakdown = new ArrayList<>();
    public List<Goal> activeGoals = new ArrayList<>();
    public Map<String, Integer> caloriesByType = new HashMap<>();
    public Map<String, Integer> workoutsByType = new HashMap<>();

    public static class DailySummary {
        public LocalDate date;
        public int workoutCount;
        public int totalCalories;
        public int totalMinutes;

        public DailySummary(LocalDate date, int workoutCount, int totalCalories, int totalMinutes) {
            this.date = date;
            this.workoutCount = workoutCount;
            this.totalCalories = totalCalories;
            this.totalMinutes = totalMinutes;
        }
    }
}
