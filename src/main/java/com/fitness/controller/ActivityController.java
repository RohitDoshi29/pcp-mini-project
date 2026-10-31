package com.fitness.controller;

import com.fitness.dto.ActivityRequest;
import com.fitness.model.Activity;
import com.fitness.service.ActivityService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@CrossOrigin(origins = "*")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Activity logActivity(@RequestBody ActivityRequest request) {
        return activityService.logActivity(request);
    }

    @GetMapping("/user/{userId}")
    public List<Activity> getUserActivities(@PathVariable Long userId) {
        return activityService.getUserActivities(userId);
    }

    @GetMapping("/user/{userId}/recent")
    public List<Activity> getRecentActivities(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "7") int days) {
        return activityService.getRecentActivities(userId, days);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
    }
}
