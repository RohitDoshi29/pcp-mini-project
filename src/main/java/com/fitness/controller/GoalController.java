package com.fitness.controller;

import com.fitness.dto.GoalRequest;
import com.fitness.model.Goal;
import com.fitness.model.GoalStatus;
import com.fitness.service.GoalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@CrossOrigin(origins = "*")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Goal createGoal(@RequestBody GoalRequest request) {
        return goalService.createGoal(request);
    }

    @GetMapping("/user/{userId}")
    public List<Goal> getUserGoals(@PathVariable Long userId) {
        return goalService.getUserGoals(userId);
    }

    @PatchMapping("/{id}/status")
    public Goal updateGoalStatus(@PathVariable Long id, @RequestParam GoalStatus status) {
        return goalService.updateGoalStatus(id, status);
    }
}
