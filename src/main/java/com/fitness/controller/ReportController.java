package com.fitness.controller;

import com.fitness.dto.ImprovementReportResponse;
import com.fitness.dto.ProgressReportResponse;
import com.fitness.service.ReportService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/weekly/{userId}")
    public ProgressReportResponse getWeeklyReport(@PathVariable Long userId) {
        return reportService.generateProgressReport(userId, "WEEKLY");
    }

    @GetMapping("/monthly/{userId}")
    public ProgressReportResponse getMonthlyReport(@PathVariable Long userId) {
        return reportService.generateProgressReport(userId, "MONTHLY");
    }

    @GetMapping("/improvements/{userId}")
    public ImprovementReportResponse getImprovementReport(@PathVariable Long userId) {
        return reportService.generateImprovementReport(userId);
    }
}
