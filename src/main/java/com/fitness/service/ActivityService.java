package com.fitness.service;

import com.fitness.dto.ActivityRequest;
import com.fitness.exception.ResourceNotFoundException;
import com.fitness.model.Activity;
import com.fitness.model.User;
import com.fitness.repository.ActivityRepository;
import com.fitness.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service class handling logging and viewing daily workout activities.
 */
@Service
@Transactional
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final GoalService goalService;

    public ActivityService(ActivityRepository activityRepository, UserRepository userRepository, GoalService goalService) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
        this.goalService = goalService;
    }

    /**
     * Log a new workout activity and immediately update user's fitness goals.
     */
    public Activity logActivity(ActivityRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        LocalDate date = request.getActivityDate() != null ? request.getActivityDate() : LocalDate.now();

        Activity activity = new Activity(
                user,
                request.getActivityType(),
                request.getDurationMinutes(),
                request.getCaloriesBurned(),
                request.getDistanceKm(),
                date,
                request.getNotes()
        );

        Activity saved = activityRepository.save(activity);

        // Update active goals with the newly logged calories/minutes
        goalService.refreshGoalsForUser(user.getId());

        return saved;
    }

    /**
     * Get all logged activities for a user (most recent first).
     */
    @Transactional(readOnly = true)
    public List<Activity> getUserActivities(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        return activityRepository.findByUserIdOrderByActivityDateDescLoggedAtDesc(userId);
    }

    /**
     * Get activities logged within the last N days.
     */
    @Transactional(readOnly = true)
    public List<Activity> getRecentActivities(Long userId, int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        LocalDate endDate = LocalDate.now();
        return activityRepository.findByUserIdAndActivityDateBetweenOrderByActivityDateAsc(userId, startDate, endDate);
    }

    /**
     * Delete an activity by ID and update goals.
     */
    public void deleteActivity(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found with ID: " + id));

        Long userId = activity.getUser().getId();
        activityRepository.delete(activity);

        // Refresh goal metrics after deletion
        goalService.refreshGoalsForUser(userId);
    }
}

