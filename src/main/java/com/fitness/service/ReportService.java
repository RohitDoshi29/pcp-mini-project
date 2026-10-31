package com.fitness.service;

import com.fitness.dto.ImprovementReportResponse;
import com.fitness.dto.ProgressReportResponse;
import com.fitness.dto.ProgressReportResponse.DailySummary;
import com.fitness.exception.ResourceNotFoundException;
import com.fitness.model.Activity;
import com.fitness.model.Goal;
import com.fitness.model.User;
import com.fitness.repository.ActivityRepository;
import com.fitness.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Service class for Progress Reports and Improvement Tracking.
 * 
 * Written using clear, simple Java loops and beginner-friendly logic.
 */
@Service
@Transactional(readOnly = true)
public class ReportService {

    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final GoalService goalService;

    public ReportService(UserRepository userRepository, ActivityRepository activityRepository, GoalService goalService) {
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.goalService = goalService;
    }

    /**
     * Generate a progress report with totals, daily logs, and Pie Chart data.
     * Period can be "WEEKLY" (7 days) or "MONTHLY" (30 days).
     */
    public ProgressReportResponse generateProgressReport(Long userId, String period) {
        // Step 1: Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Step 2: Determine date range
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;
        if ("MONTHLY".equalsIgnoreCase(period)) {
            startDate = endDate.minusDays(29);
            period = "MONTHLY";
        } else {
            startDate = endDate.minusDays(6);
            period = "WEEKLY";
        }

        // Step 3: Fetch activities from the database for this date range
        List<Activity> activities = activityRepository.findByUserIdAndActivityDateBetweenOrderByActivityDateAsc(
                userId, startDate, endDate);

        // Step 4: Simple loop to calculate totals and Pie Chart categories
        int totalCalories = 0;
        int totalMinutes = 0;
        Map<String, Integer> caloriesByType = new HashMap<>();
        Map<String, Integer> workoutsByType = new HashMap<>();

        for (Activity activity : activities) {
            // Add to totals
            totalCalories += activity.getCaloriesBurned();
            totalMinutes += activity.getDurationMinutes();

            // Prepare Pie Chart data: Calories & Workouts grouped by Activity Type
            String typeName = activity.getActivityType().name();

            int currentCaloriesForType = caloriesByType.getOrDefault(typeName, 0);
            caloriesByType.put(typeName, currentCaloriesForType + activity.getCaloriesBurned());

            int currentCountForType = workoutsByType.getOrDefault(typeName, 0);
            workoutsByType.put(typeName, currentCountForType + 1);
        }

        long totalWorkouts = activities.size();

        // Step 5: Calculate averages
        double avgCaloriesPerWorkout = totalWorkouts > 0 ? (double) totalCalories / totalWorkouts : 0.0;
        double avgMinutesPerWorkout = totalWorkouts > 0 ? (double) totalMinutes / totalWorkouts : 0.0;
        long daysCount = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double avgCaloriesPerDay = daysCount > 0 ? (double) totalCalories / daysCount : 0.0;

        // Step 6: Build day-by-day table data
        Map<LocalDate, List<Activity>> activitiesByDate = new HashMap<>();
        for (Activity a : activities) {
            activitiesByDate.computeIfAbsent(a.getActivityDate(), k -> new ArrayList<>()).add(a);
        }

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

        // Step 7: Get active goals
        List<Goal> goals = goalService.getUserGoals(userId);

        // Step 8: Assemble final report
        ProgressReportResponse report = new ProgressReportResponse();
        report.setUserId(user.getId());
        report.setUserName(user.getName());
        report.setPeriod(period);
        report.setStartDate(startDate);
        report.setEndDate(endDate);

        report.setTotalWorkouts(totalWorkouts);
        report.setTotalCaloriesBurned(totalCalories);
        report.setTotalDurationMinutes(totalMinutes);

        report.setAverageCaloriesPerWorkout(Math.round(avgCaloriesPerWorkout * 10.0) / 10.0);
        report.setAverageMinutesPerWorkout(Math.round(avgMinutesPerWorkout * 10.0) / 10.0);
        report.setAverageCaloriesPerDay(Math.round(avgCaloriesPerDay * 10.0) / 10.0);

        report.setDailyBreakdown(dailySummaries);
        report.setActiveGoals(goals);

        // Populate Pie Chart maps
        report.setCaloriesByType(caloriesByType);
        report.setWorkoutsByType(workoutsByType);

        return report;
    }

    /**
     * Compare This Week vs Last Week to track improvements over time.
     */
    public ImprovementReportResponse generateImprovementReport(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        LocalDate today = LocalDate.now();

        // 1. Current Period: Last 7 days
        LocalDate curStart = today.minusDays(6);
        LocalDate curEnd = today;

        // 2. Previous Period: 7 days before that
        LocalDate prevStart = today.minusDays(13);
        LocalDate prevEnd = today.minusDays(7);

        // 3. Fetch data for Current Period
        Long curWorkouts = activityRepository.countWorkoutsBetween(userId, curStart, curEnd);
        Integer curCalories = activityRepository.sumCaloriesBurnedBetween(userId, curStart, curEnd);
        Integer curMinutes = activityRepository.sumDurationMinutesBetween(userId, curStart, curEnd);

        // 4. Fetch data for Previous Period
        Long prevWorkouts = activityRepository.countWorkoutsBetween(userId, prevStart, prevEnd);
        Integer prevCalories = activityRepository.sumCaloriesBurnedBetween(userId, prevStart, prevEnd);
        Integer prevMinutes = activityRepository.sumDurationMinutesBetween(userId, prevStart, prevEnd);

        // Handle nulls safely
        long cw = curWorkouts != null ? curWorkouts : 0L;
        int cc = curCalories != null ? curCalories : 0;
        int cm = curMinutes != null ? curMinutes : 0;

        long pw = prevWorkouts != null ? prevWorkouts : 0L;
        int pc = prevCalories != null ? prevCalories : 0;
        int pm = prevMinutes != null ? prevMinutes : 0;

        // 5. Calculate Differences
        long workoutDiff = cw - pw;
        int calorieDiff = cc - pc;
        int minuteDiff = cm - pm;

        // 6. Calculate Percentage Improvements
        double workoutPct = calculatePctChange(pw, cw);
        double caloriePct = calculatePctChange(pc, cc);
        double minutePct = calculatePctChange(pm, cm);

        // 7. Choose simple status & encouraging message
        String status;
        String message;

        if (calorieDiff > 0 && workoutDiff >= 0) {
            status = "EXCELLENT_PROGRESS";
            message = String.format("🎉 Outstanding progress! You burned %d more calories (+%.1f%%) and logged %d more workout session(s) than last week. Keep up the momentum!",
                    calorieDiff, caloriePct, Math.max(workoutDiff, 0));
        } else if (calorieDiff >= -150 && (cw >= pw || cw >= 3)) {
            status = "STEADY_PROGRESS";
            message = "👍 Good consistency! You maintained your workout routine. Regular consistency is key to fitness!";
        } else {
            status = "NEEDS_BOOST";
            message = String.format("💪 You burned %d fewer calories than last week. Let's get active and hit your goals this week!",
                    Math.abs(calorieDiff));
        }

        // 8. Assemble response
        ImprovementReportResponse report = new ImprovementReportResponse();
        report.setUserId(user.getId());
        report.setUserName(user.getName());
        report.setCurrentPeriodStart(curStart);
        report.setCurrentPeriodEnd(curEnd);
        report.setPreviousPeriodStart(prevStart);
        report.setPreviousPeriodEnd(prevEnd);

        report.setCurrentWorkouts(cw);
        report.setCurrentCalories(cc);
        report.setCurrentMinutes(cm);

        report.setPreviousWorkouts(pw);
        report.setPreviousCalories(pc);
        report.setPreviousMinutes(pm);

        report.setWorkoutChange(workoutDiff);
        report.setCalorieChange(calorieDiff);
        report.setMinuteChange(minuteDiff);

        report.setWorkoutImprovementPct(workoutPct);
        report.setCalorieImprovementPct(caloriePct);
        report.setMinuteImprovementPct(minutePct);

        report.setImprovementStatus(status);
        report.setMotivationalMessage(message);

        return report;
    }

    private double calculatePctChange(double previous, double current) {
        if (previous == 0.0) {
            return current > 0 ? 100.0 : 0.0;
        }
        double change = ((current - previous) / previous) * 100.0;
        return Math.round(change * 10.0) / 10.0;
    }
}
