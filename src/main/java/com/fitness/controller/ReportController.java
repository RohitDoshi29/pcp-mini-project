package com.fitness.controller;

import com.fitness.dto.ImprovementReportResponse;
import com.fitness.dto.ProgressReportResponse;
import com.fitness.service.ReportService;
import org.springframework.web.bind.annotation.*;

/**
 * ===================================================================
 * [PRESENTATION POINT] CONTROLLER LAYER: ReportController
 * ===================================================================
 * - ROLE: Delivers Progress Summaries, Pie Chart Data, and Improvement Tracking.
 * - CONCEPTS:
 *   1. REST API design: Organizes reporting under `/api/reports`.
 *   2. Sends aggregated metrics and Chart.js categories as clean JSON.
 * ===================================================================
 */
@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    // Constructor Injection
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * [ENDPOINT 1] GET /api/reports/weekly/{userId}
     * - PURPOSE: Returns a 7-day progress report including:
     *   1. Total calories burned, total active minutes, total workouts.
     *   2. Daily breakdown list (Monday to Sunday).
     *   3. `caloriesByType` and `workoutsByType` maps that feed the Chart.js Pie Charts!
     */
    @GetMapping("/weekly/{userId}")
    public ProgressReportResponse getWeeklyReport(@PathVariable Long userId) {
        // CODE FLOW: Generate 7-day progress summary and pie chart datasets
        return reportService.generateProgressReport(userId, "WEEKLY");
    }

    /**
     * [ENDPOINT 2] GET /api/reports/monthly/{userId}
     * - PURPOSE: Returns a 30-day progress report with monthly totals and pie charts.
     */
    @GetMapping("/monthly/{userId}")
    public ProgressReportResponse getMonthlyReport(@PathVariable Long userId) {
        // CODE FLOW: Generate 30-day progress summary and pie chart datasets
        return reportService.generateProgressReport(userId, "MONTHLY");
    }

    /**
     * [ENDPOINT 3] GET /api/reports/improvements/{userId}
     * - PURPOSE: Tracks user improvement over time!
     * - FLOW:
     *   1. Compares Current 7 Days vs Previous 7 Days.
     *   2. Computes percentage changes (+68.1% calories burned).
     *   3. Evaluates improvement status (EXCELLENT_PROGRESS, STEADY_PROGRESS, NEEDS_BOOST).
     *   4. Generates a personalized motivational coaching feedback message.
     */
    @GetMapping("/improvements/{userId}")
    public ImprovementReportResponse getImprovementReport(@PathVariable Long userId) {
        // CODE FLOW: Calculate week-over-week comparison metrics
        return reportService.generateImprovementReport(userId);
    }
}
