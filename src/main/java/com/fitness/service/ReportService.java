package com.fitness.service;

import com.fitness.dto.ImprovementReportResponse;
import com.fitness.dto.ProgressReportResponse;
import com.fitness.dto.ProgressReportResponse.DailySummary;
import com.fitness.model.Activity;
import com.fitness.model.User;
import com.fitness.repository.ActivityRepository;
import com.fitness.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * ===================================================================
 * [PRESENTATION POINT] SERVICE LAYER: ReportService
 * ===================================================================
 * - ROLE: Generates Progress Reports, Pie Chart Datasets, and Improvement Tracking.
 * - CONCEPTS:
 *   1. Data Aggregation: Calculates sums and averages using clean Java loops.
 *   2. Chart.js Feed: Groups calories and workout counts by sport category into maps.
 *   3. Trend Analysis: Compares This Week vs Last Week to compute percentage gains.
 * ===================================================================
 */
@Service
public class ReportService {

    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final GoalService goalService;

    // Constructor Injection
    public ReportService(UserRepository userRepository, ActivityRepository activityRepository, GoalService goalService) {
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.goalService = goalService;
    }

    /**
     * [FUNCTION 1] generateProgressReport(Long userId, String period)
     * - PURPOSE: Produces weekly (7 days) or monthly (30 days) reports with Pie Chart datasets.
     * - STEP-BY-STEP CODE FLOW:
     *   STEP 1: Verify the user exists.
     *   STEP 2: Set start and end dates (Weekly = last 7 days, Monthly = last 30 days).
     *   STEP 3: Query SQL database to fetch activities in this date window.
     *   STEP 4: Loop through activities to calculate totals and populate:
     *           - `caloriesByType`: maps sport -> calories (e.g. RUNNING -> 360) for Pie Chart!
     *           - `workoutsByType`: maps sport -> sessions (e.g. RUNNING -> 1) for Doughnut Chart!
     *   STEP 5: Compute averages (avg calories/session, avg minutes/session, daily average).
     *   STEP 6: Build day-by-day activity breakdown list.
     *   STEP 7: Attach active goals and return ProgressReportResponse.
     */
    public ProgressReportResponse generateProgressReport(Long userId, String period) {
        // STEP 1: Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // STEP 2: Determine date range
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = "MONTHLY".equalsIgnoreCase(period) ? endDate.minusDays(29) : endDate.minusDays(6);
        period = "MONTHLY".equalsIgnoreCase(period) ? "MONTHLY" : "WEEKLY";

        // STEP 3: Query activities within date range
        List<Activity> activities = activityRepository.findByUserIdAndActivityDateBetweenOrderByActivityDateAsc(userId, startDate, endDate);

        // STEP 4: Aggregate metrics and prepare Pie Chart data
        int totalCalories = 0;
        int totalMinutes = 0;
        Map<String, Integer> caloriesByType = new HashMap<>();
        Map<String, Integer> workoutsByType = new HashMap<>();
        Map<LocalDate, List<Activity>> activitiesByDate = new HashMap<>();

        for (Activity a : activities) {
            totalCalories += a.getCaloriesBurned();
            totalMinutes += a.getDurationMinutes();

            String type = a.getActivityType().name();
            // Group calories by activity type for the Pie Chart
            caloriesByType.put(type, caloriesByType.getOrDefault(type, 0) + a.getCaloriesBurned());
            // Group sessions by activity type for the Doughnut Chart
            workoutsByType.put(type, workoutsByType.getOrDefault(type, 0) + 1);

            activitiesByDate.computeIfAbsent(a.getActivityDate(), k -> new ArrayList<>()).add(a);
        }

        long totalWorkouts = activities.size();
        long daysCount = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        // STEP 5: Build day-by-day table breakdown
        List<DailySummary> dailySummaries = new ArrayList<>();
        LocalDate loopDate = startDate;
        while (!loopDate.isAfter(endDate)) {
            List<Activity> dayList = activitiesByDate.getOrDefault(loopDate, Collections.emptyList());
            int dayCal = 0;
            int dayMin = 0;
            for (Activity a : dayList) {
                dayCal += a.getCaloriesBurned();
                dayMin += a.getDurationMinutes();
            }
            dailySummaries.add(new DailySummary(loopDate, dayList.size(), dayCal, dayMin));
            loopDate = loopDate.plusDays(1);
        }

        // STEP 6: Package into response DTO
        ProgressReportResponse report = new ProgressReportResponse();
        report.userId = user.getId();
        report.userName = user.getName();
        report.period = period;
        report.startDate = startDate;
        report.endDate = endDate;
        report.totalWorkouts = totalWorkouts;
        report.totalCaloriesBurned = totalCalories;
        report.totalDurationMinutes = totalMinutes;
        report.averageCaloriesPerWorkout = totalWorkouts > 0 ? Math.round(((double) totalCalories / totalWorkouts) * 10.0) / 10.0 : 0.0;
        report.averageMinutesPerWorkout = totalWorkouts > 0 ? Math.round(((double) totalMinutes / totalWorkouts) * 10.0) / 10.0 : 0.0;
        report.averageCaloriesPerDay = Math.round(((double) totalCalories / daysCount) * 10.0) / 10.0;
        report.dailyBreakdown = dailySummaries;
        report.activeGoals = goalService.getUserGoals(userId);

        // Feed data for the interactive Chart.js charts
        report.caloriesByType = caloriesByType;
        report.workoutsByType = workoutsByType;

        return report;
    }

    /**
     * [FUNCTION 2] generateImprovementReport(Long userId)
     * - PURPOSE: Tracks whether the user is improving over time!
     * - STEP-BY-STEP CODE FLOW:
     *   STEP 1: Define Current Window (Last 7 days: Day -6 to Day 0).
     *   STEP 2: Define Previous Window (Prior 7 days: Day -13 to Day -7).
     *   STEP 3: Query SQL database for workouts, calories, and minutes in both periods.
     *   STEP 4: Calculate differences (Current - Previous) and percentage improvements.
     *   STEP 5: Evaluate status (EXCELLENT_PROGRESS, STEADY_PROGRESS, NEEDS_BOOST).
     *   STEP 6: Generate friendly motivational coaching message.
     */
    public ImprovementReportResponse generateImprovementReport(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        LocalDate today = LocalDate.now();

        // STEP 1: Current 7 days
        LocalDate curStart = today.minusDays(6);
        LocalDate curEnd = today;

        // STEP 2: Previous 7 days
        LocalDate prevStart = today.minusDays(13);
        LocalDate prevEnd = today.minusDays(7);

        // STEP 3: Run SQL queries for both periods
        Long curW = activityRepository.countWorkoutsBetween(userId, curStart, curEnd);
        Integer curC = activityRepository.sumCaloriesBurnedBetween(userId, curStart, curEnd);
        Integer curM = activityRepository.sumDurationMinutesBetween(userId, curStart, curEnd);

        Long prevW = activityRepository.countWorkoutsBetween(userId, prevStart, prevEnd);
        Integer prevCalories = activityRepository.sumCaloriesBurnedBetween(userId, prevStart, prevEnd);
        Integer prevM = activityRepository.sumDurationMinutesBetween(userId, prevStart, prevEnd);

        long cw = curW != null ? curW : 0L;
        int cc = curC != null ? curC : 0;
        int cm = curM != null ? curM : 0;

        long pw = prevW != null ? prevW : 0L;
        int pc = prevCalories != null ? prevCalories : 0;
        int pm = prevM != null ? prevM : 0;

        // STEP 4: Calculate numerical differences and percentage gains
        long workoutDiff = cw - pw;
        int calorieDiff = cc - pc;
        int minuteDiff = cm - pm;

        double workoutPct = calcPct(pw, cw);
        double caloriePct = calcPct(pc, cc);
        double minutePct = calcPct(pm, cm);

        // STEP 5: Evaluate status and coaching feedback
        String status = (calorieDiff > 0 && workoutDiff >= 0) ? "EXCELLENT_PROGRESS" :
                        (calorieDiff >= -150 && (cw >= pw || cw >= 3)) ? "STEADY_PROGRESS" : "NEEDS_BOOST";

        String message = status.equals("EXCELLENT_PROGRESS")
                ? String.format("🎉 Great job! You burned %d more calories (+%.1f%%) and logged %d more workout session(s) than last week. Keep up the momentum!", calorieDiff, caloriePct, Math.max(workoutDiff, 0))
                : status.equals("STEADY_PROGRESS")
                ? "👍 Good consistency! You maintained your workout routine. Regular consistency is key to fitness!"
                : String.format("💪 You burned %d fewer calories than last week. Let's get active and hit your goals this week!", Math.abs(calorieDiff));

        // STEP 6: Return comparison results
        ImprovementReportResponse report = new ImprovementReportResponse();
        report.userId = user.getId();
        report.userName = user.getName();
        report.currentPeriodStart = curStart;
        report.currentPeriodEnd = curEnd;
        report.previousPeriodStart = prevStart;
        report.previousPeriodEnd = prevEnd;
        report.currentWorkouts = cw;
        report.currentCalories = cc;
        report.currentMinutes = cm;
        report.previousWorkouts = pw;
        report.previousCalories = pc;
        report.previousMinutes = pm;
        report.workoutChange = workoutDiff;
        report.calorieChange = calorieDiff;
        report.minuteChange = minuteDiff;
        report.workoutImprovementPct = workoutPct;
        report.calorieImprovementPct = caloriePct;
        report.minuteImprovementPct = minutePct;
        report.improvementStatus = status;
        report.motivationalMessage = message;

        return report;
    }

    /**
     * [HELPER FUNCTION] calcPct(double prev, double curr)
     * - Formula: ((current - previous) / previous) * 100
     */
    private double calcPct(double prev, double curr) {
        if (prev == 0.0) return curr > 0 ? 100.0 : 0.0;
        return Math.round(((curr - prev) / prev) * 1000.0) / 10.0;
    }
}
