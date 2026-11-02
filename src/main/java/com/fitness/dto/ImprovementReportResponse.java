package com.fitness.dto;

import java.time.LocalDate;

/**
 * ===================================================================
 * [PRESENTATION POINT] DTO LAYER: ImprovementReportResponse
 * ===================================================================
 * - ROLE: Data Transfer Object carrying comparative metrics between current week and previous week.
 * - CONCEPTS:
 *   1. Trend Analysis: Encapsulates absolute deltas (workoutChange, calorieChange, minuteChange)
 *      and percentage shifts (+25.0%, -10.0%).
 *   2. Gamification / User Feedback: Delivers `improvementStatus` (e.g. "IMPROVED", "DECLINED")
 *      and dynamic `motivationalMessage` calculated by `ReportService`.
 * ===================================================================
 */
public class ImprovementReportResponse {
    public Long userId;
    public String userName;

    // Date range boundaries for the two compared time windows
    public LocalDate currentPeriodStart, currentPeriodEnd;
    public LocalDate previousPeriodStart, previousPeriodEnd;

    // Comparative raw numbers
    public long currentWorkouts, previousWorkouts, workoutChange;
    public int currentCalories, previousCalories, calorieChange;
    public int currentMinutes, previousMinutes, minuteChange;

    // Computed percentage shifts & motivational feedback
    public double workoutImprovementPct, calorieImprovementPct, minuteImprovementPct;
    public String improvementStatus;        // "IMPROVED", "DECLINED", or "CONSISTENT"
    public String motivationalMessage;      // Motivational encouragement string
}

