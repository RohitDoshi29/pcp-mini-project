package com.fitness.dto;

import com.fitness.model.Goal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Detailed progress report for a given time window (e.g., Weekly, Monthly).
 * Includes aggregate metrics, day-by-day logs, and category breakdowns for Pie Charts.
 */
public class ProgressReportResponse {

    private Long userId;
    private String userName;
    private String period; // "WEEKLY" or "MONTHLY"
    private LocalDate startDate;
    private LocalDate endDate;

    // Aggregate statistics
    private long totalWorkouts;
    private int totalDurationMinutes;
    private int totalCaloriesBurned;

    private double averageCaloriesPerWorkout;
    private double averageMinutesPerWorkout;
    private double averageCaloriesPerDay;

    // Day-by-day activity breakdown
    private List<DailySummary> dailyBreakdown = new ArrayList<>();

    // User's active goals during this report
    private List<Goal> activeGoals = new ArrayList<>();

    // Pie Chart Data: Activity Type -> Total Calories Burned
    // Example: {"RUNNING": 670, "CYCLING": 460, "WEIGHTLIFTING": 340}
    private Map<String, Integer> caloriesByType = new HashMap<>();

    // Pie Chart Data: Activity Type -> Number of Sessions
    // Example: {"RUNNING": 2, "CYCLING": 1, "WEIGHTLIFTING": 1}
    private Map<String, Integer> workoutsByType = new HashMap<>();

    public ProgressReportResponse() {
    }

    // Nested class representing one day's numbers
    public static class DailySummary {
        private LocalDate date;
        private int workoutCount;
        private int totalCalories;
        private int totalMinutes;

        public DailySummary(LocalDate date, int workoutCount, int totalCalories, int totalMinutes) {
            this.date = date;
            this.workoutCount = workoutCount;
            this.totalCalories = totalCalories;
            this.totalMinutes = totalMinutes;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public int getWorkoutCount() {
            return workoutCount;
        }

        public void setWorkoutCount(int workoutCount) {
            this.workoutCount = workoutCount;
        }

        public int getTotalCalories() {
            return totalCalories;
        }

        public void setTotalCalories(int totalCalories) {
            this.totalCalories = totalCalories;
        }

        public int getTotalMinutes() {
            return totalMinutes;
        }

        public void setTotalMinutes(int totalMinutes) {
            this.totalMinutes = totalMinutes;
        }
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
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

    public long getTotalWorkouts() {
        return totalWorkouts;
    }

    public void setTotalWorkouts(long totalWorkouts) {
        this.totalWorkouts = totalWorkouts;
    }

    public int getTotalDurationMinutes() {
        return totalDurationMinutes;
    }

    public void setTotalDurationMinutes(int totalDurationMinutes) {
        this.totalDurationMinutes = totalDurationMinutes;
    }

    public int getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }

    public void setTotalCaloriesBurned(int totalCaloriesBurned) {
        this.totalCaloriesBurned = totalCaloriesBurned;
    }

    public double getAverageCaloriesPerWorkout() {
        return averageCaloriesPerWorkout;
    }

    public void setAverageCaloriesPerWorkout(double averageCaloriesPerWorkout) {
        this.averageCaloriesPerWorkout = averageCaloriesPerWorkout;
    }

    public double getAverageMinutesPerWorkout() {
        return averageMinutesPerWorkout;
    }

    public void setAverageMinutesPerWorkout(double averageMinutesPerWorkout) {
        this.averageMinutesPerWorkout = averageMinutesPerWorkout;
    }

    public double getAverageCaloriesPerDay() {
        return averageCaloriesPerDay;
    }

    public void setAverageCaloriesPerDay(double averageCaloriesPerDay) {
        this.averageCaloriesPerDay = averageCaloriesPerDay;
    }

    public List<DailySummary> getDailyBreakdown() {
        return dailyBreakdown;
    }

    public void setDailyBreakdown(List<DailySummary> dailyBreakdown) {
        this.dailyBreakdown = dailyBreakdown;
    }

    public List<Goal> getActiveGoals() {
        return activeGoals;
    }

    public void setActiveGoals(List<Goal> activeGoals) {
        this.activeGoals = activeGoals;
    }

    public Map<String, Integer> getCaloriesByType() {
        return caloriesByType;
    }

    public void setCaloriesByType(Map<String, Integer> caloriesByType) {
        this.caloriesByType = caloriesByType;
    }

    public Map<String, Integer> getWorkoutsByType() {
        return workoutsByType;
    }

    public void setWorkoutsByType(Map<String, Integer> workoutsByType) {
        this.workoutsByType = workoutsByType;
    }
}
