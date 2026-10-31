package com.fitness.controller;

import com.fitness.dto.ActivityRequest;
import com.fitness.model.Activity;
import com.fitness.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for logging workouts and retrieving activity history.
 */
@RestController
@RequestMapping("/api/activities")
@CrossOrigin(origins = "*")
@Tag(name = "Activities", description = "Endpoints to log workouts, calories, and view logs")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping
    @Operation(summary = "Log a new workout activity with calories burned")
    public ResponseEntity<Activity> logActivity(@Valid @RequestBody ActivityRequest request) {
        Activity created = activityService.logActivity(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all logged activities for a user (newest first)")
    public ResponseEntity<List<Activity>> getUserActivities(@PathVariable Long userId) {
        return ResponseEntity.ok(activityService.getUserActivities(userId));
    }

    @GetMapping("/user/{userId}/recent")
    @Operation(summary = "Get recent activities within the last N days (default: 7)")
    public ResponseEntity<List<Activity>> getRecentActivities(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(activityService.getRecentActivities(userId, days));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an activity by ID")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }
}

