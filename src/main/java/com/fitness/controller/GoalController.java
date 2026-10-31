package com.fitness.controller;

import com.fitness.dto.GoalRequest;
import com.fitness.model.Goal;
import com.fitness.model.GoalStatus;
import com.fitness.service.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for setting and monitoring user fitness goals.
 */
@RestController
@RequestMapping("/api/goals")
@CrossOrigin(origins = "*")
@Tag(name = "Goals", description = "Endpoints to create and monitor fitness goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    @Operation(summary = "Create a new fitness goal for a user")
    public ResponseEntity<Goal> createGoal(@Valid @RequestBody GoalRequest request) {
        Goal created = goalService.createGoal(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all goals for a user with updated progress percentages")
    public ResponseEntity<List<Goal>> getUserGoals(@PathVariable Long userId) {
        return ResponseEntity.ok(goalService.getUserGoals(userId));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Manually update the status of a goal")
    public ResponseEntity<Goal> updateGoalStatus(
            @PathVariable Long id,
            @RequestParam GoalStatus status) {
        return ResponseEntity.ok(goalService.updateGoalStatus(id, status));
    }
}

