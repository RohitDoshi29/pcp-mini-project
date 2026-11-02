package com.fitness.controller;

import com.fitness.dto.GoalRequest;
import com.fitness.model.Goal;
import com.fitness.model.GoalStatus;
import com.fitness.service.GoalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ===================================================================
 * [PRESENTATION POINT] CONTROLLER LAYER: GoalController
 * ===================================================================
 * - ROLE: Handles fitness goal creation, tracking, and status updates.
 * - CONCEPTS:
 *   1. @RestController: Serves goal data as JSON.
 *   2. @RequestMapping("/api/goals"): All goal endpoints start with this prefix.
 *   3. @PatchMapping: Standard HTTP method used when updating partial fields (like status).
 * ===================================================================
 */
@RestController
@RequestMapping("/api/goals")
@CrossOrigin(origins = "*")
public class GoalController {

    private final GoalService goalService;

    // Constructor Injection: Spring supplies the GoalService bean automatically
    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    /**
     * [ENDPOINT 1] POST /api/goals
     * - PURPOSE: Creates a new fitness challenge (e.g. "Burn 2000 kcal this week").
     * - FLOW:
     *   1. @RequestBody extracts the goal title, target value, and dates from JSON.
     *   2. GoalService saves the goal and immediately calculates progress against existing logs.
     *   3. Sends HTTP 201 Created with the saved Goal.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Goal createGoal(@RequestBody GoalRequest request) {
        // CODE FLOW: Forward to service to create and evaluate goal
        return goalService.createGoal(request);
    }

    /**
     * [ENDPOINT 2] GET /api/goals/user/{userId}
     * - PURPOSE: Returns all goals for a user with live progress percentages.
     * - FLOW:
     *   1. GoalService runs SQL queries to sum calories/minutes within the goal's date range.
     *   2. Automatically updates status to ACHIEVED if target is reached.
     *   3. Returns updated list of goals to display progress bars in the dashboard.
     */
    @GetMapping("/user/{userId}")
    public List<Goal> getUserGoals(@PathVariable Long userId) {
        // CODE FLOW: Fetch all goals with live calculated progress
        return goalService.getUserGoals(userId);
    }

    /**
     * [ENDPOINT 3] PATCH /api/goals/{id}/status?status=ACHIEVED
     * - PURPOSE: Allows manually updating the status of a goal (IN_PROGRESS, ACHIEVED, EXPIRED).
     * - FLOW:
     *   1. @PathVariable extracts the goal ID.
     *   2. @RequestParam reads the new status enum value.
     */
    @PatchMapping("/{id}/status")
    public Goal updateGoalStatus(@PathVariable Long id, @RequestParam GoalStatus status) {
        // CODE FLOW: Update status field in database
        return goalService.updateGoalStatus(id, status);
    }
}
