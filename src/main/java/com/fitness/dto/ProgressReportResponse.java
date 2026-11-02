package com.fitness.dto;

import com.fitness.model.Goal;
import java.time.LocalDate;
import java.util.*;

/**
 * ===================================================================
 * [PRESENTATION POINT] DTO LAYER: ProgressReportResponse
 * ===================================================================
 * - ROLE: Composite Data Transfer Object returned by `ReportController` to feed the entire
 *   frontend dashboard in a single network request.
 * - CONCEPTS:
 *   1. Aggregated Response: Combines user info, high-level summary cards, daily bar chart points,
 *      active goals, and pie-chart category breakdowns into one structured JSON object.
 *   2. Efficiency: Prevents frontend "waterfall" requests (making 5 separate API calls)
 *      by delivering all needed dashboard data in one payload.
 * ===================================================================
 */
public class ProgressReportResponse {
    // User & Period info
    public Long userId;
    public String userName;
    public String period;           // "weekly", "monthly", or "custom"
    public LocalDate startDate;
    public LocalDate endDate;

    // High-Level KPI Summary Metrics (Dashboard Top Cards)
    public long totalWorkouts;
    public int totalDurationMinutes;
    public int totalCaloriesBurned;

    // Statistical Averages
    public double averageCaloriesPerWorkout;
    public double averageMinutesPerWorkout;
    public double averageCaloriesPerDay;

    // Chart feeds & Lists
    public List<DailySummary> dailyBreakdown = new ArrayList<>();  // Daily activity bar/line chart feed
    public List<Goal> activeGoals = new ArrayList<>();             // User's active goals for progress bars
    public Map<String, Integer> caloriesByType = new HashMap<>();  // Calories breakdown by workout type (Pie Chart)
    public Map<String, Integer> workoutsByType = new HashMap<>();  // Workout count by workout type (Pie Chart)

    /**
     * Nested DTO representing day-by-day aggregated metrics for chart visualizations.
     */
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

