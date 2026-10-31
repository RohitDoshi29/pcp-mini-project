package com.fitness.controller;

import com.fitness.dto.ImprovementReportResponse;
import com.fitness.dto.ProgressReportResponse;
import com.fitness.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for generating progress reports and improvement metrics over time.
 */
@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
@Tag(name = "Reports & Analytics", description = "Endpoints for progress summaries and tracking improvements over time")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/weekly/{userId}")
    @Operation(summary = "Generate a 7-day progress report with daily breakdown and totals")
    public ResponseEntity<ProgressReportResponse> getWeeklyReport(@PathVariable Long userId) {
        return ResponseEntity.ok(reportService.generateProgressReport(userId, "WEEKLY"));
    }

    @GetMapping("/monthly/{userId}")
    @Operation(summary = "Generate a 30-day progress report with daily breakdown and totals")
    public ResponseEntity<ProgressReportResponse> getMonthlyReport(@PathVariable Long userId) {
        return ResponseEntity.ok(reportService.generateProgressReport(userId, "MONTHLY"));
    }

    @GetMapping("/improvements/{userId}")
    @Operation(summary = "Track improvements over time (Current Week vs Previous Week comparison)")
    public ResponseEntity<ImprovementReportResponse> getImprovementReport(@PathVariable Long userId) {
        return ResponseEntity.ok(reportService.generateImprovementReport(userId));
    }
}

