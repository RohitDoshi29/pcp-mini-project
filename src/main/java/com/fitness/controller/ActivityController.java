package com.fitness.controller;

import com.fitness.dto.ActivityRequest;
import com.fitness.model.Activity;
import com.fitness.service.ActivityService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ===================================================================
 * [PRESENTATION POINT] CONTROLLER LAYER: ActivityController
 * ===================================================================
 * - ROLE: Acts as the "Waiter" for workout-related HTTP requests.
 * - CONCEPTS:
 *   1. @RestController: Listens for web requests and returns JSON responses.
 *   2. @RequestMapping("/api/activities"): Base URL path for all endpoints in this file.
 *   3. @CrossOrigin(origins = "*"): Enables CORS so frontend browsers can communicate.
 *   4. Constructor Injection: Injects ActivityService without using outdated @Autowired.
 * ===================================================================
 */
@RestController
@RequestMapping("/api/activities")
@CrossOrigin(origins = "*")
public class ActivityController {

    // Dependency: The Service layer where business logic and goal updates happen
    private final ActivityService activityService;

    // CONSTRUCTOR INJECTION: Spring Boot automatically finds and injects ActivityService bean
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    /**
     * [ENDPOINT 1] POST /api/activities
     * - PURPOSE: Logs a new workout session (running, cycling, swimming, etc.)
     * - FLOW:
     *   1. @RequestBody deserializes JSON text from browser into ActivityRequest object.
     *   2. Delegates to activityService.logActivity() which writes SQL and updates active goals.
     *   3. @ResponseStatus(HttpStatus.CREATED) sends HTTP status code 201 Created.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Activity logActivity(@RequestBody ActivityRequest request) {
        // CODE FLOW: Forward the workout payload to the service layer
        return activityService.logActivity(request);
    }

    /**
     * [ENDPOINT 2] GET /api/activities/user/{userId}
     * - PURPOSE: Retrieves the full workout history for a specific user.
     * - FLOW:
     *   1. @PathVariable extracts the user's ID number from the URL path.
     *   2. Returns a list of activities sorted with the newest sessions first.
     */
    @GetMapping("/user/{userId}")
    public List<Activity> getUserActivities(@PathVariable Long userId) {
        // CODE FLOW: Fetch user history from database via service
        return activityService.getUserActivities(userId);
    }

    /**
     * [ENDPOINT 3] GET /api/activities/user/{userId}/recent?days=7
     * - PURPOSE: Fetches activities logged in the last N days (default is 7 days).
     * - FLOW:
     *   1. @RequestParam reads query parameters (e.g. ?days=14), defaults to 7 if omitted.
     *   2. Passes the days parameter to the service to filter records by date.
     */
    @GetMapping("/user/{userId}/recent")
    public List<Activity> getRecentActivities(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "7") int days) {
        // CODE FLOW: Filter activities within recent timeframe
        return activityService.getRecentActivities(userId, days);
    }

    /**
     * [ENDPOINT 4] DELETE /api/activities/{id}
     * - PURPOSE: Deletes a specific workout record by ID.
     * - FLOW:
     *   1. Removes the workout row from the database.
     *   2. Recalculates goals so progress bars adjust to reflect the deleted calories.
     *   3. Sends HTTP status code 204 No Content.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteActivity(@PathVariable Long id) {
        // CODE FLOW: Delete activity and update goal metrics
        activityService.deleteActivity(id);
    }
}
